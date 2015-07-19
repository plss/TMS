package mibh.mis.tms;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import mibh.mis.tms.data.PlanData;

/**
 * Created by ponlakiss on 06/07/2015.
 */
public class PlanList extends Fragment {

    RecyclerView recyclerView;
    PlanItem adapter;
    RecyclerView mainRecyclerView;
    CardView listItem;
    View rootView;
    RelativeLayout rejectPlan;
    private ArrayList<HashMap<String, String>> data = new ArrayList<>();
    public Dialog dialog;
    private TextView WOHEADER_OPEN, COMPANY_NAME, SOURCE_NAME, DEST_NAME, DISTANCE_PLAN, PRO_NAME, CUSTOMER_NAME, detailwoitem, WOHEADER_DOCID, remark;
    private View print;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.plan_list, container, false);

        mainRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        listItem = (CardView) rootView.findViewById(R.id.cardlist_item);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.home_recyclerview);
        rejectPlan = (RelativeLayout) rootView.findViewById(R.id.rejectPlan);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new PlanData().getPlanData();

        TextView txtNoPlan = (TextView) rootView.findViewById(R.id.txtNoPlan);
        if (data.size() == 0 || data == null) {
            txtNoPlan.setText("ไม่มีแผนงาน");
        } else txtNoPlan.setVisibility(View.INVISIBLE);

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

        rejectPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                builderSingle.setTitle("กรุณาเลือกเหตุผล");
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("รถเสีย");
                arrayAdapter.add("ลาป่วยกระทันหัน");
                arrayAdapter.add("ลากิจกระทันหัน");
                builderSingle.setNegativeButton("ยกเลิก",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(arrayAdapter,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strName = arrayAdapter.getItem(which);
                                AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                                builderInner.setMessage(strName);
                                builderInner.setTitle("เหตุผล : ");
                                builderInner.setPositiveButton("ตกลง",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                builderInner.show();
                            }
                        });
                builderSingle.show();
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

}
