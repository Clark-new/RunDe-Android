package com.bokecc.video.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bokecc.video.R;

public class ToastUtils {

    public static void showRedToast(Context context,String text) {
        Toast toast = new Toast(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_red_toast, null);
        TextView textView1 = view.findViewById(R.id.id_toast_txt);
        textView1.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
