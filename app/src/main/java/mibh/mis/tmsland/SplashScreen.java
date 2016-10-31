package mibh.mis.tmsland;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import mibh.mis.tmsland.service.CheckOnline;

/**
 * Created by ponlakiss on 06/04/2015.
 */

public class SplashScreen extends AppCompatActivity {

    final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        CheckOnline check = new CheckOnline(this);

        if (!check.isGpsEnable()) {
            showAlertDialog(SplashScreen.this, "ไม่สาสมารถเชื่อมต่อเครือข่ายได้", "กรุณาตรวจสอบอินเตอร์เน็ตและ gps");
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mainIntent = new Intent(SplashScreen.this, Login.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                    //new Version(SplashScreen.this);
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alertDialog.show();
    }
}
