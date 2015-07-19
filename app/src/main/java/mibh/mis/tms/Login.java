package mibh.mis.tms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import mibh.mis.tms.data.FuelData;
import mibh.mis.tms.data.PlanData;
import mibh.mis.tms.data.WorkData;
import mibh.mis.tms.database.img_tms;
import mibh.mis.tms.service.CallService;
import mibh.mis.tms.service.GetLocation;

public class Login extends Activity {

    private UserLoginTask mAuthTask = null;
    private View focusView = null;
    private EditText mTruckID;
    private EditText mEmpID;
    private View loginLayout;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ArrayList<img_tms.Image_tms> cursor;
    private img_tms ImgTms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sp = getSharedPreferences("info", Context.MODE_PRIVATE);
        editor = sp.edit();

        ImgTms = new img_tms(Login.this);

        loginLayout = findViewById(R.id.layoutLogin);
        mTruckID = (EditText) findViewById(R.id.truckId);
        mEmpID = (EditText) findViewById(R.id.driverempid);

        if (sp.getString("truckid", "") != null) {
            mTruckID.setText(sp.getString("truckid", ""));
            mEmpID.setText(sp.getString("empid", ""));
        }

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
        String truck = mTruckID.getText().toString();
        String emp = mEmpID.getText().toString();

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
            mAuthTask = new UserLoginTask(truck, emp);
            mAuthTask.execute();
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
                //resultServiceLogin = "NoPlan";
                if (resultServiceLogin.equalsIgnoreCase(getString(R.string.result_error)) || resultServiceLogin.equalsIgnoreCase(getString(R.string.noresult))) {
                    return resultServiceLogin;
                } else if (resultServiceLogin.equalsIgnoreCase("WORK")) {
                    resultServiceWork = call.callWork(truckid, empid);
                    Log.d("TEST WORK", resultServiceWork);
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
                    editor.putBoolean("WORK", true);
                    editor.putString("truckid", resultWork.get(0).get("TRUCK_ID"));
                    editor.putString("empid", resultWork.get(0).get("EMP_ID"));
                    editor.putString("firstname", resultWork.get(0).get("DRIVER_FIRSTNAME"));
                    editor.putString("lastname", resultWork.get(0).get("DRIVER_LASTNAME"));
                    editor.putString("DOCHEADER_URL", resultWork.get(0).get("DOCHEADER_URL"));
                    editor.putString("DOCFUEL_URL", resultWork.get(0).get("DOCFUEL_URL"));
                    editor.putString("DOCITEM_URL", resultWork.get(0).get("DOCITEM_URL"));
                } else if (resultServiceLogin.equalsIgnoreCase("PLAN")) {
                    resultServicePlan = call.callPlan(truckid, empid);
                    planData.clearPlanData();
                    planData.convert(resultServicePlan);
                    resultPlan = planData.getPlanData();
                    editor.putBoolean("WORK", false);
                    editor.putString("truckid", resultPlan.get(0).get("PLHEADERTRUCK"));
                    editor.putString("empid", resultPlan.get(0).get("EMP_ID"));
                    editor.putString("firstname", resultPlan.get(0).get("F_NAME"));
                    editor.putString("lastname", resultPlan.get(0).get("L_NAME"));
                    editor.putString("DOCHEADER_URL", resultPlan.get(0).get("DOCHEADER_URL"));
                    editor.putString("DOCFUEL_URL", resultPlan.get(0).get("DOCFUEL_URL"));
                    editor.putString("DOCITEM_URL", resultPlan.get(0).get("DOCITEM_URL"));
                    if (resultPlan.get(0).get("PLHEADERDOCID").equalsIgnoreCase("NOPLAN")) {
                        planData.clearPlanData();
                    }
                    else delFile(resultPlan.get(0).get("PLHEADERDOCID"));
                }
                return resultServiceLogin;
            } catch (Exception e) {
                Log.d("Error doInBg", e.toString());
                return "error";
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            dialog.dismiss();

            if (result.equalsIgnoreCase(getString(R.string.result_error))) {
                showAlertDialog(Login.this, "การเชื่อมต่อผิดพลาด", "กรุณาลองใหม่ภายหลัง");
            } else if (result.equalsIgnoreCase(getString(R.string.noresult))) {
                showAlertDialog(Login.this, "ไม่พบข้อมูล", "กรุณาตรวจสอบ เบอร์รถ และ รหัสผนักงาน");
            } else {
                editor.commit();
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void sentLog(String DRIVER_ID, String DRIVER_NAME, String TRUCK_ID, String LAST_WORK) {
        String identifier = null;
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null)
            identifier = tm.getDeviceId();
        if (identifier == null || identifier.length() == 0)
            identifier = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (!sp.contains("sentlog") || !sp.getBoolean("sentlog", false)) {
            String result = new CallService().saveLog(identifier, DRIVER_ID, DRIVER_NAME, TRUCK_ID, LAST_WORK);
            Log.d("test", identifier + DRIVER_ID + DRIVER_NAME + TRUCK_ID + LAST_WORK);
            Log.d("test", "sent Log" + result);
            if (result != null && !result.equals("error")) {
                editor = sp.edit();
                editor.putBoolean("sentlog", true);
                editor.commit();
            }

        } else {
            if (!sp.contains("empid") || !sp.getString("empid", "").equalsIgnoreCase(DRIVER_ID)) {
                String result = new CallService().saveLog(identifier, DRIVER_ID, DRIVER_NAME, TRUCK_ID, LAST_WORK);
                Log.d("test", identifier + DRIVER_ID + DRIVER_NAME + TRUCK_ID + LAST_WORK);
                Log.d("test", "sent Log" + result);
                if (result != null && !result.equals("error")) {
                    editor = sp.edit();
                    editor.putBoolean("sentlog", true);
                    editor.commit();
                }
            }
        }
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void delFile(String WOHEADERDOCID){

        cursor = ImgTms.Img_GetAllInactive();
        for (int i = 0; i < cursor.size(); i++) {
            Log.d("in",cursor.get(i).Filename);
            Log.d("in",cursor.get(i).WorkHid);
        }

        Log.d("in","DELTE");
        try {
            cursor = ImgTms.Img_GetActiveAndWoheader(WOHEADERDOCID);
            Log.d("in","DELTE"+ WOHEADERDOCID);
            String fileName;
            for (int i = 0; i < cursor.size(); i++) {
                fileName = cursor.get(i).Filename;
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DCIM/TMS/");
                imagesFolder.mkdirs();
                File output = new File(imagesFolder, fileName);
                if (output.exists()) {
                    boolean deleted = output.delete();
                    Log.d("testExist " + i, fileName + " " + deleted);
                }
                ImgTms.deleteFilename(fileName);
                Log.d("testDel " + i, fileName);
            }
            ImgTms.close();
            Log.d("in","NOTHING"+ WOHEADERDOCID);
        }catch (Exception e){
            Log.d("Error DelFile",e.toString());
        }

    }

}

