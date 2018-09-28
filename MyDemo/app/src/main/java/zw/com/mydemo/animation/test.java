package zw.com.mydemo.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.View;

import com.harsom.delemu.utils.BitmapUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZouWei on 2018/9/12.
 */
public class test extends View {
    public static void main(String args[]) {
//        ArcProgress a1 = new ArcProgress(1);
//        ArcProgress a2 = new ArcProgress(5);

        TestBean a1 = new TestBean("sdf");
        TestBean a2 = new TestBean("sdf");
        if (a1 == a2) {
            System.out.println("a1和a2内存地址相同");
        } else {
            System.out.println("a1和a2内存地址不相同");
        }

        Map<String, String> map = new HashMap<String,String>();

        map.put("key", "value");
        System.out.println("a1HashCode=" + a1.hashCode() + "  a2HashCode=" + a2.hashCode());

    }


    public static class TestBean {
        public String name;

        public TestBean(String name) {
            this.name = name;
        }
    }

    public test(Context context) {
        super(context);
    }

    public test(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public test(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


}
