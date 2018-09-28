package com.harsom.delemu.album

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.harsom.delemu.AlbumRepository
import com.harsom.delemu.BaseActivity
import com.harsom.delemu.ImageSelector
import com.harsom.delemu.R
import com.harsom.delemu.album.widget.MyViewPager
import com.harsom.delemu.bean.AlbumConfig
import com.harsom.delemu.bean.AlbumFolder
import com.harsom.delemu.bean.ImageInfo
import com.harsom.delemu.callback.InitAlbumCallback
import com.harsom.delemu.utils.DensityUtil
import com.harsom.delemu.utils.ToastUtil
import kotlinx.android.synthetic.main.fragmetn_image_detail.*
import kotlinx.android.synthetic.main.layout_appbar.*
import uk.co.senab.photoview.PhotoView
import uk.co.senab.photoview.PhotoViewAttacher
import kotlin.collections.ArrayList

/**
 * Created by ZouWei on 2018/8/7.
 */

class AlbumDetailActivity : BaseActivity() {

    companion object {
        val ARG_IMAGE_LIST = "imageInfos"
        val ARG_CURRENT_POSITION = "currentPosition"
    }

    private var isFullscreen = true
    private var mImageInfos = ArrayList<ImageInfo>()
    private var mItemPosition = -1
    private var mCurrentPosition = -1
    private var mCount = 0
    private lateinit var mAlbumConfig: AlbumConfig
    private lateinit var mAdapter: AlbumDetailAdapter
    private lateinit var mRepository: AlbumRepository

//    private lateinit var mViewPager: MyViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_detial)
        mRepository = AlbumRepository.getInstance()
        val position: Int = intent.getIntExtra(ARG_IMAGE_LIST, 0)
        if (position == -1) {
            if (mRepository.selectSize > 0)
                mImageInfos.addAll(mRepository.selectImageInfos)
        } else {
            mRepository.initImageRepository(supportLoaderManager, object : InitAlbumCallback {
                override fun onInitFinish(folders: ArrayList<AlbumFolder>?) {
                    mImageInfos = folders!![position].imageInfos
                }
                override fun onDataNoAvaliable() {
                }
            })
        }
        mItemPosition = intent.getIntExtra("itemPosition", -1)
        mCurrentPosition = intent.getIntExtra(ARG_CURRENT_POSITION, 0)
        mAlbumConfig = ImageSelector.getConfig()
        showSelectedCount(mRepository.selectSize, mAlbumConfig.maxCount)
        initCount()
        if (mItemPosition != -1) showSelectedCount(mItemPosition, mImageInfos.size)
        iv_back.setOnClickListener {
            finish()
        }
        initView()
    }


    override fun onResume() {
        super.onResume()
        updateIndicator()
    }

    override fun onDestroy() {
        super.onDestroy()
        Glide.get(this).clearMemory()
    }

    private fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0以后 动态调整底部选择栏距离底部的高度，如果有虚拟按键的话
            val navHeight: Int = DensityUtil.getNavigationBarHeight(applicationContext, windowManager)
            val params: FrameLayout.LayoutParams = fl_folder.layoutParams as FrameLayout.LayoutParams
            params.setMargins(0, 0, 0, navHeight)
            fl_folder.layoutParams = params
        }

        cb_checkbox.setOnClickListener {
            val position: Int = view_pager.currentItem
            val imageInfo: ImageInfo = mAdapter.getItem(position)

            if (imageInfo.isUploaded) {
                ToastUtil.showToast(this, "图片已经上传过!")
                cb_checkbox.isChecked = false
                return@setOnClickListener
            }
            if (mItemPosition != -1) {
                if (imageInfo.isSelected) {
                    mCount--
                    mImageInfos[position].isSelected = false
                } else {
                    mCount++
                    mImageInfos[position].isSelected = true
                }
                showSelectedCount(mCount, mImageInfos.size)
                return@setOnClickListener
            }
            if (!imageInfo.isSelected) {
                selectedImage(imageInfo)
            } else {
                unSelectedImage(imageInfo)
            }
        }
        tv_submit.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
        mAdapter = AlbumDetailAdapter(Glide.with(this), mImageInfos)
        view_pager.adapter = mAdapter
        view_pager.addOnPageChangeListener(onPageChangeListener)
        cb_checkbox.isChecked = mImageInfos[mCurrentPosition].isSelected
        view_pager.currentItem = mCurrentPosition
    }


    private val onPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            val item = mAdapter.getItem(position)
            cb_checkbox.isChecked = item.isSelected
            updateIndicator()
        }
    }

    private fun selectedImage(imageInfo: ImageInfo) {
        if (mRepository.selectSize > mAlbumConfig.maxCount) {
            ToastUtil.showToast(this, getString(R.string.out_of_limit, mAlbumConfig.maxCount))
            cb_checkbox.isChecked = false
            return
        }
        imageInfo.isSelected = true
        mRepository.addSelect(imageInfo)
        showSelectedCount(mRepository.selectSize, mAlbumConfig.maxCount)
    }

    private fun unSelectedImage(imageInfo: ImageInfo) {
        imageInfo.isSelected = false
        mRepository.removeSelect(imageInfo)
        showSelectedCount(mRepository.selectSize, mAlbumConfig.maxCount)
    }


    fun updateIndicator() {
        tv_title.text = "${view_pager.currentItem + 1}/${mAdapter.count}"
    }

    /**
     * 初始化选中数量
     */
    private fun initCount() {
        for (imageInfo in mImageInfos) {
            if (imageInfo.isSelected) mCount++
        }
    }



    private fun isFullscreen() {
        app_bar.visibility = if (isFullscreen) View.GONE else View.VISIBLE
        if (isFullscreen) {
            isFullscreen = false
            fl_folder.visibility = View.GONE
        } else {
            isFullscreen = true
            fl_folder.visibility = View.VISIBLE
        }
    }

    inner class AlbumDetailAdapter(val requestManager: RequestManager, private val mData: ArrayList<ImageInfo>) : PagerAdapter() {
        private var mAttacher: PhotoViewAttacher? = null
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return mData.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View?)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            val photoView: PhotoView = LayoutInflater.from(container.context)
                    .inflate(R.layout.item_image_detail, container, false) as PhotoView
            mAttacher = PhotoViewAttacher(photoView)
            mAttacher!!.setOnPhotoTapListener(PhotoTapListener())
            requestManager
                    .load(mData[position].path)
                    .asBitmap()
                    .fitCenter()
                    .thumbnail(0.2f)
                    .into(photoView)
            container.addView(photoView)
            return photoView
        }

        fun getItem(position: Int): ImageInfo {
            return mData[position]
        }
    }

    private inner class PhotoTapListener : PhotoViewAttacher.OnPhotoTapListener {

        override fun onPhotoTap(view: View, x: Float, y: Float) {
            isFullscreen()
        }

        override fun onOutsidePhotoTap() {}
    }

}