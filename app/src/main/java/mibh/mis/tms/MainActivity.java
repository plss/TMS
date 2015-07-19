package mibh.mis.tms;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import mibh.mis.tms.data.FuelData;
import mibh.mis.tms.data.PlanData;
import mibh.mis.tms.data.WorkData;
import mibh.mis.tms.service.CallService;
import mibh.mis.tms.service.UploadPic;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FrameLayout mContentFrame;
    private int mCurrentSelectedPosition;
    private static Timer timer;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        sp = getSharedPreferences("info", Context.MODE_PRIVATE);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mContentFrame = (FrameLayout) findViewById(R.id.nav_contentframe);

        setUpToolbar();
        setUpNavDrawer();

        mCurrentSelectedPosition = (sp.getBoolean("WORK", true)) ? 1 : 0;

        Menu menu = mNavigationView.getMenu();
        menu.getItem(mCurrentSelectedPosition).setChecked(true);
        selectedItem(mCurrentSelectedPosition);

        //new db_tms(MainActivity.this).EmptyAllTable();
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new CheckNotification(), 0, 1000 * 60 * 30);

    }

    class CheckNotification extends TimerTask {
        public void run() {
            String result = new CallService().checkNotify(sp.getString("truckid", ""));
            Log.d("Result Notify", result);
            if (!result.equals("[]") && !result.equals("null")) {
                try {
                    JSONArray data = new JSONArray(result);
                    JSONObject c = data.getJSONObject(0);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification notification = new Notification(R.mipmap.ic_launcher, "มีการแจ้งเตือนไหม่", System.currentTimeMillis());
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    String Title = "ข้อความใหม่";
                    String[] splitDot = c.getString("CO_ITEM").split("\\.");
                    String Message = "มีงานเข้า " + splitDot[0] + " งาน";
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent activity = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
                    notification.setLatestEventInfo(MainActivity.this, Title, Message, activity);
                    //notification.number += 1;
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.defaults = Notification.DEFAULT_SOUND;
                    notification.defaults = Notification.DEFAULT_VIBRATE;
                    notificationManager.notify(0, notification);
                } catch (Exception e) {
                    Log.d("Error creNotify", e.toString());
                }
            }

            new UploadPic(MainActivity.this).execute();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*if (id == R.id.action_settings) {
            return true;
        }*/

        switch (item.getItemId()) {
            case R.id.refresh:
                Toast.makeText(MainActivity.this, "Refresh", Toast.LENGTH_LONG).show();
                new RefreshTask(sp.getString("truckid", ""), sp.getString("empid", "")).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectedItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                mToolbar.setTitle(R.string.menu_plan);
                fragment = new PlanList();
                break;
            case 1:
                mToolbar.setTitle(R.string.menu_work);
                fragment = new WorkList();
                break;
            case 2:
                mToolbar.setTitle(R.string.menu_fuel);
                fragment = new FuelList();
                break;
            case 3:
                mToolbar.setTitle(R.string.menu_doc_work);
                fragment = new DocheaderPage();
                break;
            case 4:
                mToolbar.setTitle(R.string.menu_doc_fuel);
                fragment = new DocfuelPage();
                break;
            case 5:
                AlertDialog.Builder dialogComming = new AlertDialog.Builder(MainActivity.this);
                dialogComming.setMessage("พร้อมใช้เร็วๆนี้ ..");
                dialogComming.setPositiveButton("ตกลง",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialogComming.show();
                //mToolbar.setTitle(R.string.menu_cash);
                break;
            case 6:
            case 7:
                try {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("doublea.mobile.kunna.camlocator");
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.d("Error open photostamp", e.toString());
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
                    builderSingle.setMessage("กรุณาติดตั้ง Photo Stamp");
                    builderSingle.setPositiveButton("ตกลง",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builderSingle.show();
                }
                break;
            case 8:
                try {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.example.app.wmsonmobile");
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.d("Error open photostamp", e.toString());
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
                    builderSingle.setMessage("กรุณาติดตั้ง WMS on mobile");
                    builderSingle.setPositiveButton("ตกลง",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builderSingle.show();
                }
                break;
            case 9:
                logOut();
                Intent mainIntent = new Intent(MainActivity.this, Login.class);
                startActivity(mainIntent);
                break;
            default:
                break;
        }
        if (fragment != null) {
            if (!sp.getBoolean("WORK", true) && (position == 1 || position == 2 || position == 3 || position == 4)) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
                builderSingle.setMessage("คุณยังไม่ได้ยืนยันเปิดงาน");
                builderSingle.setPositiveButton("ตกลง",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Menu menu = mNavigationView.getMenu();
                                menu.getItem(0).setChecked(true);
                                selectedItem(0);
                                dialog.dismiss();
                            }
                        });
                builderSingle.show();
            } else {
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.nav_contentframe, fragment).commit();
                mDrawerLayout.closeDrawers();
            }
        }
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private void setUpNavDrawer() {

        TextView empName = (TextView) findViewById(R.id.empName);
        TextView empID = (TextView) findViewById(R.id.empID);
        TextView truckID = (TextView) findViewById(R.id.truckID);
        empName.setText(sp.getString("firstname", "") + " " + sp.getString("lastname", ""));
        empID.setText("รหัสพนักงาน " + sp.getString("empid", ""));
        truckID.setText("เบอร์รถ " + sp.getString("truckid", ""));

        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_drawer);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.drawer_plan:
                        mCurrentSelectedPosition = 0;
                        break;
                    case R.id.drawer_work:
                        mCurrentSelectedPosition = 1;
                        break;
                    case R.id.drawer_fuel:
                        mCurrentSelectedPosition = 2;
                        break;
                    case R.id.drawer_docwork:
                        mCurrentSelectedPosition = 3;
                        break;
                    case R.id.drawer_docfuel:
                        mCurrentSelectedPosition = 4;
                        break;
                    case R.id.drawer_cash:
                        mCurrentSelectedPosition = 5;
                        break;
                    case R.id.drawer_check:
                        mCurrentSelectedPosition = 6;
                        break;
                    case R.id.drawer_sick:
                        mCurrentSelectedPosition = 7;
                        break;
                    case R.id.drawer_weight:
                        mCurrentSelectedPosition = 8;
                        break;
                    case R.id.drawer_logout:
                        Snackbar.make(mContentFrame, "Log Out", Snackbar.LENGTH_SHORT).show();
                        mCurrentSelectedPosition = 9;
                        break;
                    default:
                        break;
                }
                selectedItem(mCurrentSelectedPosition);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

/*        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            mUserLearnedDrawer = true;
            saveSharedSetting(this, PREF_USER_LEARNED_DRAWER, "true");
        }*/

    }

/*    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TEST","OnStop");

    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TEST","OnDestoy");
        new WorkData().clearWorkData();
        new PlanData().clearPlanData();
        new FuelData().clearFuelData();
        new UploadPic(MainActivity.this).execute();
    }

    private void logOut() {
        /*SharedPreferences sp = getSharedPreferences("info", 0);
        sp.edit().clear().apply();*/
        new WorkData().clearWorkData();
        new PlanData().clearPlanData();
        new FuelData().clearFuelData();
        finish();
    }

    public class RefreshTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private String resultServiceWork, resultServicePlan, resultServiceLogin, resultServiceFuel;
        ArrayList<HashMap<String, String>> resultWork, resultPlan;
        private CallService call = new CallService();
        private PlanData planData = new PlanData();
        private WorkData workData = new WorkData();
        private FuelData fuelData = new FuelData();
        private String truckid, empid;
        private String WOHEADER_DOCID;

        public RefreshTask(String truck, String empid) {
            this.truckid = truck;
            this.empid = empid;
            editor = sp.edit();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            new WorkData().clearWorkData();
            new PlanData().clearPlanData();
            new FuelData().clearFuelData();
            dialog.setMessage("กำลังอัพเดทข้อมูล");
            dialog.setCancelable(false);
            dialog.show();
            //new GetLocation(Login.this);
        }

        @Override
        protected String doInBackground(Void... params) {

            resultServiceLogin = call.checkLogin(truckid, empid);

            if (resultServiceLogin.equalsIgnoreCase(getString(R.string.result_error)) || resultServiceLogin.equalsIgnoreCase(getString(R.string.noresult))) {
                return resultServiceLogin;
            } else if (resultServiceLogin.equalsIgnoreCase("WORK")) {
                resultServiceWork = call.callWork(truckid, empid);
                workData.convert(resultServiceWork);
                resultWork = workData.getWorkData();
                resultServicePlan = call.callPlan(truckid, empid);
                planData.convert(resultServicePlan);
                resultPlan = planData.getPlanData();
                if (resultPlan.get(0).get("PLHEADERDOCID").equalsIgnoreCase("NOPLAN")) {
                    planData.clearPlanData();
                }
                WOHEADER_DOCID = resultWork.get(0).get("WOHEADER_DOCID");
                resultServiceFuel = call.callFuel(WOHEADER_DOCID);
                fuelData.convert(resultServiceFuel, WOHEADER_DOCID);
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
            }
            return resultServiceLogin;
        }

        @Override
        protected void onPostExecute(final String result) {
            dialog.dismiss();
            if (result.equalsIgnoreCase(getString(R.string.result_error))) {
                showAlertDialog(MainActivity.this, "การเชื่อมต่อผิดพลาด", "กรุณาลองใหม่ภายหลัง");
            } else if (result.equalsIgnoreCase(getString(R.string.noresult))) {
                showAlertDialog(MainActivity.this, "ไม่พบข้อมูล", "กรุณาตรวจสอบ เบอร์รถ และ รหัสผนักงาน");
            } else {
                editor.commit();
            }
        }

        @Override
        protected void onCancelled() {
            dialog.dismiss();
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
    }
}
