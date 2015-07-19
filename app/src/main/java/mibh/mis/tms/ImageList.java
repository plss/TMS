package mibh.mis.tms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import mibh.mis.tms.data.WorkData;
import mibh.mis.tms.database.img_tms;

/**
 * Created by ponlakiss on 06/09/2015.
 */
public class ImageList extends AppCompatActivity {

    RecyclerView recyclerView;
    Adapter adapter;
    CardView listItem;
    private ArrayList<String> data = new ArrayList<>();
    String[] arrName;
    String WOHEADER_DOCID, WOITEM_DOCID, DETAIL, From;
    private static SparseBooleanArray mCheckStates;
    private ArrayList<img_tms.Image_tms> cursor;
    private img_tms ImgTms;
    private int Index;
    private boolean isChoose = false;
    SharedPreferences sp;
    private boolean isLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_list);
        listItem = (CardView) findViewById(R.id.cardimage_item);
        recyclerView = (RecyclerView) findViewById(R.id.image_recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ImageList.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ImgTms = new img_tms(ImageList.this);
        sp = getSharedPreferences("info", Context.MODE_PRIVATE);
        setUpToolbar();

        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("From")) {
            From = extras.getString("From");
            if (From.equals(img_tms.GTYPE_WORK)) {
                arrName = getResources().getStringArray(R.array.list_pic);
                if (extras.containsKey("WOHEADER_DOCID")) {
                    WOHEADER_DOCID = extras.getString("WOHEADER_DOCID");
                }
                if (extras.containsKey("WOITEM_DOCID")) {
                    WOITEM_DOCID = extras.getString("WOITEM_DOCID");
                }
                if (extras.containsKey("DETAIL")) {
                    DETAIL = extras.getString("DETAIL");
                }
                if (extras.containsKey("Index")) {
                    Index = extras.getInt("Index");
                }

            } else if (From.equals(img_tms.GTYPE_FUEL)) {
                arrName = getResources().getStringArray(R.array.usefuel_array);
                if (extras.containsKey("WOHEADER_DOCID")) {
                    WOHEADER_DOCID = extras.getString("WOHEADER_DOCID");
                }
                if (extras.containsKey("DOCID")) {
                    WOITEM_DOCID = extras.getString("DOCID");
                }
                DETAIL = "เติมเชื้อเพลิง";
            } else if (From.equals(img_tms.GTYPE_STGNATURE)) {
                arrName = getResources().getStringArray(R.array.list_signature);
                if (extras.containsKey("WOHEADER_DOCID")) {
                    WOHEADER_DOCID = extras.getString("WOHEADER_DOCID");
                }
                if (extras.containsKey("ITEM")) {
                    WOITEM_DOCID = extras.getString("ITEM");
                }
            }
        }

        mCheckStates = new SparseBooleanArray(arrName.length);

        cursor = ImgTms.Img_GetImageByDoc_itemAndGroupType(WOHEADER_DOCID, From, WOITEM_DOCID);
        for (int i = 0; i < cursor.size(); i++) {
            if (From.equals(img_tms.GTYPE_WORK)) {
                int x = Integer.parseInt(cursor.get(i).Type_img);
                mCheckStates.put(x - 10, true);
            } else if (From.equals(img_tms.GTYPE_FUEL)) {
                int x = Integer.parseInt(cursor.get(i).Type_img);
                mCheckStates.put(x - 30, true);
            } else if (From.equals(img_tms.GTYPE_STGNATURE)) {
                int x = Integer.parseInt(cursor.get(i).Type_img);
                mCheckStates.put(x - 50, true);
            }
        }
        ImgTms.close();
        if (adapter == null) {
            adapter = new Adapter(ImageList.this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    private void setUpToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("DOCID", WOITEM_DOCID);
                    setResult(RESULT_OK, intent);
                    ImageList.this.finish();
                }
            });
        }
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.VersionViewHolder> {

        Context context;

        public Adapter(Context context) {
            this.context = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int position) {
            versionViewHolder.imageTxt.setText(arrName[position]);
            versionViewHolder.imageMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ImageList.this, GridPic.class);
                    intent.putExtra("From", From);
                    intent.putExtra("WOHEADER_DOCID", WOHEADER_DOCID);
                    intent.putExtra("ITEM", WOITEM_DOCID);
                    if (From.equals(img_tms.GTYPE_WORK)) {
                        intent.putExtra("Type_Img", String.valueOf(10 + position));
                    } else if (From.equals(img_tms.GTYPE_FUEL)) {
                        intent.putExtra("Type_Img", String.valueOf(30 + position));
                    } else if (From.equals(img_tms.GTYPE_STGNATURE)) {
                        intent.putExtra("Type_Img", String.valueOf(50 + position));
                    }
                    startActivity(intent);
                }
            });
            versionViewHolder.imageClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sp.getString("latitude", "0").equalsIgnoreCase("0") || sp.getString("longtitude", "0").equalsIgnoreCase("0")
                            || Double.valueOf(sp.getString("latitude", "0")) == 0 || Double.valueOf(sp.getString("longtitude", "0")) == 0) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ImageList.this);
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
                    } else {
                        if (From.equals(img_tms.GTYPE_WORK)) {
                            String result;
                            ArrayList<HashMap<String, String>> work = new WorkData().getWorkData();
                            CheckLocationDist checkDist = new CheckLocationDist();
                            CheckLocationDist.StationCheck clSource = new CheckLocationDist.StationCheck();
                            clSource.vLatLng = work.get(Index).get("SoureLatLng");
                            clSource.vStationName = work.get(Index).get("SOURCE_NAME");
                            clSource.vkeyStation = work.get(Index).get("SOURCE_ID");
                            CheckLocationDist.StationCheck clDest = new CheckLocationDist.StationCheck();
                            clDest.vLatLng = work.get(Index).get("DestLatLng");
                            clDest.vStationName = work.get(Index).get("DEST_NAME");
                            clDest.vkeyStation = work.get(Index).get("DEST_ID");
                            result = checkDist.CheckPointInStaion(clSource, clDest, sp.getString("latitude", "") + "," + sp.getString("longtitude", ""));
                            Log.d("testttt", result + " " + work.get(Index).get("DestLatLng") + " " + sp.getString("latitude", "") + "," + sp.getString("longtitude", ""));
                            if (result == null || result.equalsIgnoreCase("No")) {
                                AlertDialog.Builder builderSingle = new AlertDialog.Builder(ImageList.this);
                                builderSingle.setTitle("กรุณาเลือกสถานที่");
                                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ImageList.this, android.R.layout.select_dialog_singlechoice);
                                arrayAdapter.add(work.get(Index).get("SOURCE_NAME"));
                                arrayAdapter.add(work.get(Index).get("DEST_NAME"));
                                arrayAdapter.add("อื่นๆ");
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
                                                Intent intent = new Intent(ImageList.this, CameraMain.class);
                                                intent.putExtra("From", From);
                                                intent.putExtra("WOHEADER_DOCID", WOHEADER_DOCID);
                                                intent.putExtra("ITEM", WOITEM_DOCID);
                                                Log.d("WOITEM_DOCID", WOITEM_DOCID);
                                                //intent.putExtra("MODE", arrName[position]);
                                                intent.putExtra("MODE", ImgTms.Doc_name(String.valueOf(10 + position)));
                                                intent.putExtra("DETAIL", DETAIL);
                                                intent.putExtra("Type_Img", String.valueOf(10 + position));
                                                //intent.putExtra("STATUS", ImgTms.Doc_name(String.valueOf(10 + position)));
                                                switch (which) {
                                                    case 0:
                                                        strName = "ต้นทาง: " + strName;
                                                        break;
                                                    case 1:
                                                        strName = "ปลายทาง: " + strName;
                                                        break;
                                                }
                                                intent.putExtra("STATUS", strName);
                                                ImageList.this.startActivityForResult(intent, position);
                                            }
                                        });
                                builderSingle.show();
                            } else {
                                Intent intent = new Intent(ImageList.this, CameraMain.class);
                                intent.putExtra("From", From);
                                intent.putExtra("WOHEADER_DOCID", WOHEADER_DOCID);
                                intent.putExtra("ITEM", WOITEM_DOCID);
                                Log.d("WOITEM_DOCID", WOITEM_DOCID);
                                //intent.putExtra("MODE", arrName[position]);
                                intent.putExtra("MODE", ImgTms.Doc_name(String.valueOf(10 + position)));
                                intent.putExtra("DETAIL", DETAIL);
                                intent.putExtra("Type_Img", String.valueOf(10 + position));
                                //intent.putExtra("STATUS", ImgTms.Doc_name(String.valueOf(10 + position)));
                                intent.putExtra("STATUS", result);
                                ImageList.this.startActivityForResult(intent, position);
                            }

                        } else if (From.equals(img_tms.GTYPE_FUEL)) {
                            Intent intent = new Intent(ImageList.this, CameraMain.class);
                            intent.putExtra("From", From);
                            intent.putExtra("WOHEADER_DOCID", WOHEADER_DOCID);
                            intent.putExtra("ITEM", WOITEM_DOCID);
                            Log.d("WOITEM_DOCID", WOITEM_DOCID);
                            intent.putExtra("MODE", ImgTms.Doc_name(String.valueOf(30 + position)));
                            intent.putExtra("DETAIL", DETAIL);
                            intent.putExtra("Type_Img", String.valueOf(30 + position));
                            intent.putExtra("STATUS", ImgTms.Doc_name(String.valueOf(30 + position)));
                            ImageList.this.startActivityForResult(intent, position);
                        } else if (From.equals(img_tms.GTYPE_STGNATURE)) {
                            Intent cam = new Intent(context, Signature_Pad.class);
                            cam.putExtra("WOHEADER_DOCID", WOHEADER_DOCID);
                            cam.putExtra("ITEM", WOITEM_DOCID);
                            cam.putExtra("TYPE_IMG", String.valueOf(50 + position));
                            ImageList.this.startActivityForResult(cam, position);
                        }
                    }

                }
            });
            if (!mCheckStates.get(position, false)) {
                versionViewHolder.imageStat.setImageResource(R.drawable.close_circle);
            } else versionViewHolder.imageStat.setImageResource(R.drawable.marked_circle_green);
        }

        @Override
        public int getItemCount() {
            return arrName == null ? 0 : arrName.length;
        }

        public class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            CardView cardItemLayout;
            TextView imageTxt;
            ImageView imageStat, imageMore;
            View imageClick;

            public VersionViewHolder(View itemView) {
                super(itemView);
                cardItemLayout = (CardView) itemView.findViewById(R.id.cardimage_item);
                imageTxt = (TextView) itemView.findViewById(R.id.imageTxt);
                imageStat = (ImageView) itemView.findViewById(R.id.imagestat);
                imageMore = (ImageView) itemView.findViewById(R.id.imagemore);
                imageClick = itemView.findViewById(R.id.imageClick);
                imageMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            mCheckStates.put(requestCode, true);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("DOCID", WOITEM_DOCID);
                setResult(RESULT_OK, intent);
                ImageList.this.finish();
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
}

