package com.harsom.delemu.album

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.harsom.delemu.AlbumRepository
import com.harsom.delemu.BaseActivity
import com.harsom.delemu.ImageSelector
import com.harsom.delemu.R
import com.harsom.delemu.adapter.AlbumAdapter
import com.harsom.delemu.adapter.FolderAdapter
import com.harsom.delemu.bean.AlbumFolder
import com.harsom.delemu.bean.ImageInfo
import com.harsom.delemu.callback.ImageItemListener
import com.harsom.delemu.callback.InitAlbumCallback
import com.harsom.delemu.callback.onFolderItemListener
import com.harsom.delemu.crop.CropActivity
import com.harsom.delemu.utils.*
import kotlinx.android.synthetic.main.activity_album.*
import kotlinx.android.synthetic.main.layout_appbar.*
import kotlinx.android.synthetic.main.popupwindow_list.view.*
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by ZouWei on 2018/7/18.
 */
class AlbumActivity : BaseActivity() {
    val REQUEST_PERMISSION_READ_STOREAGE = 101
    val REQUEST_PERMISSION_CAMERA = 102
    val REQUEST_IMAGE_DETAIL = 1001
    private lateinit var mRepository: AlbumRepository
    private lateinit var mAdapter: AlbumAdapter

    private lateinit var mTempFile: File//临时拍照文件

    private var mSelectPosition: Int = 0//选中的相册Position

    private var mPopupWindow: PopupWindow? = null
    private lateinit var mRequestManager: RequestManager
    private lateinit var mFolderAdapter: FolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)
        mRepository = AlbumRepository.getInstance()
        //初始化参数
        mRepository.init(this)
        mRequestManager = Glide.with(this)
        initView()
    }

    override fun onResume() {
        super.onResume()
        //检测是否需要添加权限
        checkPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        Glide.get(applicationContext).clearMemory()
        mRepository.cleanCacheAndSelcted()
    }

    private fun initView() {
        preview_tv.setOnClickListener {
            if (mRepository.selectSize > 0) {
                toDetailActivity(-1, 0)
            }
        }
        photo_tv.setOnClickListener {
            showPopupWindow(photo_tv)
        }
        tv_submit.setOnClickListener { selectComplete(mRepository.selectImageInfos, false) }
        mAdapter = AlbumAdapter(mRequestManager, imageItemListener)
        mFolderAdapter = FolderAdapter(mRequestManager, object : onFolderItemListener {
            override fun onFolderItemClick(albumFolder: AlbumFolder, position: Int) {
                mSelectPosition = position
                mAdapter.setData(albumFolder.imageInfos)
                tv_title.text = albumFolder.folderName
                dismissPop()
            }
        })
        initRecyclerView()
        initOriginalCheckBox()
    }

    /**
     * 初始化原图选项框
     */
    private fun initOriginalCheckBox() {
        if (ImageSelector.getConfig().showOriginalCheckBox) {
            cb_original.visibility = View.VISIBLE
            cb_original.isChecked = AppUtils.isOriginal(this, true)
            cb_original.setOnCheckedChangeListener { buttonView, isChecked ->
                buttonView.isChecked = isChecked
                AppUtils.setOriginal(this, isChecked)
            }
        } else {
            cb_original.visibility = View.GONE
        }
    }


    /**
     * 初始化recyclerView
     */
    private fun initRecyclerView() {
        rv_image_grid.layoutManager = GridLayoutManager(this, ImageSelector.getConfig().gridColumns)
        rv_image_grid.adapter = mAdapter
    }

    /**
     * 获取本地图片
     */
    private fun initData() {
        mRepository.initImageRepository(supportLoaderManager, initAlbumCallback)
    }

    /**
     * 检测是否有权限
     */
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
                && ActivityCompat.checkSelfPermission(applicationContext, Manifest
                        .permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest
                    .permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_READ_STOREAGE)
        } else {
            initData()
        }
    }


    /**
     * 权限申请回调
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_READ_STOREAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initData()
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                ToastUtil.showToast(applicationContext, "拍照权限获取失败")
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ImageSelector.REQUEST_OPEN_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    toCropActivity(mTempFile.absolutePath)
                }
            }
            ImageSelector.REQUEST_CROP_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
            }
            REQUEST_IMAGE_DETAIL -> {
                if (resultCode == Activity.RESULT_OK)
                    selectComplete(mRepository.selectImageInfos, false)
            }
        }

    }


    private val imageItemListener = object : ImageItemListener {
        override fun onSelectedImageClick(imageInfo: ImageInfo, maxCount: Int, position: Int) {
            imageInfo.isSelected = true
            mRepository.addSelect(imageInfo)
            showSelectedCount(mRepository.selectSize, maxCount)
        }

        override fun onUnSelectedImageClick(imageInfo: ImageInfo, position: Int) {
            imageInfo.isSelected = false
            mRepository.removeSelect(imageInfo)
            showSelectedCount(mRepository.selectSize, ImageSelector.getConfig().maxCount)
        }

        override fun onCameraItemClick() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    ActivityCompat.requestPermissions(this@AlbumActivity, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
                }
            }
        }

        override fun onImageClick(view: View, realPosition: Int, imageInfo: ImageInfo, selectModel: Int) {
            if (selectModel == ImageSelector.MULTI_MODE) {
                toDetailActivity(mSelectPosition, realPosition)
            } else {
                toCropActivity(imageInfo.path)
            }
        }

    }

    /**
     * 获取本地图片数据回调
     */
    private val initAlbumCallback = object : InitAlbumCallback {

        override fun onDataNoAvaliable() {
            Log.d("没有数据")
        }

        override fun onInitFinish(folders: ArrayList<AlbumFolder>?) {
            Log.d("返回数据")
            mAdapter.setData(folders!![mSelectPosition].imageInfos)
            mFolderAdapter.mAlbumFolders = folders
            mFolderAdapter.notifyDataSetChanged()
            showSelectedCount(mRepository.selectSize, ImageSelector.getConfig().maxCount)
        }
    }


    private fun startCamera() {
        //        // 跳转到系统照相机
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            try {
                mTempFile = FileUtils.createTmpFile(applicationContext)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (mTempFile.exists()) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(applicationContext, mTempFile))
                startActivityForResult(cameraIntent, ImageSelector.REQUEST_OPEN_CAMERA)
            } else {
                ToastUtil.showToast(this, getString(R.string.img_error))
            }
        } else {
            ToastUtil.showToast(this, getString(R.string.msg_no_camera))
        }
    }

    private fun getUriForFile(context: Context, file: File): Uri {
        val uri: Uri
        uri = if (Build.VERSION.SDK_INT >= 24) {
            val fileProviderImage = CommonUtils.getMetaData(applicationContext, "FILE_PROVIDER")
            FileProvider.getUriForFile(context.applicationContext, fileProviderImage, file)
        } else {
            Uri.fromFile(file)
        }
        return uri
    }

    /**
     * 跳转图片预览界面
     */
    fun toDetailActivity(itemPosition: Int, currentPosition: Int) {
        val intent = Intent(this, AlbumDetailActivity::class.java)
        intent.putExtra(AlbumDetailActivity.ARG_IMAGE_LIST, itemPosition)
        intent.putExtra(AlbumDetailActivity.ARG_CURRENT_POSITION, currentPosition)
        startActivityForResult(intent, REQUEST_IMAGE_DETAIL)
    }

    /**
     * 跳转图片裁剪界面
     */
    fun toCropActivity(path: String) {
        val intent = Intent(this, CropActivity::class.java)
        intent.putExtra(CropActivity.ARG_IMAGE_PATH, path)
        startActivityForResult(intent, ImageSelector.REQUEST_CROP_IMAGE)
    }


    /**
     * 返回选中图片
     */
    private fun selectComplete(imageInfos: ArrayList<ImageInfo>, refreshMedia: Boolean) {
        val intent = Intent()
        intent.putParcelableArrayListExtra(ImageSelector.SELECTED_RESULT, imageInfos)
        if (refreshMedia) {
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTempFile)))
        }
        setResult(Activity.RESULT_OK, intent)
        finish()

    }


    /**
     * 显示PopupWindow
     */
    private fun showPopupWindow(view: View) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        if (mPopupWindow == null) initPopupWindow()
        mPopupWindow!!.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1] - mPopupWindow!!.height)
    }

    /**
     * 初始化PopupWindow
     */
    private fun initPopupWindow() {
        mPopupWindow = PopupWindow(applicationContext)
        val view: View = LayoutInflater.from(applicationContext).inflate(R.layout.popupwindow_list, null)
        mPopupWindow!!.contentView = view
        val metrics = DisplayMetrics()
        val windowManager: WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        val width: Int = metrics.widthPixels
        val height: Int = metrics.heightPixels / 100 * 70
        mPopupWindow!!.width = width
        mPopupWindow!!.height = height
        mPopupWindow!!.animationStyle = R.style.mypopwindow_anim_style
        mPopupWindow!!.isClippingEnabled = true
        mPopupWindow!!.isFocusable = true
        mPopupWindow!!.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext, R.color.selector_buttom_bg))
        view.recyclerView_popup.setHasFixedSize(true)
        view.recyclerView_popup.layoutManager = LinearLayoutManager(applicationContext)
        view.recyclerView_popup.adapter = mFolderAdapter
    }

    /**
     *关闭Pop
     */
    private fun dismissPop() {
        if (mPopupWindow != null) {
            mPopupWindow!!.dismiss()
        }
    }
}
