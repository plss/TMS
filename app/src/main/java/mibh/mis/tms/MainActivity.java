package mibh.mis.tms;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import mibh.mis.tms.data.FuelData;
import mibh.mis.tms.data.PlanData;
import mibh.mis.tms.data.WorkData;
import mibh.mis.tms.database.img_tms;
import mibh.mis.tms.qrcode.Content;
import mibh.mis.tms.qrcode.QRCodeEncoder;
import mibh.mis.tms.service.CallService;
import mibh.mis.tms.service.CheckOnline;
import mibh.mis.tms.service.validateLatLng;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FrameLayout mContentFrame;
    private int mCurrentSelectedPosition;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private CheckOnline check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        sp = getSharedPreferences("info", Context.MODE_PRIVATE);
        check = new CheckOnline(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mContentFrame = (FrameLayout) findViewById(R.id.nav_contentframe);

        setUpToolbar();
        setUpNavDrawer();
        //Log.d("TEST Check", Calendar.getInstance().getTimeInMillis() / (24 * 60 * 60 * 1000) + " " + sp.getLong("lastcheckstatus", 0) / (24 * 60 * 60 * 1000));
        if (Calendar.getInstance().getTimeInMillis() / (24 * 60 * 60 * 1000) != sp.getLong("lastcheckstatus", 0) / (24 * 60 * 60 * 1000)) {
            mCurrentSelectedPosition = 5;
            editor = sp.edit();
            editor.putString("statuscheckdriver", "");
            editor.putString("statuschecktruck", "");
            editor.apply();
        } else if (sp.getBoolean("WORK", true)) {
            mCurrentSelectedPosition = 1;
        } else {
            mCurrentSelectedPosition = 0;
        }
        //mCurrentSelectedPosition = (sp.getBoolean("WORK", true)) ? 1 : 0;

        Menu menu = mNavigationView.getMenu();
        menu.getItem(mCurrentSelectedPosition).setChecked(true);
        selectedItem(mCurrentSelectedPosition);

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
                if (check.isOnline()) {
                    Toast.makeText(MainActivity.this, "Refresh", Toast.LENGTH_LONG).show();
                    new RefreshTask(sp.getString("truckid", ""), sp.getString("empid", "")).execute();
                } else {
                    showAlertDialog();
                }

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
            /*case 3:
                mToolbar.setTitle(R.string.menu_doc_work);
                fragment = new DocheaderPage();
                break;
            case 4:
                mToolbar.setTitle(R.string.menu_doc_fuel);
                fragment = new DocfuelPage();
                break;*/
            case 3:
                if (check.isOnline()) {
                    fragment = new Income();
                    mToolbar.setTitle(R.string.menu_cash);
                } else {
                    showAlertDialog();
                }
                break;
            case 4:
                if (check.isOnline()) {
                    fragment = new PaySlip();
                    mToolbar.setTitle(R.string.menu_slip);
                } else {
                    showAlertDialog();
                }
                break;
            case 5:
                if (check.isOnline()) {
                    fragment = new Maintenance();
                    mToolbar.setTitle(R.string.menu_check);
                } else {
                    showAlertDialog();
                }
                break;
            case 6:
                if (validateLatLng.check(sp)) {
                    fragment = new CameraFragment();
                    mToolbar.setTitle(R.string.menu_cam);
                    Intent intent = new Intent(MainActivity.this, CameraMain.class);
                    intent.putExtra("From", img_tms.GTYPE_OTHER);
                    intent.putExtra("WOHEADER_DOCID", sp.getString("lastwork","OTHER"));
                    intent.putExtra("ITEM", "10");
                    intent.putExtra("Type_Img", img_tms.IMG_OTHER);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setMessage("กรุณารอข้อมูลสถานที่สักครู่");
                    alertDialogBuilder.setNegativeButton("ตกลง",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

                break;
            /*case 8:
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
                break;*/
            case 7:
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
            case 8:
                logOut();
                Intent mainIntent = new Intent(MainActivity.this, Login.class);
                startActivity(mainIntent);
                break;
            default:
                break;
        }
        if (fragment != null) {
            if (!sp.getBoolean("WORK", true) && (position == 1 || position == 2)) {
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

        LinearLayout headerFrame = (LinearLayout) findViewById(R.id.headerFrame);
        TextView empName = (TextView) findViewById(R.id.empName);
        TextView empID = (TextView) findViewById(R.id.empID);
        TextView truckID = (TextView) findViewById(R.id.truckID);
        headerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_truckid);
                dialog.setCancelable(true);
                TextView truckidText = (TextView) dialog.findViewById(R.id.truckidText);
                ImageView qrTruck = (ImageView) dialog.findViewById(R.id.qrTruck);
                ImageView barTruck = (ImageView) dialog.findViewById(R.id.barTruck);
                String txtTruck = sp.getString("truckid", "");
                truckidText.setText(txtTruck);
                int qrCodeDimention = 250;
                try {
                    QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(txtTruck, null, Content.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);
                    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                    qrTruck.setImageBitmap(bitmap);
                    qrCodeEncoder = new QRCodeEncoder(txtTruck, null, Content.Type.TEXT, BarcodeFormat.CODE_39.toString(), qrCodeDimention);
                    bitmap = qrCodeEncoder.encodeAsBitmap();
                    barTruck.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.d("Error TuckCode", e.toString());
                }
                dialog.show();
            }
        });
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
                    /*case R.id.drawer_docwork:
                        mCurrentSelectedPosition = 3;
                        break;
                    case R.id.drawer_docfuel:
                        mCurrentSelectedPosition = 4;
                        break;*/
                    case R.id.drawer_cash:
                        mCurrentSelectedPosition = 3;
                        break;
                    case R.id.drawer_slip:
                        mCurrentSelectedPosition = 4;
                        break;
                    case R.id.drawer_check:
                        mCurrentSelectedPosition = 5;
                        break;
                    case R.id.drawer_cam:
                        mCurrentSelectedPosition = 6;
                        break;
                    case R.id.drawer_weight:
                        mCurrentSelectedPosition = 7;
                        break;
                    case R.id.drawer_logout:
                        Snackbar.make(mContentFrame, "Log Out", Snackbar.LENGTH_SHORT).show();
                        mCurrentSelectedPosition = 8;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new WorkData().clearWorkData();
        new PlanData().clearPlanData();
        new FuelData().clearFuelData();
        //new UploadPic(MainActivity.this).execute();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!check.isGpsEnable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);
            builder.setMessage("กรุณาเปิด GPS ก่อนใช้งาน");
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

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setMessage("กรุณาเปิด Internet ก่อนใช้งาน");
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (sp.getBoolean("WORK", true)) {
                    mCurrentSelectedPosition = 1;
                } else {
                    mCurrentSelectedPosition = 0;
                }
                //mCurrentSelectedPosition = (sp.getBoolean("WORK", true)) ? 1 : 0;
                Menu menu = mNavigationView.getMenu();
                menu.getItem(mCurrentSelectedPosition).setChecked(true);
                selectedItem(mCurrentSelectedPosition);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

}
