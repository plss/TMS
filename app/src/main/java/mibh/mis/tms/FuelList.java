package mibh.mis.tms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;

import java.util.ArrayList;

import mibh.mis.tms.data.FuelData;
import mibh.mis.tms.database.img_tms;
import mibh.mis.tms.qrcode.Content;
import mibh.mis.tms.qrcode.QRCodeEncoder;
import mibh.mis.tms.service.CallService;
import mibh.mis.tms.service.CheckOnline;

/**
 * Created by ponlakiss on 06/07/2015.
 */

public class FuelList extends Fragment {

    RecyclerView recyclerView;
    Adapter adapter;
    CardView listItem;
    View rootView;
    private ArrayList<String> data = new ArrayList<>();
    img_tms ImgTms;
    FuelData fuelData;
    private ArrayList<img_tms.Image_tms> cursor;
    View btnNewFuel;
    SharedPreferences sp;
    CheckOnline check;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fuel_list, container, false);

        listItem = (CardView) rootView.findViewById(R.id.cardfuel_item);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.fuel_recycleview);
        btnNewFuel = rootView.findViewById(R.id.btnNewFuel);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        sp = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        check = new CheckOnline(getActivity());
        ImgTms = new img_tms(getActivity());
        fuelData = new FuelData();
        data = fuelData.getFuelData();
        if (data.size() == 0 || data == null) {
            //btnNewFuel.setVisibility(View.GONE);
        }

        cursor = ImgTms.Img_GetImageByGroupType(fuelData.getDocId(), ImgTms.GTYPE_FUEL);
        for (int i = 0; i < cursor.size(); i++) {
            fuelData.setFuelState(cursor.get(i).Doc_item);
        }
        ImgTms.close();
        if (adapter == null) {
            adapter = new Adapter(getActivity());
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        btnNewFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check.isOnline()){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("คุณต้องการที่จะเบิกเชื้อเพลิงเพิ่มใช่หรือไม่");
                    alertDialogBuilder.setPositiveButton("ใช่",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    new Loading().execute(fuelData.getDocId(), sp.getString("empid", ""));
                                }
                            });
                    alertDialogBuilder.setNegativeButton("ไม่",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                else {
                    showAlertDialog();
                }

            }
        });


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (resultCode == -1) {
            /*fuelData.setFuelState(requestCode);
            adapter.notifyDataSetChanged();*/
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.nav_contentframe, new FuelList()).commit();
        //}
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.VersionViewHolder> {

        Context context;

        public Adapter(Context context) {
            this.context = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fuel_item, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int position) {
            versionViewHolder.fuelTxt.setText("เติมเชื้อเพลิง " + (position + 1));
            versionViewHolder.fuelQr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String qrData = fuelData.getFuelIdIndex(position);
                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.qr_preview);
                    dialog.setCancelable(true);
                    TextView qrText = (TextView) dialog.findViewById(R.id.qrTxt);
                    ImageView qrPic = (ImageView) dialog.findViewById(R.id.qrPic);
                    qrText.setText(qrData);
                    int qrCodeDimention = 500;
                    QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrData, null,
                            Content.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);
                    try {
                        Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                        /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(500, 500);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        params.setMargins(10, 10, 10, 10);
                        qrPic.setLayoutParams(params);*/
                        qrPic.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.show();
                }
            });
            versionViewHolder.fuelClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ImageList.class);
                    intent.putExtra("From", ImgTms.GTYPE_FUEL);
                    intent.putExtra("WOHEADER_DOCID", fuelData.getDocId());
                    intent.putExtra("DOCID", fuelData.getFuelIdIndex(position));
                    FuelList.this.startActivityForResult(intent, position);
                }
            });
            if (!fuelData.getFuelState(position)) {
                versionViewHolder.fuelStat.setImageResource(R.drawable.fuel_black);
            } else versionViewHolder.fuelStat.setImageResource(R.drawable.fuel_green);
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        public class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            CardView cardItemLayout;
            TextView fuelTxt;
            ImageView fuelStat, fuelQr;
            View fuelClick;

            public VersionViewHolder(View itemView) {
                super(itemView);
                cardItemLayout = (CardView) itemView.findViewById(R.id.cardfuel_item);
                fuelTxt = (TextView) itemView.findViewById(R.id.fuelTxt);
                fuelStat = (ImageView) itemView.findViewById(R.id.fuelstat);
                fuelQr = (ImageView) itemView.findViewById(R.id.fuelqr);
                fuelClick = itemView.findViewById(R.id.fuelClick);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                //getPosition();
            }
        }

    }

    private class Loading extends AsyncTask<String, Void, String> {

        ProgressDialog progressBar;
        String result;

        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(getActivity());
            progressBar.setMessage("กำลังขออนุมัติใบเบิกเพิ่ม");
            progressBar.show();
        }

        @Override
        protected String doInBackground(String... s) {
            result = new CallService().genNewFuel(s[0], s[1]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.dismiss();
            if (s.equals("null") || s.equals("[]")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("เน็ตเวิร์กขัดข้องกรุณาลองใหม่");
                alertDialogBuilder.setNegativeButton("ตกลง",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                fuelData.addFuelId(result);
                //fuel.setFeuel(result);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showAlertDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
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
