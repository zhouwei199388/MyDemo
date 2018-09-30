package zw.com.mydemo.liveData

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.harsom.delemu.utils.Log
import kotlinx.android.synthetic.main.activity_livedata.*
import zw.com.mydemo.R

/**
 * Created by ZouWei on 2018/9/28.
 */
class LiveDataActivity : FragmentActivity() {

    private val mText = "测试数据"
    private var mNumber = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livedata)
        MyViewModel.myTestString.observe(this, Observer<String> { t ->
            if (t != null)
                Log.d(t)
            tv_change.text = t
        })
        supportFragmentManager.beginTransaction().add(R.id.fragment,LiveDataFragment()).commit()
        supportFragmentManager.beginTransaction().add(R.id.fragment,LiveDataFragment2()).commit()
        btn_change.setOnClickListener {
            mNumber++
            MyViewModel.myTestString.value = "$mText$mNumber"
        }
    }
}