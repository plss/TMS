package mibh.mis.tmsland;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import mibh.mis.tmsland.database.img_tms;

/**
 * Created by ponlakiss on 06/09/2015.
 */

public class GridPic extends AppCompatActivity {

    private SparseBooleanArray mCheckStates;
    private Uri uri;
    private String WOHEADER_DOCID, from, ITEM, TYPE_IMG;
    private ArrayList<img_tms.Image_tms> Arr;
    private ArrayList<String> arrFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_pic);
        final GridView gridview = (GridView) findViewById(R.id.gridview);
        setUpToolbar();
        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("WOHEADER_DOCID")) {
            WOHEADER_DOCID = extras.getString("WOHEADER_DOCID");
        }
        if (extras.containsKey("From")) {
            from = extras.getString("From");
        }
        if (extras.containsKey("ITEM")) {
            ITEM = extras.getString("ITEM");
        }
        if (extras.containsKey("Type_Img")) {
            TYPE_IMG = extras.getString("Type_Img");
        }

        img_tms ImgTms = new img_tms(GridPic.this);
        //ArrayList<img_tms.Image_tms> arrTemp;
        if (from.equalsIgnoreCase(img_tms.GTYPE_OTHER)) {
            Arr = ImgTms.Img_GetImageByGroupType(from);
        } else {
            Arr = ImgTms.Img_GetImageByDoc_itemAndGroupTypeAndImg_Type(WOHEADER_DOCID, from, ITEM, TYPE_IMG);
        }



        /*Log.d("TEST Grid", WOHEADER_DOCID + " " + from + " " + ITEM + " " + TYPE_IMG);
        for (int i = 0; i < Arr.size(); ++i) {
            Log.d("TEST " + i, Arr.get(i).Doc_item + " " + Arr.get(i).Type_img);
        }*/
        ImgTms.close();
        mCheckStates = new SparseBooleanArray(Arr.size());
        gridview.setAdapter(new ImageAdapter(this));


        Log.d("TEST IMG", from + " " + TYPE_IMG);
        /*gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(!mCheckStates.get(position,false)){
                    mCheckStates.put(position,true);
                }else {
                    mCheckStates.put(position,false);
                }
                *//*Toast.makeText(GridPic.this, "" + position, Toast.LENGTH_SHORT).show();
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DCIM/TMS");
                //File output = new File(imagesFolder, "IWORK_20150605115625_3TH087.jpg");
                File output = new File(imagesFolder, Arr.get(position).Filename);
                Uri url = Uri.fromFile(output);
                Intent intent = new Intent(Intent.ACTION_SEND);
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url.toString()));
                intent.setType(mimeType);
                intent.putExtra(Intent.EXTRA_STREAM, url);
                startActivity(Intent.createChooser(intent, "Share image via"));*//*
            }
        });*/
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public ImageAdapter(Context c) {
            mContext = c;
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return Arr.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.grid_item, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (mCheckStates.get(id, false)) {
                        cb.setChecked(false);
                        mCheckStates.put(id, false);
                    } else {
                        cb.setChecked(true);
                        mCheckStates.put(id, true);
                    }
                }
            });

            final File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DCIM/TMS");
            File output = new File(imagesFolder, Arr.get(position).Filename);
            uri = Uri.fromFile(output);
            /*if (output.exists()) {
                Log.d("test", "YES" + Arr.get(position).Filename);
            }*/
            holder.imageview.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int id = v.getId();
                    File output = new File(imagesFolder, Arr.get(id).Filename);
                    Uri uri2 = Uri.fromFile(output);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri2, "image/*");
                    startActivity(intent);
                }
            });
            Picasso.with(mContext).load(uri).noFade().resize(240, 320).into(holder.imageview);
            //holder.imageview.setImageURI(uri);
            holder.checkbox.setChecked(mCheckStates.get(position, false));
            holder.id = position;
            return convertView;
            /*File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DCIM/TMS");
            //File output = new File(imagesFolder, "IWORK_20150605115625_3TH087.jpg");
            File output = new File(imagesFolder, Arr.get(position).Filename);
            uri = Uri.fromFile(output);
            imageView.setImageURI(uri);
            //imageView.setImageResource(mThumbIds[position]);
            return relative;*/

        }
    }

    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.grid_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.line_share:

                ArrayList<Uri> imageUris = new ArrayList<Uri>();
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DCIM/TMS");
                Uri newUri;

                for (int i = 0; i < Arr.size(); i++) {
                    if (mCheckStates.get(i, false)) {
                        File output = new File(imagesFolder, Arr.get(i).Filename);
                        if (output.exists()) {
                            System.gc();
                            newUri = Uri.fromFile(output);
                            imageUris.add(newUri);
                        }
                    }
                }
                try {
                    //Intent intent = getPackageManager().getLaunchIntentForPackage();
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    shareIntent.setPackage("jp.naver.line.android");
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent, "Share images to.."));
                } catch (Exception e) {
                }
                break;
            case android.R.id.home:
                GridPic.this.finish();
        }
        return (super.onOptionsItemSelected(menuItem));
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
                    setResult(RESULT_OK, intent);
                    GridPic.this.finish();
                }
            });
        }
    }
}
