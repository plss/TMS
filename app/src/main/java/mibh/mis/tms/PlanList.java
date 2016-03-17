package mibh.mis.tms;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import mibh.mis.tms.data.PlanData;
import mibh.mis.tms.service.CallService;

/**
 * Created by ponlakiss on 06/07/2015.
 */
public class PlanList extends Fragment {

    RecyclerView recyclerView;
    PlanItem adapter;
    RecyclerView mainRecyclerView;
    CardView listItem;
    View rootView;
    RelativeLayout rejectPlan, acceptPlan;
    LinearLayout btnPlan;
    private ArrayList<HashMap<String, String>> data = new ArrayList<>();
    public Dialog dialog;
    private TextView acceptText, WOHEADER_OPEN, COMPANY_NAME, SOURCE_NAME, DEST_NAME, DISTANCE_PLAN, PRO_NAME, CUSTOMER_NAME, detailwoitem, WOHEADER_DOCID, remark;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.plan_list, container, false);

        sp = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);

        mainRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        listItem = (CardView) rootView.findViewById(R.id.cardlist_item);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.home_recyclerview);
        rejectPlan = (RelativeLayout) rootView.findViewById(R.id.rejectPlan);
        acceptPlan = (RelativeLayout) rootView.findViewById(R.id.acceptPlan);
        btnPlan = (LinearLayout) rootView.findViewById(R.id.btnPlan);
        acceptText = (TextView) rootView.findViewById(R.id.acceptTxt);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new PlanData().getPlanData();

        TextView txtNoPlan = (TextView) rootView.findViewById(R.id.txtNoPlan);
        if (data.size() == 0 || data == null) {
            txtNoPlan.setText("ไม่มีแผนงาน");
            btnPlan.setVisibility(View.GONE);
        } else {
            txtNoPlan.setVisibility(View.INVISIBLE);
            btnPlan.setVisibility(View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new PlanItem(getActivity(), data);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        adapter.SetOnItemClickListener(new PlanItem.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                initDialogDetail();
                showDialogDetail(position);
            }
        });


        acceptText.setText(sp.getBoolean("WORK", true) ? "ตอบรับแผน" : "รับงาน");

        acceptPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sp.getBoolean("WORK", true)){
                    showDialogRemark("Accept");
                }else{
                    showDialog();
                }

            }
        });

        rejectPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRemark("Reject");
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_test, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void initDialogDetail() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_detail_plan);
        dialog.setCancelable(true);
        detailwoitem = (TextView) dialog.findViewById(R.id.detailwoitem);
        WOHEADER_DOCID = (TextView) dialog.findViewById(R.id.WOHEADER_DOCID);
        WOHEADER_OPEN = (TextView) dialog.findViewById(R.id.WOHEADER_OPEN);
        COMPANY_NAME = (TextView) dialog.findViewById(R.id.COMPANY_NAME);
        SOURCE_NAME = (TextView) dialog.findViewById(R.id.SOURCE_NAME);
        DEST_NAME = (TextView) dialog.findViewById(R.id.DEST_NAME);
        DISTANCE_PLAN = (TextView) dialog.findViewById(R.id.DISTANCE_PLAN);
        PRO_NAME = (TextView) dialog.findViewById(R.id.PRO_NAME);
        CUSTOMER_NAME = (TextView) dialog.findViewById(R.id.CUSTUMER_NAME);
        remark = (TextView) dialog.findViewById(R.id.remarkPlan);
    }

    public void showDialogDetail(int position) {
        detailwoitem.setText("รายละเอียด " + (position + 1) + "/" + data.size());
        String DT[] = data.get(position).get("PLHEADERDATESTARTPLAN").split("T");
        WOHEADER_OPEN.setText(DT[0] + " / " + DT[1].substring(0, 5));
        WOHEADER_DOCID.setText(data.get(position).get("PLHEADERDOCID"));
        COMPANY_NAME.setText(data.get(position).get("PLHEADERCOMPANYNAMEPLAN"));
        SOURCE_NAME.setText(data.get(position).get("PLITEMSOURCENAME"));
        DEST_NAME.setText(data.get(position).get("PLITEMDESTNAME"));
        DISTANCE_PLAN.setText(data.get(position).get("PLCUSTOMERDISTANCETC") + " กม.");
        String productSt = data.get(position).get("PLITEMPRODUCTPRONAME");
        PRO_NAME.setText(Html.fromHtml(productSt.replace("ลง", "<font color='red'>ลง</font>").replace("ขึ้น", "<font color='blue'>ขึ้น</font>").replace("\n", "<br>")));
        CUSTOMER_NAME.setText(data.get(position).get("PLCUSTOMERCUSTNAME"));
        remark.setText(data.get(position).get("PLCUSTOMERREMARK"));
        ImageView close = (ImageView) dialog.findViewById(R.id.close);
        dialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    public void showDialogRemark(final String TYPE) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("กรุณาใส่เหตุผล");
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_comment, null);
        final AutoCompleteTextView input = (AutoCompleteTextView) view.findViewById(R.id.inputComment);
        input.setThreshold(1);
        input.setLines(3);
        input.setPaddingRelative(16, 0, 16, 0);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        alert.setView(view);
        alert.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String strName = input.getText().toString();
                AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                builderInner.setMessage(strName);
                builderInner.setTitle("เหตุผล : ");
                builderInner.setPositiveButton("ตกลง",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new loadData(data.get(0).get("PLHEADERDOCID"), sp.getString("truckid", ""), TYPE, strName).execute();
                            }
                        });
                builderInner.show();
            }
        });
                /*alert.setNeutralButton("# รูปแบบข้อความ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });*/
        final AlertDialog alert2 = alert.create();
        alert2.show();
    }

    public void showDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        //alert.setTitle("กรุณาใส่เหตุผล");
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_checkbox, null);
        CheckBox cb = (CheckBox) view.findViewById(R.id.cbInDialog);
        cb.setText("ยืนยันเปิดงาน");
        alert.setView(view);
        alert.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                new loadData(data.get(0).get("PLHEADERDOCID"), sp.getString("truckid", ""), "Accept", "").execute();
            }
        });
        final AlertDialog alert2 = alert.create();
        alert2.show();
        ((AlertDialog) alert2).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ((AlertDialog) alert2).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else{
                    ((AlertDialog) alert2).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
    }

    private class loadData extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        String PLANID, TRUCKID, STATUS, REMARK;

        public loadData(String PLANID, String TRUCKID, String STATUS, String REMARK) {
            this.PLANID = PLANID;
            this.TRUCKID = TRUCKID;
            this.STATUS = STATUS;
            this.REMARK = REMARK;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("กรุณารอสักครู่");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (s.equalsIgnoreCase("true") && STATUS.equalsIgnoreCase("Accept") && !sp.getBoolean("WORK", true)) {
                new MainActivity.RefreshTask(sp.getString("truckid", ""), sp.getString("empid", ""), getActivity()).execute();
            } else if (!s.equalsIgnoreCase("true")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("ผิดพลาด กรุณาลองใหม่อีกครั้ง");
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
        protected String doInBackground(String... str) {
            CallService callService = new CallService();
            String result;
            Log.d("TEST", PLANID + " " + TRUCKID + " " + STATUS + " " + REMARK + " " + sp.getBoolean("WORK", true));
            if (STATUS.equalsIgnoreCase("Accept") && !sp.getBoolean("WORK", true)) {
                result = callService.Save_OpenWork(PLANID);
            } else {
                result = callService.Save_PlanAcceptReject(PLANID, TRUCKID, STATUS, REMARK);
            }
            return result;
        }
    }

}
