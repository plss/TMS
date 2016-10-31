package mibh.mis.tmsland;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;

import mibh.mis.tmsland.data.WorkData;
import mibh.mis.tmsland.service.CallService;

/**
 * Created by ponlakiss on 06/07/2016.
 */
public class DetailActivity extends AppCompatActivity {

    private HashMap<String, String> data;
    private TextView tvSource, tvDest,
            tvWoHeader, tvDateStart, tvTimeStart, tvDateAlive, tvTimeAlive,
            tvProduct, tvWeightIn, tvWeightOut, tvWeightNet,
            tvPlanStart, tvCustomer, tvDistance, tvHeadLicense, tvTailLicense, tvRemark, tvHeadId, tvTailId;
    private FrameLayout btnCloseItem;
    private int weightNet, miles;
    private String dateIn, dateOut, doNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);
        initInstances();
        setUpToolbar();

    }

    private void initInstances() {

        int position = getIntent().getExtras().getInt("position");
        data = new WorkData().getWorkData().get(position);

        tvSource = (TextView) findViewById(R.id.tvSource);
        tvDest = (TextView) findViewById(R.id.tvDest);
        tvWoHeader = (TextView) findViewById(R.id.tvWoHeader);
        tvDateStart = (TextView) findViewById(R.id.tvDateStart);
        tvTimeStart = (TextView) findViewById(R.id.tvTimeStart);
        tvDateAlive = (TextView) findViewById(R.id.tvDateAlive);
        tvTimeAlive = (TextView) findViewById(R.id.tvTimeAlive);
        tvProduct = (TextView) findViewById(R.id.tvProduct);
        tvWeightIn = (TextView) findViewById(R.id.tvWeightIn);
        tvWeightOut = (TextView) findViewById(R.id.tvWeightOut);
        tvWeightNet = (TextView) findViewById(R.id.tvWeightNet);
        tvPlanStart = (TextView) findViewById(R.id.tvPlanStart);
        tvCustomer = (TextView) findViewById(R.id.tvCustomer);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvHeadLicense = (TextView) findViewById(R.id.tvHeadLicense);
        tvHeadId = (TextView) findViewById(R.id.tvHeadId);
        tvTailLicense = (TextView) findViewById(R.id.tvTailLicense);
        tvTailId = (TextView) findViewById(R.id.tvTailId);
        tvRemark = (TextView) findViewById(R.id.tvRemark);
        btnCloseItem = (FrameLayout) findViewById(R.id.btnCloseItem);

        tvSource.setText(data.get("SOURCE_NAME"));
        tvDest.setText(data.get("DEST_NAME"));
        tvWoHeader.setText(data.get("WOHEADER_DOCID"));
        String DT[] = data.get("PLANSTARTSOURCE").split("T");
        if(DT.length < 2){
            tvDateStart.setText("0");
            tvTimeStart.setText("0 น.");
        }else {
            tvDateStart.setText(DT[0]);
            tvTimeStart.setText(DT[1].substring(0, 5) + " น.");
        }
        DT = data.get("PLANARRIVEDEST").split("T");
        if(DT.length < 2){
            tvDateAlive.setText("0");
            tvTimeAlive.setText("0 น.");
        }else {
            tvDateAlive.setText(DT[0]);
            tvTimeAlive.setText(DT[1].substring(0, 5) + " น.");
        }
        String productSt = data.get("PRO_NAME");
        tvProduct.setText(Html.fromHtml(productSt.replace("ลง", "<font color='red'>ลง</font>").replace("ขึ้น", "<font color='blue'>ขึ้น</font>").replace("\n", "<br>")));
        tvWeightIn.setText(data.get("AMTPRODUCT_TF").equals("True") ? "" : "ไม่ต้องใส่น้ำหนัก");
        tvWeightOut.setText(data.get("AMTPRODUCT_TF").equals("True") ? "" : "ไม่ต้องใส่น้ำหนัก");
        tvWeightNet.setText(data.get("AMTPRODUCT_TF").equals("True") ? "" : "ไม่ต้องใส่น้ำหนัก");
        DT = data.get("WOHEADER_OPEN").split("T");
        if(DT.length < 2){
            tvPlanStart.setText("0 / 0");
        }else {
            tvPlanStart.setText(DT[0] + " / " + DT[1].substring(0, 5));
        }
        tvCustomer.setText(data.get("CUSTOMER_NAME"));
        tvDistance.setText(data.get("DISTANCE_PLAN") + " กม.");
        tvHeadId.setText(data.get("TRUCK_ID"));
        tvHeadLicense.setText(data.get("TRUCK_LICENSE") + " " + data.get("TRUCK_LICENSE_PROVINCE"));
        tvTailId.setText(data.get("TAIL_ID"));
        tvTailLicense.setText(data.get("TAIL_LICENSE") + " " + data.get("TAIL_LICENSE_PROVINCE"));
        tvRemark.setText(data.get("Remark_ProductDetail"));

        btnCloseItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCloseItem.setEnabled(false);
                new GetWeight().execute();
                /*if (data.get("AMTPRODUCT_TF").equals("True")) {

                } else {
                    weightNet = 0;
                    dateIn = "";
                    dateOut = "";
                    doNo = "";
                    showCloseItemDialog("NONE");
                }*/
            }
        });

    }

    public void showCloseItemDialog(final String strWeightNet, String strMiles) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //alert.setTitle(data.get("PRO_NAME").split(" ")[0]);
        View view = getLayoutInflater().inflate(R.layout.dialog_close_work, null);
        TextView tvProductClose = (TextView) view.findViewById(R.id.tvProductClose);
        tvProductClose.setText(data.get("PRO_FULLNAME"));
        CheckBox cb = (CheckBox) view.findViewById(R.id.cbCloseWrok);
        final EditText etMileClose = (EditText) view.findViewById(R.id.etMileClose);
        final EditText etWeightInput = (EditText) view.findViewById(R.id.etWeightInput);
        alert.setView(view);
        alert.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                if (data.get("AMTPRODUCT_TF").equals("True")) {
                    try {
                        weightNet = Integer.parseInt(etWeightInput.getText().toString());
                    } catch (NumberFormatException ex) {
                        weightNet = 0;
                    }
                }
                String milesTemp = etMileClose.getText().toString();
                miles = milesTemp.equals("") ? 0 : Integer.parseInt(milesTemp);
                new CloseItem().execute();
            }
        });
        alert.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert2 = alert.create();
        alert2.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                btnCloseItem.setEnabled(true);
            }
        });
        alert2.show();
        ((AlertDialog) alert2).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((AlertDialog) alert2).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    ((AlertDialog) alert2).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
        if (!data.get("AMTPRODUCT_TF").equals("True")) {
            etWeightInput.setText("สินค้าไม่ต้องใส่น้ำหนัก");
            etWeightInput.setEnabled(false);
        } else {
            etWeightInput.setText(strWeightNet);
        }
        etMileClose.setText(strMiles);
    }

    private void setUpToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("รายละเอียดเส้นทาง " + data.get("WOITEM_DOCID"));
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
            /*case R.id.signature:
                Intent cam = new Intent(ImageList.this, Signature_Pad.class);
                startActivity(cam);
                break;*/
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private class CloseItem extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog = new ProgressDialog(DetailActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("กรุณารอสักครู่");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase("true")) {
                progressDialog.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailActivity.this);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setMessage("ปิดงานเสร็จสิ้น");
                alertDialogBuilder.setNegativeButton("ตกลง",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                progressDialog.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailActivity.this);
                alertDialogBuilder.setMessage("ปิดงานไม่สำเร็จ\nกรุณาลองใหม่อีกครั้ง");
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
        protected String doInBackground(Void... params) {
            String resultService = new CallService().saveCloseEachWorkItem(data.get("WOHEADER_DOCID"), data.get("WOITEM_DOCID"), data.get("TRUCK_ID"), String.valueOf(weightNet), String.valueOf(miles), dateIn, dateOut, doNo);
            return resultService;
        }
    }

    private class GetWeight extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(DetailActivity.this);

        @Override
        protected String doInBackground(Void... params) {

            String resultWeight = new CallService().getWeight(data.get("TRUCK_ID"), data.get("WOHEADER_OPEN"));
            String resultMiles = new CallService().getMileClose(data.get("WOHEADER_DOCID"), data.get("WOITEM_DOCID"), data.get("TRUCK_ID"));

            try {
                JSONObject c = new JSONObject(resultWeight);
                weightNet = (int) c.getDouble("Receive_weightnet");
                dateIn = c.getString("Receive_datein");
                dateOut = c.getString("Receive_dateout");
                doNo = c.getString("DO");
            } catch (JSONException e) {
                weightNet = 0;
                dateIn = "";
                dateOut = "";
                doNo = "";
            }

            try {
                JSONArray jsonArr = new JSONArray(resultMiles);
                JSONObject c = jsonArr.getJSONObject(0);
                miles = (int) c.getDouble("MILE_CLOSE");
            } catch (JSONException e) {
                miles = 0;
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("ดาวโหลดข้อมูลน้ำหนักและเลขไมล์");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            showCloseItemDialog(String.valueOf(weightNet), String.valueOf(miles));
        }
    }

}
