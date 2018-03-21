package com.robotmonsterlabs.ping.utility;

import android.content.Context;
import android.widget.Toast;

import com.robotmonsterlabs.ping.ActivityLogin;

/**
 * Created by joduplessis on 2015/07/14.
 */
public class ToastMessage {
    public void show(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
