package zw.com.mydemo.socket

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.harsom.delemu.utils.Log
import kotlinx.android.synthetic.main.activity_socket.*
import zw.com.mydemo.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Created by ZouWei on 2018/9/27.
 */
class SocketActivity : AppCompatActivity() {
    private val prot = 9999
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket)
        init()
    }

    private fun init() {
        btn_send.setOnClickListener {
            object : Thread() {
                override fun run() {
                    try {
                        val socket = Socket()
                        socket.connect(InetSocketAddress("0.0.0.0", prot), 2000)
                        val br = BufferedReader(InputStreamReader(socket.getInputStream(), "utf-8") as Reader?)
                        val line = br.readLine()
                        Log.d("读取数据：$line")
                        br.close()
                        socket.close()
                    } catch (e: Exception) {
                        Log.d("sdfsdf")
                        e.printStackTrace()
                    }
                }
            }.start()
        }
    }

}