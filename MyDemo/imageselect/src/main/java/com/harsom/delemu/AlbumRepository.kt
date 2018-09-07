package com.harsom.delemu

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.text.TextUtils
import com.harsom.delemu.bean.AlbumFolder
import com.harsom.delemu.bean.ImageInfo
import com.harsom.delemu.callback.InitAlbumCallback
import com.harsom.delemu.utils.Log
import java.io.File


/**
 * Created by ZouWei on 2018/7/18.
 */
class AlbumRepository private constructor() {
    //lateinit 延迟初始化属性与变量
    lateinit var mPhotoFolderName: String //所有图片的相册名
    lateinit var IMAGE_PROJECTION: Array<String> //相册请求参数集合
    lateinit var mLoader: CursorLoader //图片加载器
    private val IMAGE_LOADER_ID: Int = 1000
    private lateinit var mCacheFolders: MutableMap<String, AlbumFolder>

    private val mSelectImageInfos: ArrayList<ImageInfo> = ArrayList()

    companion object {
        fun getInstance() = Holder.INSTANCE
    }

    private object Holder {
        val INSTANCE = AlbumRepository()
    }

    /**
     * 初始化参数
     */
    fun init(context: Context) {
        mCacheFolders = LinkedHashMap()
        IMAGE_PROJECTION = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media._ID)
        mPhotoFolderName = context.getString(R.string.label_general_folder_name)
        mLoader = CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_PROJECTION, null, null, "${IMAGE_PROJECTION[2]} DESC")
    }


    /**
     * 获取本地图片
     */
    fun initImageRepository(loaderManager: LoaderManager, callback: InitAlbumCallback) {
        if (!mCacheFolders.isEmpty()) {
            callback.onInitFinish(getCacheAlbumFolder())
            return
        }
        loaderManager.initLoader(IMAGE_LOADER_ID, null,
                object : LoaderManager.LoaderCallbacks<Cursor> {
                    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
                        Log.d("onLoadFinished")
                        if (data == null || !data.moveToNext()) return
                        if (data.count <= 0) {
                            callback.onDataNoAvaliable()
                            return
                        }
                        val albumFolders = ArrayList<AlbumFolder>()
                        val generalAlbumFolder = AlbumFolder("allPhoto")
                        val imageList = ArrayList<ImageInfo>()
                        generalAlbumFolder.imageInfos = imageList
                        generalAlbumFolder.folderName = mPhotoFolderName
                        albumFolders.add(generalAlbumFolder)

                        while (data.moveToNext()) {
                            val imageInfo: ImageInfo = createImageInfo(data)
                            if (imageInfo.size!!.toInt() == 0) continue
                            generalAlbumFolder.imageInfos.add(imageInfo)
                            val folderFile: File = File(imageInfo.path).parentFile
                            val path: String = folderFile.absolutePath
                            var albumFolder = getFolderByPath(path, albumFolders)
                            if (albumFolder == null) {
                                albumFolder = AlbumFolder()
                                albumFolder.cover = imageInfo
                                albumFolder.folderName = folderFile.name
                                albumFolder.path = path
                                val imageInfos = ArrayList<ImageInfo>()
                                imageInfos.add(imageInfo)
                                albumFolder.imageInfos = imageInfos
                                albumFolders.add(albumFolder)
                            } else {
                                albumFolder.imageInfos.add(imageInfo)
                            }
                        }
                        Log.d("albumFolders size is ${albumFolders.size}")
                        generalAlbumFolder.cover = generalAlbumFolder.imageInfos[0]
                        callback.onInitFinish(albumFolders)
                        processLoadAlbumFolder(albumFolders)
                    }

                    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
                        Log.d("onCreateLoader")
                        return mLoader
                    }

                    override fun onLoaderReset(loader: Loader<Cursor>) {
                        Log.d("onLoaderReset")
                    }

                }
        )
    }

    /**
     * 获取缓存相册
     */
    private fun getCacheAlbumFolder(): ArrayList<AlbumFolder>? {
        if (mCacheFolders.isEmpty()) return null
        val array = ArrayList<AlbumFolder>()
        array.addAll(mCacheFolders.values)
        return array
    }

    /**
     * 缓存相册
     */
    fun processLoadAlbumFolder(folders: ArrayList<AlbumFolder>) {
        mCacheFolders.clear()
        for (folder: AlbumFolder in folders) {
            mCacheFolders[folder.path!!] = folder
        }
    }

    /**
     * 判断集合中是否有传入key的列   有：返回该数据；无：返回null
     */
    fun getFolderByPath(path: String, albumFolders: ArrayList<AlbumFolder>): AlbumFolder? {
        var albumFolderBean: AlbumFolder? = null
        for (album: AlbumFolder in albumFolders) {
            if (TextUtils.equals(album.path, path))
                albumFolderBean = album
        }
        return albumFolderBean
    }

    /**
     * 根据获取的cursor创建ImageInfo
     */
    fun createImageInfo(cursor: Cursor): ImageInfo {
        val imagePath: String = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
        val displayName: String = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
        val addTime: Long = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))
        val imageSize: Long = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
        val id: Long = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
        val imageInfo = ImageInfo(imagePath, displayName, addTime)
        imageInfo.id = id
        imageInfo.size = imageSize
        val orientation: Int = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION))
        val width: Int
        val height: Int
        if (orientation == 0) {
            width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH))
            height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT))
        } else {
            width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT))
            height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH))
        }
        imageInfo.orientation = orientation
        imageInfo.width = width
        imageInfo.height = height


        return imageInfo
    }

    /**
     * 添加选中图片对象
     */
    fun addSelect(imageInfo: ImageInfo) {
        mSelectImageInfos.add(imageInfo)
    }


    /**
     * 删除选中图片
     */
    fun removeSelect(imageInfo: ImageInfo) {
        mSelectImageInfos.remove(imageInfo)
    }

    /**
     * 选中图片数
     */
    val selectSize: Int
        get() = mSelectImageInfos.size


    /**
     * 获取选中图片集合
     */
    val selectImageInfos: ArrayList<ImageInfo>
        get() = mSelectImageInfos

    /**
     * 清空缓存
     */
    fun cleanCacheAndSelcted() {
        mSelectImageInfos.clear()
        mCacheFolders.clear()
    }

}
