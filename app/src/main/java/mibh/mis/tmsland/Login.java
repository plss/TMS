package mibh.mis.tmsland;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import mibh.mis.tmsland.data.DriverData;
import mibh.mis.tmsland.data.FuelData;
import mibh.mis.tmsland.data.PlanData;
import mibh.mis.tmsland.data.WorkData;
import mibh.mis.tmsland.database.img_tms;
import mibh.mis.tmsland.service.CallService;
import mibh.mis.tmsland.service.CheckOnline;
import mibh.mis.tmsland.service.GetLocation;
import mibh.mis.tmsland.service.Multifunction;
import mibh.mis.tmsland.service.UploadPic;
import mibh.mis.tmsland.service.Version;

public class Login extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    private View focusView = null;
    private EditText mTruckID;
    private EditText mEmpID;
    private View loginLayout;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ArrayList<img_tms.Image_tms> cursor;
    private img_tms ImgTms;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        if (isPackageInstalled()) dialogFyi();

        sp = getSharedPreferences("info", Context.MODE_PRIVATE);
        editor = sp.edit();

        ImgTms = new img_tms(Login.this);

        loginLayout = findViewById(R.id.layoutLogin);
        mTruckID = (EditText) findViewById(R.id.truckId);
        mEmpID = (EditText) findViewById(R.id.driverempid);
        TextView txtVersion = (TextView) findViewById(R.id.txtVersion);

        //txtVersion.setText("V." + sp.getString("VERSION", "-"));
        txtVersion.setText("V " + getVersionName());

        if (sp.getString("truckid", "") != null) {
            mTruckID.setText(sp.getString("truckid", ""));
            mEmpID.setText(sp.getString("empid", ""));
        }

        Fabric.with(this, new Crashlytics());
        new Version(Login.this);
        new GetLocation(Login.this);

        ImageButton loginBtn = (ImageButton) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        loginLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }
        });
    }

    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mTruckID.setError(null);
        mEmpID.setError(null);

        // Store values at the time of the login attempt.
        String truck = mTruckID.getText().toString().toUpperCase().trim().replaceAll("\\W", "");
        String emp = mEmpID.getText().toString().toUpperCase().trim().replaceAll("\\W", "");

        boolean cancel = false;

        if (TextUtils.isEmpty(truck)) {
            mTruckID.setError("กรุณาระบุเบอร์รถ");
            focusView = mTruckID;
            cancel = true;
        } else if (TextUtils.isEmpty(emp)) {
            mEmpID.setError("กรุณาระบุรหัสพนักงาน");
            focusView = mEmpID;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (new CheckOnline(this).isOnline()) {
                mAuthTask = new UserLoginTask(truck, emp);
                mAuthTask.execute();
            } else {
                if (truck.equals(sp.getString("truckid", "")) && emp.equals(sp.getString("empid", "")) && !sp.getString("empid", "").equals("")) {
                    new UserOfflineTask(truck, emp).execute();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setCancelable(true);
                    builder.setMessage("กรุณาเปิด Internet ก่อนใช้งาน");
                    builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }

            }

        }
    }

    public class UserOfflineTask extends AsyncTask<Void, Void, String> {

        String truckid, empid, work, plan, fuel;
        private PlanData planData = new PlanData();
        private WorkData workData = new WorkData();
        private FuelData fuelData = new FuelData();

        public UserOfflineTask(String truckid, String empid) {
            this.truckid = truckid;
            this.empid = empid;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (sp.getBoolean("WORK", false)) {
                work = readFile("WORK");
                workData.clearWorkData();
                workData.convert(work);
                //plan = readFile("PLAN");
                planData.clearPlanData();
                //planData.convert(plan);
                fuel = readFile("FUEL");
                fuelData.clearFuelData();
                fuelData.convert(fuel, workData.getWorkData().get(0).get("WOHEADER_DOCID"));
            } else {
                plan = readFile("PLAN");
                planData.clearPlanData();
                planData.convert(plan);
                workData.clearWorkData();
                fuel = readFile("FUEL");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            startService();
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(Login.this);
        private final String truckid;
        private final String empid;
        private String resultServiceWork, resultServicePlan, resultServiceLogin, resultServiceFuel;
        ArrayList<HashMap<String, String>> resultWork, resultPlan;
        private CallService call = new CallService();
        private PlanData planData = new PlanData();
        private WorkData workData = new WorkData();
        private FuelData fuelData = new FuelData();
        private DriverData driverData = new DriverData();

        UserLoginTask(String truckid, String empid) {
            this.truckid = truckid;
            this.empid = empid;
            dialog.setMessage("กำลังตรวจสอบข้อมูล");
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                resultServiceLogin = call.checkLogin(truckid, empid);
                Log.d(TAG, "login: " + resultServiceLogin);
                if (resultServiceLogin.equalsIgnoreCase(getString(R.string.result_error)) || resultServiceLogin.equalsIgnoreCase(getString(R.string.noresult))) {
                    return resultServiceLogin;
                } else if (resultServiceLogin.equalsIgnoreCase("WORK")) {
                    resultServiceWork = call.callWork(truckid, empid);
                    Log.d(TAG, "work: " + resultServiceWork);
                    workData.clearWorkData();
                    workData.convert(resultServiceWork);
                    resultWork = workData.getWorkData();
                    resultServicePlan = call.callPlan(truckid, empid);
                    planData.clearPlanData();
                    planData.convert(resultServicePlan);
                    resultPlan = planData.getPlanData();
                    if (resultPlan.get(0).get("PLHEADERDOCID").equalsIgnoreCase("NOPLAN")) {
                        planData.clearPlanData();
                    }
                    resultServiceFuel = call.callFuel(resultWork.get(0).get("WOHEADER_DOCID"));
                    fuelData.clearFuelData();
                    fuelData.convert(resultServiceFuel, resultWork.get(0).get("WOHEADER_DOCID"));
                    sentLog(resultWork.get(0).get("EMP_ID"), resultWork.get(0).get("DRIVER_FIRSTNAME") + " " + resultWork.get(0).get("DRIVER_LASTNAME"), resultWork.get(0).get("TRUCK_ID"), resultWork.get(0).get("WOHEADER_DOCID"));
                    delFile(resultWork.get(0).get("WOHEADER_DOCID"));
                    driverData.convert(new CallService().getDriver(empid, truckid));
                    editor.putBoolean("WORK", true);
                    editor.putString("truckid", resultWork.get(0).get("TRUCK_ID"));
                    editor.putString("empid", driverData.getEMP_ID());
                    editor.putString("firstname", driverData.getF_NAME());
                    editor.putString("lastname", driverData.getL_NAME());
                    editor.putString("tel", driverData.getTEL());
                    editor.putString("lastwork", resultWork.get(0).get("WOHEADER_DOCID"));
                    editor.putString("DOCHEADER_URL", resultWork.get(0).get("DOCHEADER_URL"));
                    editor.putString("DOCFUEL_URL", resultWork.get(0).get("DOCFUEL_URL"));
                    editor.putString("DOCITEM_URL", resultWork.get(0).get("DOCITEM_URL"));

                    writeFile(resultServiceWork, "WORK");
                    writeFile(resultServicePlan, "PLAN");
                    writeFile(resultServiceFuel, "FUEL");
                } else if (resultServiceLogin.equalsIgnoreCase("PLAN")) {
                    resultServicePlan = call.callPlan(truckid, empid);
                    Log.d(TAG, "plan: " + resultServiceWork);
                    planData.clearPlanData();
                    planData.convert(resultServicePlan);
                    resultPlan = planData.getPlanData();
                    sentLog(resultPlan.get(0).get("EMP_ID"), resultPlan.get(0).get("F_NAME") + " " + resultPlan.get(0).get("L_NAME"), resultPlan.get(0).get("PLHEADERTRUCK"), "");
                    driverData.convert(call.getDriver(empid, truckid));
                    editor.putBoolean("WORK", false);
                    editor.putString("truckid", resultPlan.get(0).get("PLHEADERTRUCK"));
                    editor.putString("empid", driverData.getEMP_ID());
                    editor.putString("firstname", driverData.getF_NAME());
                    editor.putString("lastname", driverData.getL_NAME());
                    editor.putString("tel", driverData.getTEL());
                    editor.putString("DOCHEADER_URL", resultPlan.get(0).get("DOCHEADER_URL"));
                    editor.putString("DOCFUEL_URL", resultPlan.get(0).get("DOCFUEL_URL"));
                    editor.putString("DOCITEM_URL", resultPlan.get(0).get("DOCITEM_URL"));
                    if (resultPlan.get(0).get("PLHEADERDOCID").equalsIgnoreCase("NOPLAN")) {
                        planData.clearPlanData();
                    } //else delFile(resultPlan.get(0).get("PLHEADERDOCID"));
                    writeFile(resultServicePlan, "PLAN");
                }

                ArrayList<img_tms.Hashtag_TB_TMS> TbHash = ImgTms.HT_GetDateTbHashtag("TbHashtag");
                String resultHashtag = call.getHashtag(TbHash.size() <= 0 ? "000000000000" : TbHash.get(0).Server_Date);
                String resultWorkList = call.getWorkList(TbHash.size() <= 1 ? "000000000000" : TbHash.get(1).Server_Date);

                ImgTms.UpsertWorkList(resultWorkList);
                ImgTms.UpsertHashtag(resultHashtag);
                return resultServiceLogin;
            } catch (Exception e) {
                Log.d("Error doInBg", e.toString());
                return "error";
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;

            if (!Login.this.isDestroyed() && dialog != null) {
                dialog.dismiss();
            }
            editor.apply();
            Log.d(TAG, "onPostExecute: " + sp.getString("empid", "") + " " + result);
            if (result.equalsIgnoreCase(getString(R.string.result_error))) {
                //showAlertDialog(Login.this, "การเชื่อมต่อผิดพลาด", "กรุณาลองใหม่ภายหลัง");
                new UserOfflineTask(truckid, empid).execute();
            } else if (sp.getString("empid", "").equalsIgnoreCase("")) {
                showAlertDialog(Login.this, "ไม่พบข้อมูล", "กรุณาตรวจสอบเบอร์รถ หรือ รหัสผนักงาน");
            } else {
                editor.putBoolean("notification", true);
                editor.apply();
                startService();
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

    public void writeFile(String Json, String Type) {
        Multifunction fileMng = new Multifunction(Login.this);
        fileMng.mCreateAndSaveFile(Type + ".txt", Json);
    }

    public String readFile(String Type) {
        Multifunction fileMng = new Multifunction(Login.this);
        return fileMng.mReadJsonData(Type + ".txt");
    }

    public void sentLog(String DRIVER_ID, String DRIVER_NAME, String TRUCK_ID, String LAST_WORK) {
        String identifier = null;
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null)
            identifier = tm.getDeviceId();
        if (identifier == null || identifier.length() == 0)
            identifier = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String result = new CallService().saveLog(identifier, DRIVER_ID, DRIVER_NAME, TRUCK_ID, LAST_WORK);
        //Log.d("test", identifier + DRIVER_ID + DRIVER_NAME + TRUCK_ID + LAST_WORK);
        //Log.d("test", "sent Log" + result);
        if (result != null && !result.equals("error")) {
            editor = sp.edit();
            editor.putBoolean("sentlog", true);
            editor.apply();
        }

         /*else if (!sp.getString("empid", "TestUser").equalsIgnoreCase(DRIVER_ID)) {
            String result = new CallService().saveLog(identifier, DRIVER_ID, DRIVER_NAME, TRUCK_ID, LAST_WORK);
            Log.d("test", identifier + DRIVER_ID + DRIVER_NAME + TRUCK_ID + LAST_WORK);
            Log.d("test", "sent Log" + result);
            if (result != null && !result.equals("error")) {
                editor = sp.edit();
                editor.putBoolean("sentlog", true);
                editor.apply();
            }
        }*/
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setNegativeButton("ตกลง",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void delFile(String WOHEADERDOCID) {

        cursor = ImgTms.Img_GetAllInactive();

        try {
            cursor = ImgTms.Img_GetActiveAndWoheader(WOHEADERDOCID);
            String fileName;
            for (int i = 0; i < cursor.size(); i++) {
                if (cursor.get(i).Group_Type.equalsIgnoreCase(img_tms.GTYPE_OTHER) ||
                        cursor.get(i).Group_Type.equalsIgnoreCase(img_tms.GTYPE_REQWORK) ||
                        cursor.get(i).Group_Type.equalsIgnoreCase(img_tms.GTYPE_MAINTENANCE) ||
                        cursor.get(i).Group_Type.equalsIgnoreCase(img_tms.GTYPE_MTNDRIVER)) {
                    Calendar today = Calendar.getInstance();
                    String strThatDay = cursor.get(i).Date_img;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date d = today.getTime();
                    try {
                        d = formatter.parse(strThatDay);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Calendar thatDay = Calendar.getInstance();
                    thatDay.setTime(d);
                    long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();
                    if ((diff / (24 * 60 * 60 * 1000)) < 7) {
                        continue;
                    }
                }

                fileName = cursor.get(i).Filename;
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DCIM/TMS/");
                imagesFolder.mkdirs();
                File output = new File(imagesFolder, fileName);
                if (output.exists()) {
                    boolean deleted = output.delete();
                }
                ImgTms.deleteFilename(fileName);
            }
            ImgTms.close();
        } catch (Exception e) {
            Log.d("Error DelFile", e.toString());
        }

    }

    private void startService() {
        Log.i("SERVICE", "Service created...");
        Intent startServiceIntent = new Intent(Login.this, UploadPic.class);
        PendingIntent startWebServicePendingIntent = PendingIntent.getService(Login.this, 0, startServiceIntent, 0);
        AlarmManager alarmManager = (AlarmManager) Login.this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000 * 30, startWebServicePendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckOnline check = new CheckOnline(this);
        if (!check.isGpsEnable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setCancelable(false);
            builder.setMessage("กรุณาเปิด Internet และ GPS ก่อนใช้งาน");
            builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            builder.show();
        }
    }

    private boolean isPackageInstalled() {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("mibh.mis.tms", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void uninstallOldApp() {
        Uri packageURI = Uri.parse("package:" + "mibh.mis.tms");
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        startActivity(uninstallIntent);
    }

    private void dialogFyi() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        //alert.setTitle(data.get("PRO_NAME").split(" ")[0]);
        View view = getLayoutInflater().inflate(R.layout.dialog_fyi, null);
        alert.setView(view);
        alert.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        /*alert.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });*/
        final AlertDialog alert2 = alert.create();
        alert2.setCancelable(false);
        alert2.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                uninstallOldApp();
            }
        });
        alert2.show();
    }

    public String getVersionName() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception e) {
            return "0.0";
        }
    }
}

