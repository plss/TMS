package mibh.mis.tms;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;

import mibh.mis.tms.database.img_tms;
import mibh.mis.tms.service.CallService;

/**
 * Created by ponlakiss on 06/12/2015.
 */
public class Signature_Pad extends Activity {

    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private img_tms ImgTms;
    private SharedPreferences sp;
    private String WOHEADER_DOCID,ITEM,fileName,TYPE_IMG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signature_pad);
        ImgTms = new img_tms(Signature_Pad.this);
        sp = getSharedPreferences("info", Context.MODE_PRIVATE);
        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);

        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("WOHEADER_DOCID")) {
            WOHEADER_DOCID = extras.getString("WOHEADER_DOCID");
        }
        if (extras.containsKey("ITEM")) {
            ITEM = extras.getString("ITEM");
        }
        if (extras.containsKey("TYPE_IMG")) {
            TYPE_IMG = extras.getString("TYPE_IMG");
        }

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = (Button) findViewById(R.id.clear_button);
        mSaveButton = (Button) findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                save(signatureBitmap);
                finish();
                /*if (addSignatureToGallery(signatureBitmap)) {
                    Toast.makeText(Signature_Pad.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Signature_Pad.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

    }

    private void save(Bitmap bmp){
        int imageNum = 0;
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DCIM/TMS");
        imagesFolder.mkdirs();
        //String fileName = "IMG_" + String.valueOf(imageNum) + ".jpg";

        fileName = ImgTms.Gen_imgName(sp.getString("truckid",""),ImgTms.GTYPE_WORK);

        File output = new File(imagesFolder, fileName);
        Log.i("PATH", output.toString());
        while (output.exists()) {
            imageNum++;
            fileName += "_" + String.valueOf(imageNum);
        }
        //fileName += ".jpg";
        output = new File(imagesFolder, fileName);

        Uri uri = Uri.fromFile(output);
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        ContentValues image = new ContentValues();
        String dateTaken = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        image.put(MediaStore.Images.Media.TITLE, output.toString());
        image.put(MediaStore.Images.Media.DISPLAY_NAME, output.toString());
        image.put(MediaStore.Images.Media.DATE_ADDED, dateTaken);
        image.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
        image.put(MediaStore.Images.Media.DATE_MODIFIED, dateTaken);
        image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        image.put(MediaStore.Images.Media.ORIENTATION, 90);
        String path = output.getParentFile().toString().toLowerCase();
        String name = output.getParentFile().getName().toLowerCase();
        image.put(MediaStore.Images.ImageColumns.BUCKET_ID, path.hashCode());
        image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
        image.put(MediaStore.Images.Media.SIZE, output.length());
        image.put(MediaStore.Images.Media.DATA, output.getAbsolutePath());

        OutputStream os;

        try {
            os = getContentResolver().openOutputStream(uri);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.d("", e.toString());
        }

        ImgTms.SaveImg(WOHEADER_DOCID, ImgTms.GTYPE_STGNATURE, TYPE_IMG, fileName, ITEM, sp.getString("latitude", "0"), sp.getString("longtitude", "0"),"");
        ImgTms.close();
        new Loading().execute();
        Intent intent = new Intent();
        intent.putExtra("fileName", fileName);
        setResult(RESULT_OK, intent);
        Signature_Pad.this.finish();
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(photo);
            mediaScanIntent.setData(contentUri);
            Signature_Pad.this.sendBroadcast(mediaScanIntent);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private class Loading extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            String loName;
            if (sp.getString("locationname", "").length() > 200) {
                loName = sp.getString("locationname", "").substring(0, 198);
            } else loName = sp.getString("locationname", "");
            String result = new CallService().setState(WOHEADER_DOCID, ITEM, sp.getString("truckid", ""), sp.getString("latitude", "0") + "," + sp.getString("longtitude", "0"), loName, img_tms.GTYPE_STGNATURE, TYPE_IMG, sp.getString("empid", ""), sp.getString("firstname", "") + " " + sp.getString("lastname", ""),fileName,"");
            Log.d("Result savestate", result);
            return null;
        }
    }

}
