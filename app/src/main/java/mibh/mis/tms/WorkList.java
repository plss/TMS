package mibh.mis.tms;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import mibh.mis.tms.data.WorkData;
import mibh.mis.tms.database.img_tms;
import mibh.mis.tms.qrcode.Content;
import mibh.mis.tms.qrcode.QRCodeEncoder;
import mibh.mis.tms.service.CallService;
import mibh.mis.tms.service.FileDownloader;

public class WorkList extends Fragment {

    RecyclerView recyclerView;
    Adapter adapter;
    RecyclerView mainRecyclerView;
    CardView listItem;
    View rootView;
    RelativeLayout closeWork;
    private ArrayList<HashMap<String, String>> data = new ArrayList<>();
    public Dialog dialog;
    private TextView WOHEADER_OPEN, COMPANY_NAME, SOURCE_NAME, DEST_NAME, DISTANCE_PLAN, TRUCK_LICENSE, TRUCK_LICENSE_PROVINCE, TAIL_LICENSE, TAIL_LICENSE_PROVINCE, PRO_NAME, CUSTOMER_NAME, detailwoitem, WOHEADER_DOCID, remark;
    private ImageView qrDetail;
    private View zDetail;
    private img_tms ImgTms;
    private HashMap<String, Boolean> mCheckStates = new HashMap<>();
    private ArrayList<img_tms.Image_tms> cursor;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.work_list, container, false);

        mainRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        closeWork = (RelativeLayout) rootView.findViewById(R.id.closeWork);
        listItem = (CardView) rootView.findViewById(R.id.cardlist_item);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.home_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ImgTms = new img_tms(getActivity());
        sp = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);

        data = new WorkData().getWorkData();
        if (data.size() >= 0) {
            cursor = ImgTms.Img_GetImageByGroupType(data.get(0).get("WOHEADER_DOCID"), ImgTms.GTYPE_WORK);
            for (int i = 0; i < cursor.size(); i++) {
                mCheckStates.put(cursor.get(i).Doc_item, true);
            }
        }
        ImgTms.close();
        if (adapter == null) {
            adapter = new Adapter(getActivity());
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        closeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (resultCode == -1) {
            /*if (data.getStringExtra("DOCID") != null) {
                int x = Integer.parseInt(data.getStringExtra("DOCID"));
                mCheckStates.put(x, true);
            }
            adapter.notifyDataSetChanged();*/
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.nav_contentframe, new WorkList()).commit();
        //}
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_test, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.VersionViewHolder> {

        Context context;
        private String url, fileUrl;

        /* Zoom control */
        float TouchOfsWidth = 0;
        float CurrentScale = 1;
        float CurrentScalePush = 1;
        float[] CurrentOfsPos = new float[2];
        float[] ScreenOfsPos = new float[2];
        float[] TranslateOfsPos = new float[2];
        float[] TranslateOfsPosPre = new float[2];
        float[] TouchCenter = new float[2];

        public Adapter(Context context) {
            this.context = context;

            /* Init variable */
            TouchOfsWidth = 0;
            CurrentScale = 1;
            CurrentScalePush = 1;
            Arrays.fill(CurrentOfsPos, 0);
            Arrays.fill(ScreenOfsPos, 0);
            Arrays.fill(TranslateOfsPos, 0);
            Arrays.fill(TranslateOfsPosPre, 0);
            Arrays.fill(TouchCenter, 0);
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_item, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int position) {

            versionViewHolder.woitem.setText("เส้นทาง No." + data.get(position).get("WOITEM_DOCID"));
            versionViewHolder.sourceTxt.setText(data.get(position).get("SOURCE_NAME"));
            versionViewHolder.destTxt.setText(data.get(position).get("DEST_NAME"));
            final String product = data.get(position).get("PRO_NAME");
            versionViewHolder.product.setText(Html.fromHtml(product.replace("ลง", "<font color='red'>ลง</font>").replace("ขึ้น", "<font color='blue'>ขึ้น</font>").replace("\n", "<br>")));
            versionViewHolder.detailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initDialogDetail();
                    showDialogDetail(position);
                }
            });
            versionViewHolder.btnWorkItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageList.class);
                    intent.putExtra("From", img_tms.GTYPE_WORK);
                    intent.putExtra("WOHEADER_DOCID", data.get(position).get("WOHEADER_DOCID"));
                    intent.putExtra("WOITEM_DOCID", data.get(position).get("WOITEM_DOCID"));
                    intent.putExtra("DETAIL", data.get(position).get("PRO_NAME"));
                    intent.putExtra("Index", position);
                    WorkList.this.startActivityForResult(intent, 1);
                    notifyDataSetChanged();
                }
            });
            versionViewHolder.btnSignature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cam = new Intent(context, ImageList.class);
                    cam.putExtra("From", img_tms.GTYPE_STGNATURE);
                    cam.putExtra("WOHEADER_DOCID", data.get(position).get("WOHEADER_DOCID"));
                    cam.putExtra("ITEM", data.get(position).get("WOITEM_DOCID"));
                    startActivity(cam);
                }
            });
            if (mCheckStates.containsKey(data.get(position).get("WOITEM_DOCID"))) {
                if (mCheckStates.get(data.get(position).get("WOITEM_DOCID")))
                    versionViewHolder.workStatusBar.setBackgroundResource(R.drawable.color_tab_green);
            } else
                versionViewHolder.workStatusBar.setBackgroundResource(R.drawable.color_tab_red);
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        public void initDialogDetail() {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_detail_work);
            dialog.setCancelable(true);
            detailwoitem = (TextView) dialog.findViewById(R.id.detailwoitem);
            WOHEADER_DOCID = (TextView) dialog.findViewById(R.id.WOHEADER_DOCID);
            WOHEADER_OPEN = (TextView) dialog.findViewById(R.id.WOHEADER_OPEN);
            COMPANY_NAME = (TextView) dialog.findViewById(R.id.COMPANY_NAME);
            SOURCE_NAME = (TextView) dialog.findViewById(R.id.SOURCE_NAME);
            DEST_NAME = (TextView) dialog.findViewById(R.id.DEST_NAME);
            DISTANCE_PLAN = (TextView) dialog.findViewById(R.id.DISTANCE_PLAN);
            TRUCK_LICENSE = (TextView) dialog.findViewById(R.id.TRUCK_LICENSE);
            TRUCK_LICENSE_PROVINCE = (TextView) dialog.findViewById(R.id.TRUCK_LICENSE_PROVINCE);
            TAIL_LICENSE = (TextView) dialog.findViewById(R.id.TAIL_LICENSE);
            TAIL_LICENSE_PROVINCE = (TextView) dialog.findViewById(R.id.TAIL_LICENSE_PROVINCE);
            PRO_NAME = (TextView) dialog.findViewById(R.id.PRO_NAME);
            CUSTOMER_NAME = (TextView) dialog.findViewById(R.id.CUSTUMER_NAME);
            remark = (TextView) dialog.findViewById(R.id.remark);
            zDetail = dialog.findViewById(R.id.zoomDetail);
            qrDetail = (ImageView) dialog.findViewById(R.id.qrDetail);
        }

        public void showDialogDetail(int position) {
            //Log.d("TEST", "showDialogDetail: " + data.get(position).get("PLANSTARTSOURCE"));
            detailwoitem.setText("รายละเอียด " + (position + 1) + "/" + data.size());
            String DT[] = data.get(position).get("WOHEADER_OPEN").split("T");
            WOHEADER_OPEN.setText(DT[0] + " / " + DT[1].substring(0, 5));
            WOHEADER_DOCID.setText(data.get(position).get("WOHEADER_DOCID"));
            COMPANY_NAME.setText(data.get(position).get("COMPANY_NAME"));
            String DT2[] = data.get(position).get("PLANSTARTSOURCE").split("T");
            SOURCE_NAME.setText(DT2[0] + " / " + DT2[1].substring(0, 5) + "\n" + data.get(position).get("SOURCE_NAME"));
            String DT3[] = data.get(position).get("PLANARRIVEDEST").split("T");
            DEST_NAME.setText(DT3[0] + " / " + DT3[1].substring(0, 5) + "\n" + data.get(position).get("DEST_NAME"));
            DISTANCE_PLAN.setText(data.get(position).get("DISTANCE_PLAN") + " กม.");
            TRUCK_LICENSE.setText(data.get(position).get("TRUCK_ID") + " " + data.get(position).get("TRUCK_LICENSE"));
            TRUCK_LICENSE_PROVINCE.setText(data.get(position).get("TRUCK_LICENSE_PROVINCE"));
            TAIL_LICENSE.setText(data.get(position).get("TAIL_ID") + " " + data.get(position).get("TAIL_LICENSE"));
            TAIL_LICENSE_PROVINCE.setText(data.get(position).get("TAIL_LICENSE_PROVINCE"));
            String productSt = data.get(position).get("PRO_NAME");
            PRO_NAME.setText(Html.fromHtml(productSt.replace("ลง", "<font color='red'>ลง</font>").replace("ขึ้น", "<font color='blue'>ขึ้น</font>").replace("\n", "<br>")));
            remark.setText(data.get(position).get("Remark_ProductDetail").replace("\\n", "\n"));
            CUSTOMER_NAME.setText(data.get(position).get("CUSTOMER_NAME"));
            url = data.get(position).get("DOCITEM_URL");
            fileUrl = url.substring(40);
            ImageView close = (ImageView) dialog.findViewById(R.id.close);
            dialog.show();
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });
            zDetail.setOnTouchListener(zoomDetail);
            String qrTxt = data.get(position).get("WOHEADER_DOCID") + data.get(position).get("WOITEM_DOCID");
            try {
                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrTxt, null, Content.Type.TEXT, BarcodeFormat.QR_CODE.toString(), 250);
                Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                qrDetail.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.d("Error DetailCode", e.toString());
            }
        }

        public class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            CardView cardItemLayout;
            View btnWorkItem, workStatusBar, detailLayout, btnSignature;
            TextView destTxt, sourceTxt, woitem, product;

            public VersionViewHolder(View itemView) {
                super(itemView);
                cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
                sourceTxt = (TextView) itemView.findViewById(R.id.SourceText);
                destTxt = (TextView) itemView.findViewById(R.id.DestText);
                woitem = (TextView) itemView.findViewById(R.id.woitem);
                product = (TextView) itemView.findViewById(R.id.ProductText);
                btnWorkItem = itemView.findViewById(R.id.btnWorkItem);
                workStatusBar = itemView.findViewById(R.id.workStatusBar);
                detailLayout = itemView.findViewById(R.id.datailLayout);
                btnSignature = itemView.findViewById(R.id.btnSignature);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
            }
        }

        private class DownloadFile extends AsyncTask<String, Void, Void> {
            final ProgressDialog progress = ProgressDialog.show(context, "", "กำลังดาวโหลดเอกสาร", true);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(String... strings) {
                String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
                String fileName = strings[1];  // -> maven.pdf
                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File folder = new File(extStorageDirectory, "/TMS_ITEM/");
                folder.mkdir();

                File pdfFile = new File(folder, fileName);

                try {
                    pdfFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileDownloader.downloadFile(fileUrl, pdfFile);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                File pdfFile = new File(Environment.getExternalStorageDirectory() + "/TMS_ITEM/" + fileUrl);  // -> filename = maven.pdf
                Uri path = Uri.fromFile(pdfFile);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    context.startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("ไม่พบไฟล์เอกสารบนเซิร์ฟเวอร์");
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
                progress.dismiss();
            }
        }

        private View.OnTouchListener zoomDetail = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                int pointerIndex = ev.getPointerCount();
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        TouchOfsWidth = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        try {
                            if (pointerIndex == 2) {
                                if (TouchOfsWidth == 0) {
                                /* Get touch distance */
                                    TouchOfsWidth = (float) Math.sqrt(Math.pow(ev.getX(0) - ev.getX(1), 2) + Math.pow(ev.getY(0) - ev.getY(1), 2));

		    	    			/* Store touch position */
                                    TouchCenter[0] = ((ev.getX(0) + ev.getX(1)) / 2);
                                    TouchCenter[1] = ((ev.getY(0) + ev.getY(1)) / 2);

		    	    			/* Translate zoom center position */
                                    CurrentOfsPos[0] = (TouchCenter[0] - ScreenOfsPos[0]) / CurrentScale;
                                    CurrentOfsPos[1] = (TouchCenter[1] - ScreenOfsPos[1]) / CurrentScale;

		    	    			/* Get offset */
                                    TranslateOfsPos[0] = TouchCenter[0] - CurrentOfsPos[0];
                                    TranslateOfsPos[1] = TouchCenter[1] - CurrentOfsPos[1];

                                    TranslateOfsPosPre[0] = TranslateOfsPos[0];
                                    TranslateOfsPosPre[1] = TranslateOfsPos[1];
                                /* Storage last scale */
                                    CurrentScalePush = CurrentScale;
                                } else {
                                /* Calculate drag */
                                    float TouchX = ((ev.getX(0) + ev.getX(1)) / 2);
                                    float TouchY = ((ev.getY(0) + ev.getY(1)) / 2);

                                    float Dx = TouchX - TouchCenter[0];
                                    float Dy = TouchY - TouchCenter[1];

                                    TranslateOfsPos[0] = TranslateOfsPosPre[0] + Dx;
                                    TranslateOfsPos[1] = TranslateOfsPosPre[1] + Dy;

		    					/* Get new touch distance */
                                    float TouchWidth = (float) Math.sqrt(Math.pow(ev.getX(0) - ev.getX(1), 2) + Math.pow(ev.getY(0) - ev.getY(1), 2));

			    				/* Calculate new scale */
                                    if (Math.abs(TouchOfsWidth - TouchWidth) > 10) {
                                        CurrentScale = CurrentScalePush * (TouchWidth / (TouchOfsWidth));
                                        if (CurrentScale <= 1.0f) {
                                            CurrentScale = 1.0f;
                                        } else if (CurrentScale > 5.0f) {
                                            CurrentScale = 5.0f;
                                        }
                                    }

			    				/* Calculate offset to fit screen */
                                    float NewOfsX = CurrentOfsPos[0] - (CurrentOfsPos[0] * CurrentScale) + TranslateOfsPos[0];
                                    float NewOfsY = CurrentOfsPos[1] - (CurrentOfsPos[1] * CurrentScale) + TranslateOfsPos[1];
                                    float MaxOfsX = NewOfsX + (view.getWidth() * CurrentScale);
                                    float MaxOfsY = NewOfsY + (view.getHeight() * CurrentScale);

                                    if (NewOfsX > 0) {
                                        TranslateOfsPos[0] -= NewOfsX;
                                    }
                                    if (NewOfsY > 0) {
                                        TranslateOfsPos[1] -= NewOfsY;
                                    }

                                    if (MaxOfsX < view.getWidth()) {
                                        TranslateOfsPos[0] += (view.getWidth() - MaxOfsX);
                                    }
                                    if (MaxOfsY < view.getHeight()) {
                                        TranslateOfsPos[1] += (view.getHeight() - MaxOfsY);
                                    }

			    				/* Scale layout */
                                    if (CurrentScale != 1.0f) {
                                        view.setPivotX(CurrentOfsPos[0]);
                                        view.setPivotY(CurrentOfsPos[1]);
                                        view.setScaleX(CurrentScale);
                                        view.setScaleY(CurrentScale);
                                        view.setTranslationX(TranslateOfsPos[0]);
                                        view.setTranslationY(TranslateOfsPos[1]);
                                    } else {
                                    /* Reset position and scale */
                                        view.setScaleX(1);
                                        view.setScaleY(1);
                                        view.setTranslationX(0);
                                        view.setTranslationY(0);

                                        TouchOfsWidth = 0;
                                        CurrentScale = 1;
                                        CurrentScalePush = 1;
                                        Arrays.fill(CurrentOfsPos, 0);
                                        Arrays.fill(ScreenOfsPos, 0);
                                        Arrays.fill(TranslateOfsPos, 0);
                                        Arrays.fill(TranslateOfsPosPre, 0);
                                        Arrays.fill(TouchCenter, 0);
                                    }
                                }
                            } else if (pointerIndex >= 3) {
                            /* Reset position and scale */
                                view.setScaleX(1);
                                view.setScaleY(1);
                                view.setTranslationX(0);
                                view.setTranslationY(0);

                                TouchOfsWidth = 0;
                                CurrentScale = 1;
                                CurrentScalePush = 1;
                                Arrays.fill(CurrentOfsPos, 0);
                                Arrays.fill(ScreenOfsPos, 0);
                                Arrays.fill(TranslateOfsPos, 0);
                                Arrays.fill(TranslateOfsPosPre, 0);
                                Arrays.fill(TouchCenter, 0);
                            }
                        } catch (Exception e) {
                            Log.d("Oceanus", e.getMessage());
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    /* Calculate new offset */
                        ScreenOfsPos[0] = CurrentOfsPos[0] - (CurrentOfsPos[0] * CurrentScale) + TranslateOfsPos[0];
                        ScreenOfsPos[1] = CurrentOfsPos[1] - (CurrentOfsPos[1] * CurrentScale) + TranslateOfsPos[1];
                        TouchOfsWidth = 0;
                        view.invalidate();
                        break;
                }
                return true;
            }
        };

    }

    public void showDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        //alert.setTitle("กรุณาใส่เหตุผล");
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_checkbox, null);
        CheckBox cb = (CheckBox) view.findViewById(R.id.cbInDialog);
        alert.setView(view);
        alert.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                new loadData().execute();
            }
        });
        final AlertDialog alert2 = alert.create();
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
    }

    private class loadData extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("กรุณารอสักครู่");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("TEST", s);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (s.equalsIgnoreCase("true")) {
                new MainActivity.RefreshTask(sp.getString("truckid", ""), sp.getString("empid", ""), getActivity()).execute();
            } else {
                String[] str = s.split("|");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("ผิดพลาด " + str[1] + " \nกรุณาลองใหม่อีกครั้ง");
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
        protected String doInBackground(String... params) {
            return new CallService().Save_closeWork(data.get(0).get("WOHEADER_DOCID"));
        }
    }

}
