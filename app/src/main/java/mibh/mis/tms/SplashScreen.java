package mibh.mis.tms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import mibh.mis.tms.service.Version;

/**
 * Created by ponlakiss on 06/04/2015.
 */

public class SplashScreen extends AppCompatActivity {

    final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Log.d("test", !isOnline() + " " + !isGpsEnable());

        if (!isOnline() || !isGpsEnable()) {
            showAlertDialog(SplashScreen.this, "ไม่สาสมารถเชื่อมต่อเครือข่ายได้", "กรุณาตรวจสอบอินเตอร์เน็ตและ gps");
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new Version(SplashScreen.this);
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alertDialog.show();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean isGpsEnable() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        return true;
    }
}
