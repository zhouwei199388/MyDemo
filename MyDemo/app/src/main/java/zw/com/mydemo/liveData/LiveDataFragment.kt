package zw.com.mydemo.liveData

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_livedata.*
import zw.com.mydemo.R

/**
 * Created by ZouWei on 2018/9/29.
 */
class LiveDataFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyViewModel.myTestString.observe(this, Observer<String> { testString ->
            tv_change.text = testString
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_livedata, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}