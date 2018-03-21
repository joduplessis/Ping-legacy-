package com.robotmonsterlabs.ping.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Wake it
        PowerManager powermanager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = powermanager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wakelock.acquire();

        // Put here YOUR code.
        HashMap<String, String> hashMap = (HashMap<String, String>)intent.getSerializableExtra("ping");
        Toast.makeText(context, "Load alarm for "+hashMap.get("title"), Toast.LENGTH_LONG).show();

        wakelock.release();

    }

    public void setAlarm(Context context, HashMap<String,String> ping) {

        // Get our alarm manager
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Intent
        Intent i = new Intent(context, Alarm.class);
        i.putExtra("ping", ping);

        // Our pending intent
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        // Set the thing
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context) {
        // Cancel any alarms
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
