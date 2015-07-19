package mibh.mis.tms.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kobjects.base64.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import mibh.mis.tms.database.img_tms;

/**
 * Created by ponlakiss on 06/10/2015.
 */

public class UploadPic extends AsyncTask<Object, Void, Object> {

    Context context;
    String fileName, WOHEADER_DOCID, TYPE_IMG, Lat, Lng, Date, Comment;
    ArrayList<img_tms.Image_tms> cursor;
    img_tms ImgTms;

    public UploadPic(Context context) {
        this.context = context;
        ImgTms = new img_tms(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        cursor = ImgTms.Img_GetAllInactive();
        for (int i = 0; i < cursor.size(); i++) {
            fileName = cursor.get(i).Filename;
            WOHEADER_DOCID = cursor.get(i).WorkHid;
            TYPE_IMG = cursor.get(i).Type_img;
            Lat = cursor.get(i).Lat_img;
            Lng = cursor.get(i).Lng_img;
            Date = cursor.get(i).Date_img;
            Comment = cursor.get(i).Comment;
            Log.d("getAll " + i, cursor.get(i).Doc_item);
            Log.d("getAll " + i, cursor.get(i).Type_img);
            Log.d("getAll " + i, cursor.get(i).Date_img);
            Log.d("getAll " + i, cursor.get(i).Filename);
            //Log.d("getAll "+i,cursor.get(i).Group_Type);
            Log.d("getAll " + i, cursor.get(i).Lat_img);
            Log.d("getAll " + i, cursor.get(i).Lng_img);
            Log.d("getAll " + i, cursor.get(i).WorkHid);
            Log.d("getAll " + i, cursor.get(i).Stat_Upd);
            if (!savePhoto().equals("error")) {
                ImgTms.UpdateByFilename(fileName);
            }
        }
        ImgTms.close();
        return null;
    }

    private String savePhoto() {
        String Result = null;
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DCIM/TMS/" + fileName);
        File file = new File(imagesFolder + fileName);
        //if (file.exists()) {
        try {
            JSONArray array = new JSONArray();
            JSONObject dataIm_reg = new JSONObject();

            JSONObject Img_file;
            JSONArray arrayIm_reg = new JSONArray();//Table Image

            Bitmap bitmap = null;
            ByteArrayOutputStream stream = null;
            byte[] byteArray;
            String strBase64;

            // ข้อมูล Bitmap
            Img_file = new JSONObject();
            bitmap = BitmapFactory.decodeFile(imagesFolder.getAbsolutePath());
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byteArray = stream.toByteArray();
            strBase64 = Base64.encode(byteArray);
            Img_file.put("file_name", fileName);
            Img_file.put("img_file", strBase64);

            array.put(Img_file);

            //ข้อมูลรูป
            dataIm_reg = new JSONObject();
            dataIm_reg.put("Req_id", WOHEADER_DOCID);
            dataIm_reg.put("Type_img", TYPE_IMG);
            dataIm_reg.put("File_name", fileName);
            dataIm_reg.put("Lat", Lat);
            dataIm_reg.put("Lng", Lng);
            dataIm_reg.put("date_image", Date);
            dataIm_reg.put("Status", "Active");
            arrayIm_reg.put(dataIm_reg);
            Result = new CallService().savePic(array.toString(), arrayIm_reg.toString());
            Log.d("Save pic", Result);
            return Result;
        } catch (Exception e) {
            Log.d("Error save photo", e.toString());
            return "error";
        }
    }
}
