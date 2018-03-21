package com.robotmonsterlabs.ping.utility;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by joduplessis on 2015/07/14.
 */
public class GetKeyHash {

    public void GetKeyHash(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.robotmonsterlabs.ping",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("PIING KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("PIING KeyHash:", "NameNotFoundException"+e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("PIING KeyHash:", "NoSuchAlgorithmException"+e.toString());
        }

    }
}
