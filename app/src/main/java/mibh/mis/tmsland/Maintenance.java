package mibh.mis.tmsland;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import mibh.mis.tmsland.data.LastworkData;
import mibh.mis.tmsland.database.img_tms;
import mibh.mis.tmsland.service.CallService;
import mibh.mis.tmsland.service.Multifunction;
import mibh.mis.tmsland.service.validateLatLng;

/**
 * Created by ponlakiss on 09/08/2015.
 */

public class Maintenance extends Fragment {

    View rootView;
    RelativeLayout readyTruckBtn, readyDriverBtn, rdyTruckCam, rdyDriverCam, reqWorkBtn, reqWorkCam;
    TextView readyTruckTxt, readyDriverTxt, reqWorkTxt, txtMtnTruck, txtMtnName, reqWorkName, txtResultLoading;
    img_tms ImgTms;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    LastworkData lastWork;
    LinearLayout mtnContent;
    ProgressBar loadingProgress;
    final String[] strStatus = {"AVAILABLE", "NOTAVAILABLE", "NONE"};
    String[] valDate;
    AutoCompleteTextView input;
    AlertDialog dialogList2;
    String comment = "", truckId = "", strReqWork = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.maintenance, container, false);
        sp = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);

        ImgTms = new img_tms(getActivity());

        readyTruckBtn = (RelativeLayout) rootView.findViewById(R.id.readyCarBtn);
        readyDriverBtn = (RelativeLayout) rootView.findViewById(R.id.readyHumanBtn);
        reqWorkBtn = (RelativeLayout) rootView.findViewById(R.id.reqWorkBtn);
        rdyTruckCam = (RelativeLayout) rootView.findViewById(R.id.rdyCarCam);
        rdyDriverCam = (RelativeLayout) rootView.findViewById(R.id.rdyHumanCam);
        reqWorkCam = (RelativeLayout) rootView.findViewById(R.id.reqWorkCam);
        readyTruckTxt = (TextView) rootView.findViewById(R.id.readyCarTxt);
        readyDriverTxt = (TextView) rootView.findViewById(R.id.readyHumanTxt);
        reqWorkTxt = (TextView) rootView.findViewById(R.id.reqWorkTxt);
        txtMtnTruck = (TextView) rootView.findViewById(R.id.txtMtnTruck);
        txtMtnName = (TextView) rootView.findViewById(R.id.txtMtnName);
        reqWorkName = (TextView) rootView.findViewById(R.id.reqWorkName);
        mtnContent = (LinearLayout) rootView.findViewById(R.id.mtnContent);
        loadingProgress = (ProgressBar) rootView.findViewById(R.id.loadingProgress);
        txtResultLoading = (TextView) rootView.findViewById(R.id.txtResultLoading);

        truckId = sp.getString("truckid", "");
        txtMtnName.setText(sp.getString("firstname", "") + " " + sp.getString("lastname", ""));
        txtMtnTruck.setText("เบอร์รถ : " + truckId);

        lastWork = new LastworkData();

        setValDate();

        new GetLastWork().execute();

        txtResultLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetLastWork().execute();
            }
        });

        readyTruckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLatLng.check(sp)) {
                    selectDialog(0);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
            }
        });

        readyDriverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLatLng.check(sp)) {
                    selectDialog(1);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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

            }
        });

        reqWorkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLatLng.check(sp)) {
                    selectWorkListDialog();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
            }
        });

        rdyTruckCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valDate[0].equalsIgnoreCase(strStatus[2])) {
                    if (validateLatLng.check(sp)) {
                        selectDialog(0);
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
                } else {
                    Intent intent = new Intent(getActivity(), ImageList.class);
                    intent.putExtra("From", ImgTms.GTYPE_MAINTENANCE);
                    intent.putExtra("WOHEADER_DOCID", lastWork.getLastWork());
                    intent.putExtra("DOCID", "10");
                    intent.putExtra("DETAIL", "ตรวจสภาพรถ");
                    intent.putExtra("STATUS", readyTruckTxt.getText().toString());
                    Maintenance.this.startActivityForResult(intent, 0);
                }

            }
        });

        rdyDriverCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp.getString("statuscheckdriver", "").equalsIgnoreCase("")) {
                    if (validateLatLng.check(sp)) {
                        selectDialog(1);
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
                } else {
                    Intent intent = new Intent(getActivity(), ImageList.class);
                    intent.putExtra("From", ImgTms.GTYPE_MTNDRIVER);
                    intent.putExtra("WOHEADER_DOCID", lastWork.getLastWork());
                    intent.putExtra("DOCID", "10");
                    intent.putExtra("DETAIL", "รายงานตัว");
                    intent.putExtra("STATUS", readyDriverTxt.getText().toString());
                    Maintenance.this.startActivityForResult(intent, 1);
                }
            }
        });

        reqWorkCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp.getString("statusreqwork", "").equalsIgnoreCase("")) {
                    if (validateLatLng.check(sp)) {
                        selectWorkListDialog();
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
                } else {
                    Intent intent = new Intent(getActivity(), ImageList.class);
                    intent.putExtra("From", ImgTms.GTYPE_REQWORK);
                    intent.putExtra("WOHEADER_DOCID", lastWork.getLastWork());
                    intent.putExtra("DOCID", "10");
                    intent.putExtra("DETAIL", "รายงานตัวล่วงหน้า");
                    intent.putExtra("STATUS", strReqWork);
                    Maintenance.this.startActivityForResult(intent, 2);
                }
            }
        });


        return rootView;
    }

    private void setValDate() {
        Long currentDate = Calendar.getInstance().getTimeInMillis();
        if (currentDate / (24 * 60 * 60 * 1000) != sp.getLong("lastcheckstatus", 0) / (24 * 60 * 60 * 1000)) {

            editor = sp.edit();
            editor.putString("statuscheckdriver", "");
            editor.putString("statusreqwork", "");
            editor.apply();
        }
        valDate = sp.getString(truckId, "NONE|0").split("\\|");
        if (currentDate / (24 * 60 * 60 * 1000) != Long.parseLong(valDate[1]) / (24 * 60 * 60 * 1000)) {
            editor = sp.edit();
            editor.putString(truckId, strStatus[2] + "|0");
            editor.apply();
        }
    }

    private class SaveCheckDriver extends AsyncTask<Void, Void, String> {

        String resultService, statusDriver, destStatDriver, commentDriver;
        ProgressDialog dialog = new ProgressDialog(getActivity());

        public SaveCheckDriver(String statusDriver, String destStatDriver) {
            this.statusDriver = statusDriver;
            this.destStatDriver = destStatDriver;
            this.commentDriver = "";
            dialog.setMessage("กรุณารอสักครู่");
            dialog.setCancelable(false);
        }

        public SaveCheckDriver(String statusDriver, String destStatDriver, String commentDriver) {
            this.statusDriver = statusDriver;
            this.destStatDriver = destStatDriver;
            this.commentDriver = commentDriver;
            dialog.setMessage("กรุณารอสักครู่");
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(Void... params) {
            String loName;
            if (sp.getString("locationname", "").length() > 200) {
                loName = sp.getString("locationname", "").substring(0, 198);
            } else loName = sp.getString("locationname", "");
            resultService = new CallService().saveCheckDriver(lastWork.getLastWork(), "10", sp.getString("empid", ""), sp.getString("firstname", "") + " " + sp.getString("lastname", ""), statusDriver, destStatDriver, String.format("%.5f,%.5f", Double.parseDouble(sp.getString("latitude", "0")), Double.parseDouble(sp.getString("longtitude", "0"))), loName, commentDriver);
            Log.d("SaveCheckDriver", resultService);
            return resultService;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            if (str.equalsIgnoreCase("true")) {
                editor = sp.edit();
                editor.putString("statuscheckdriver", statusDriver);
                editor.putLong("lastcheckstatus", Calendar.getInstance().getTimeInMillis());
                editor.apply();
                if (sp.getString("statuscheckdriver", "").equalsIgnoreCase(strStatus[0])) {
                    readyDriverBtn.setBackgroundColor(getResources().getColor(R.color.custom_green));
                    readyDriverTxt.setTextColor(Color.BLACK);
                    readyDriverTxt.setText("พร้อม");
                } else if (sp.getString("statuscheckdriver", "").equalsIgnoreCase(strStatus[1])) {
                    readyDriverBtn.setBackgroundColor(getResources().getColor(R.color.custom_red));
                    readyDriverTxt.setTextColor(Color.WHITE);
                    readyDriverTxt.setText("ไม่พร้อม");
                }
                setValDate();

                Intent intent = new Intent(getActivity(), ImageList.class);
                intent.putExtra("From", ImgTms.GTYPE_MTNDRIVER);
                intent.putExtra("WOHEADER_DOCID", lastWork.getLastWork());
                intent.putExtra("DOCID", "10");
                intent.putExtra("DETAIL", "รายงานตัว");
                intent.putExtra("STATUS", readyDriverTxt.getText().toString());
                Maintenance.this.startActivityForResult(intent, 1);

            } else {
                showAlertDialog("การเชื่อมต่อผิดพลาด กรุณาลองอีกครั้ง");
            }
            dialog.dismiss();
        }
    }

    private class SaveCheckTruck extends AsyncTask<Void, Void, String> {

        String resultService, statusTruck, destStatTruck, commentTruck, truckId;
        ProgressDialog dialog = new ProgressDialog(getActivity());

        public SaveCheckTruck(String truckId, String statusTruck, String destStatTruck) {
            this.truckId = truckId;
            this.statusTruck = statusTruck;
            this.destStatTruck = destStatTruck;
            this.commentTruck = "";
            dialog.setMessage("กรุณารอสักครู่");
            dialog.setCancelable(false);
        }

        public SaveCheckTruck(String truckId, String statusTruck, String destStatTruck, String commentTruck) {
            this.truckId = truckId;
            this.statusTruck = statusTruck;
            this.destStatTruck = destStatTruck;
            this.commentTruck = commentTruck;
            dialog.setMessage("กรุณารอสักครู่");
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(Void... params) {
            String loName;
            if (sp.getString("locationname", "").length() > 200) {
                loName = sp.getString("locationname", "").substring(0, 198);
            } else loName = sp.getString("locationname", "");
            resultService = new CallService().saveCheckTruck(lastWork.getLastWork(), "10", sp.getString("empid", ""), sp.getString("truckid", ""), statusTruck, destStatTruck, String.format("%.5f,%.5f", Double.parseDouble(sp.getString("latitude", "0")), Double.parseDouble(sp.getString("longtitude", "0"))), loName, commentTruck);
            Log.d("SaveCheckTruck", resultService);
            return resultService;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            if (str.equalsIgnoreCase("true")) {
                editor = sp.edit();
                long currentDate = Calendar.getInstance().getTimeInMillis();
                //editor.putString("statuschecktruck", statusTruck);
                editor.putString(truckId, statusTruck + "|" + currentDate);
                editor.putLong("lastcheckstatus", currentDate);
                editor.apply();
                if (statusTruck.equalsIgnoreCase(strStatus[0])) {
                    readyTruckBtn.setBackgroundColor(getResources().getColor(R.color.custom_green));
                    readyTruckTxt.setTextColor(Color.BLACK);
                    readyTruckTxt.setText("พร้อม");
                } else if (statusTruck.equalsIgnoreCase(strStatus[1])) {
                    readyTruckBtn.setBackgroundColor(getResources().getColor(R.color.custom_red));
                    readyTruckTxt.setTextColor(Color.WHITE);
                    readyTruckTxt.setText("ไม่พร้อม");
                }
                setValDate();

                Intent intent = new Intent(getActivity(), ImageList.class);
                intent.putExtra("From", ImgTms.GTYPE_MAINTENANCE);
                intent.putExtra("WOHEADER_DOCID", lastWork.getLastWork());
                intent.putExtra("DOCID", "10");
                intent.putExtra("DETAIL", "ตรวจสภาพรถ");
                intent.putExtra("STATUS", readyTruckTxt.getText().toString());
                Maintenance.this.startActivityForResult(intent, 0);

            } else {
                showAlertDialog("การเชื่อมต่อผิดพลาด กรุณาลองอีกครั้ง");
            }
            dialog.dismiss();
        }
    }

    private class SaveReqWork extends AsyncTask<Void, Void, String> {

        ProgressDialog dialog = new ProgressDialog(getActivity());
        String listValue, listText;

        public SaveReqWork(String listValue, String listText) {
            this.listValue = listValue;
            this.listText = listText;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("กรุณารอสักครู่");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultReqWork = new CallService().Save_ReqWork(sp.getString("truckid", ""), sp.getString("empid", ""), sp.getString("firstname", "") + " " + sp.getString("lastname", ""), String.format("%.5f,%.5f", Double.parseDouble(sp.getString("latitude", "0")), Double.parseDouble(sp.getString("longtitude", "0"))), sp.getString("locationname", ""), listValue, listText, "");
            return resultReqWork;
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            if (str.equalsIgnoreCase("true")) {
                editor = sp.edit();
                strReqWork = listText;
                editor.putString("statusreqwork", listText);
                editor.putLong("lastcheckstatus", Calendar.getInstance().getTimeInMillis());
                editor.apply();
                if (!sp.getString("statusreqwork", "").equalsIgnoreCase("")) {
                    reqWorkBtn.setBackgroundColor(getResources().getColor(R.color.custom_green));
                    reqWorkTxt.setText("รายงานตัวแล้ว");
                }

                setValDate();

                Intent intent = new Intent(getActivity(), ImageList.class);
                intent.putExtra("From", ImgTms.GTYPE_REQWORK);
                intent.putExtra("WOHEADER_DOCID", lastWork.getLastWork());
                intent.putExtra("DOCID", "10");
                intent.putExtra("DETAIL", "รายงานตัวล่วงหน้า");
                intent.putExtra("STATUS", strReqWork);
                Maintenance.this.startActivityForResult(intent, 2);

            } else {
                showAlertDialog("การเชื่อมต่อผิดพลาด กรุณาลองอีกครั้ง");
            }
            dialog.dismiss();
        }
    }

    private class GetLastWork extends AsyncTask<Void, Void, String> {

        String resultService;

        @Override
        protected String doInBackground(Void... params) {
            resultService = new CallService().getLastWork(sp.getString("truckid", ""));
            Log.d("getLastWork", resultService);
            return resultService;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mtnContent.setVisibility(View.INVISIBLE);
            loadingProgress.setVisibility(View.VISIBLE);
            txtResultLoading.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            if (!str.equalsIgnoreCase("error")) {
                /*String strLast = readFileLastWork();
                if (strLast == null || strLast.equals("") || !getLastWorkJson(str).equalsIgnoreCase(getLastWorkJson(strLast))) {
                    lastWork.convert(str);
                    writeFileLastWork(str);
                    *//*editor = sp.edit();
                    editor.putString("statuscheckdriver", "");
                    editor.putString("statuschecktruck", "");
                    editor.apply();*//*
                } else {
                    lastWork.convert(strLast);
                }*/
                lastWork.convert(str);
                mtnContent.setVisibility(View.VISIBLE);
                loadingProgress.setVisibility(View.INVISIBLE);
            } else {
                //showAlertDialog("การเชื่อมต่อผิดพลาด กรุณาลองอีกครั้ง");
                loadingProgress.setVisibility(View.INVISIBLE);
                txtResultLoading.setVisibility(View.VISIBLE);
                txtResultLoading.setText("การเชื่อมต่อผิดพลาด กรุณาลองอีกครั้ง");
            }

            if (sp.getString("statuscheckdriver", "").equalsIgnoreCase(strStatus[0])) {
                readyDriverBtn.setBackgroundColor(getResources().getColor(R.color.custom_green));
                readyDriverTxt.setTextColor(Color.BLACK);
                readyDriverTxt.setText("พร้อม");
            } else if (sp.getString("statuscheckdriver", "").equalsIgnoreCase(strStatus[1])) {
                readyDriverBtn.setBackgroundColor(getResources().getColor(R.color.custom_red));
                readyDriverTxt.setTextColor(Color.WHITE);
                readyDriverTxt.setText("ไม่พร้อม");
            } else {
                readyDriverTxt.setText("พร้อม / ไม่พร้อม");
            }
            final String valDate[] = sp.getString(truckId, "NONE|0").split("\\|");
            if (valDate[0].equalsIgnoreCase(strStatus[0])) {
                readyTruckBtn.setBackgroundColor(getResources().getColor(R.color.custom_green));
                readyTruckTxt.setTextColor(Color.BLACK);
                readyTruckTxt.setText("พร้อม");
            } else if (valDate[0].equalsIgnoreCase(strStatus[1])) {
                readyTruckBtn.setBackgroundColor(getResources().getColor(R.color.custom_red));
                readyTruckTxt.setTextColor(Color.WHITE);
                readyTruckTxt.setText("ไม่พร้อม");
            } else {
                readyTruckTxt.setText("พร้อม / ไม่พร้อม");
            }
            if (!sp.getString("statusreqwork", "").equalsIgnoreCase("")) {
                strReqWork = sp.getString("statusreqwork", "");
                reqWorkBtn.setBackgroundColor(getResources().getColor(R.color.custom_green));
                reqWorkTxt.setText("รายงานตัวแล้ว");
            } else {
                reqWorkTxt.setText("รายงานตัว");
            }
        }

    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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

    private String getLastWorkJson(String lastWorkJson) {
        String result;
        try {
            JSONArray data = new JSONArray(lastWorkJson);
            JSONObject c = data.getJSONObject(0);
            result = c.getString("LAST_WORK");
        } catch (Exception e) {
            result = "error";
        }
        return result;
    }

    private void writeFileLastWork(String Json) {
        Multifunction fileMng = new Multifunction(getActivity());
        fileMng.mCreateAndSaveFile("LASTWORK.txt", Json);
    }

    private String readFileLastWork() {
        Multifunction fileMng = new Multifunction(getActivity());
        return fileMng.mReadJsonData("LASTWORK.txt");
    }

    private void selectDialog(final int index) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> adapt = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
        adapt.add("พร้อม");
        adapt.add("ไม่พร้อม");
        alertDialog.setNegativeButton("ยกเลิก",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setAdapter(adapt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    commentDialog(index, "", "");
                } else if (index == 0) {
                    new SaveCheckTruck(truckId, strStatus[which], adapt.getItem(which)).execute();
                } else new SaveCheckDriver(strStatus[which], adapt.getItem(which)).execute();
            }
        });
        alertDialog.show();
    }

    private void selectWorkListDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> adapt = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
        final ArrayList<img_tms.WorkList> workLists = ImgTms.GetActiveWorkList();
        for (int i = 0; i < workLists.size(); ++i) {
            adapt.add(workLists.get(i).list_name);
        }
        alertDialog.setNegativeButton("ยกเลิก",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setAdapter(adapt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //workLists.get(which)
                new SaveReqWork(workLists.get(which).list_id, workLists.get(which).list_name).execute();
                //commentDialog(2, workLists.get(which).list_id, workLists.get(which).list_name);
            }
        });
        alertDialog.show();
    }

    private void commentDialog(final int index, final String listValue, final String listText) {

        editor = sp.edit();
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(index == 2 ? "หมายเหตุ" : "ใส่เหตุผลที่ไม่พร้อม");
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_comment, null);
        input = (AutoCompleteTextView) view.findViewById(R.id.inputComment);
        input.setThreshold(1);
        input.setLines(3);
        input.setPaddingRelative(16, 0, 16, 0);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (sp.getString("listcomment", "").equals("")) {
                    editor.putString("listcomment", "ซ่อมรถ&เติมก๊าซ&ชั่งเข้า&ชั่งออก&รายงานตัวครับ&ลงสินค้า&ขึ้นสินค้า&ล้างรถ&บิลก็าซ&รอลงตู้&กำลังเติมก๊าซ&X-Ray");
                    editor.apply();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filter = s.toString().toLowerCase();
                ArrayList<String> listItems = new ArrayList<String>();
                int countItems = 0;
                for (String listItem : sp.getString("listcomment", "").split("&")) {
                    if (listItem.toLowerCase().contains(filter)) {
                        if (countItems >= 3) {
                            break;
                        }
                        listItems.add(listItem);
                        countItems++;
                    }

                }
                ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listItems);
                input.setAdapter(adapt);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        alert.setView(view);
        alert.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                comment = input.getText().toString();
                if (sp.getString("listcomment", "").equals("")) {
                    editor.putString("listcomment", comment);
                } else {
                    String str = comment;
                    List<String> items = Arrays.asList(sp.getString("listcomment", "").split("&"));
                    if (!items.contains(comment) && !comment.equals("")) {
                        for (int x = 0, i = 0; x < items.size(); ++x, i++) {
                            if (i >= 20) {
                                break;
                            }
                            str += "&";
                            str += items.get(x);
                        }
                        editor.putString("listcomment", str);
                    }
                }
                editor.apply();
                if (index == 0) {
                    new SaveCheckTruck(truckId, strStatus[1], "ไม่พร้อม", comment).execute();
                } else if (index == 1) {
                    new SaveCheckDriver(strStatus[1], "ไม่พร้อม", comment).execute();
                } else if (index == 2) {
                    //new SaveReqWork(comment, listValue, listText).execute();
                }
            }
        });
        alert.setNeutralButton("# รูปแบบข้อความ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        final AlertDialog alert2 = alert.create();
        alert2.show();
        Button btn = alert2.getButton(DialogInterface.BUTTON_NEUTRAL);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogList = new AlertDialog.Builder(getActivity());
                dialogList.setTitle("เลือกรูปแบบข้อความ");
                View dialoglist_view = getActivity().getLayoutInflater().inflate(R.layout.dialoglist, null);
                RecyclerView recyclerView = (RecyclerView) dialoglist_view.findViewById(R.id.dialoglist_rv);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);

                ArrayList<img_tms.Hashtag_TMS> Hash;
                if (index == 0) {
                    Hash = ImgTms.GetHashtagByGtypeAndImgType(ImgTms.GTYPE_MAINTENANCE, ImgTms.IMG_MTNTRUCKREJECT);
                } else if (index == 1) {
                    Hash = ImgTms.GetHashtagByGtypeAndImgType(ImgTms.GTYPE_MAINTENANCE, ImgTms.IMG_MTNDRIVERREJECT);
                } else {
                    Hash = ImgTms.GetHashtagByGtypeAndImgType(ImgTms.GTYPE_MAINTENANCE, ImgTms.IMG_MTNDRIVERREJECT);
                }

                arrayAdapter.clear();
                for (int i = 0; i < Hash.size(); ++i) {
                    arrayAdapter.add(Hash.get(i).list_name);
                }
                Adapter adapter = new Adapter(getActivity(), arrayAdapter);
                recyclerView.setAdapter(adapter);
                dialogList.setView(dialoglist_view);
                dialogList.setNegativeButton("ยกเลิก",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialogList2 = dialogList.create();
                dialogList2.show();
            }
        });
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        Context context;
        ArrayAdapter<String> arrayAdapter;

        public Adapter(Context context, ArrayAdapter<String> arrayAdapter) {
            this.context = context;
            this.arrayAdapter = arrayAdapter;
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
            holder.dialogTxt.setText(arrayAdapter.getItem(position));
        }

        @Override
        public int getItemCount() {
            return arrayAdapter.getCount();
        }

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialoglist_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView dialogTxt;

            public ViewHolder(View itemView) {
                super(itemView);
                dialogTxt = (TextView) itemView.findViewById(R.id.dialoglist_txt);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                input.setText(arrayAdapter.getItem(getAdapterPosition()));
                dialogList2.dismiss();
            }
        }


    }

}
