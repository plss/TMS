package mibh.mis.tmsland.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kobjects.base64.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import mibh.mis.tmsland.Login;
import mibh.mis.tmsland.R;
import mibh.mis.tmsland.database.img_tms;

/**
 * Created by ponlakiss on 06/10/2015.
 */

public class UploadPic extends IntentService {

    String fileName, WOHEADER_DOCID, TYPE_IMG, Lat, Lng, Date, Comment, ITEM, Group_Type;
    ArrayList<img_tms.Image_tms> cursor;
    img_tms ImgTms;
    SharedPreferences sp;

    public UploadPic() {
        super("ScheduledService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ImgTms = new img_tms(UploadPic.this);
        sp = getSharedPreferences("info", Context.MODE_PRIVATE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        cursor = ImgTms.Img_GetAllInactive();
        for (int i = 0; i < cursor.size(); i++) {
            fileName = cursor.get(i).Filename;
            WOHEADER_DOCID = cursor.get(i).WorkHid;
            TYPE_IMG = cursor.get(i).Type_img;
            Lat = cursor.get(i).Lat_img;
            Lng = cursor.get(i).Lng_img;
            Date = cursor.get(i).Date_img;
            Comment = cursor.get(i).Comment;
            ITEM = cursor.get(i).Doc_item;
            Group_Type = cursor.get(i).Group_Type;
            String saveResult = savePhoto();
            if (!saveResult.equalsIgnoreCase("error") && !saveResult.equalsIgnoreCase("false")) {
                ImgTms.UpdateByFilename(fileName);
            }
        }
        ImgTms.close();
        if (sp.getBoolean("notification", false)) {
            String result = new CallService().checkNotify(sp.getString("truckid", ""));
            createNotification(result);
        }

    }

    private String savePhoto() {
        String Result = null;
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DCIM/TMS/" + fileName);
        File file = new File(imagesFolder + fileName);
        if (!imagesFolder.exists()) {
            ImgTms.UpdateByFilename(fileName);
        } else {
            try {
                JSONArray array = new JSONArray();

                JSONObject Img_file;
                JSONArray arrayIm_reg = new JSONArray();

                byte[] byteArray;
                String strBase64;

                // ข้อมูล Bitmap
                Img_file = new JSONObject();

                ////////////////////////////////
                FileInputStream fis = new FileInputStream(imagesFolder);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];

                int bytesRead;
                while ((bytesRead = fis.read(b)) != -1) {
                    bos.write(b, 0, bytesRead);
                }
                //byte[] bytes = bos.toByteArray();
                ////////////////////////////////

                /*Bitmap bitmap = BitmapFactory.decodeFile(imagesFolder.getAbsolutePath());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 35, stream);*/
                byteArray = bos.toByteArray();
                strBase64 = Base64.encode(byteArray);
                Img_file.put("file_name", fileName);
                Img_file.put("img_file", strBase64);
                array.put(Img_file);

                //ข้อมูลรูป
                JSONObject dataIm_reg = new JSONObject();
                dataIm_reg.put("Req_id", WOHEADER_DOCID);
                dataIm_reg.put("Type_img", TYPE_IMG);
                dataIm_reg.put("File_name", fileName);
                dataIm_reg.put("Lat", Lat);
                dataIm_reg.put("Lng", Lng);
                dataIm_reg.put("date_image", Date);
                dataIm_reg.put("Status", "Active");
                arrayIm_reg.put(dataIm_reg);

                if (Comment.length() >= 296) {
                    Comment = Comment.substring(0, 295) + "..";
                }
                new CallService().setState(WOHEADER_DOCID, ITEM, sp.getString("truckid", ""), String.format("%.5f,%.5f", Double.parseDouble(Lat), Double.parseDouble(Lng)), "Location not found", Group_Type, TYPE_IMG, sp.getString("empid", ""), sp.getString("firstname", "") + " " + sp.getString("lastname", ""), fileName, Comment);
                Result = new CallService().savePic(array.toString(), arrayIm_reg.toString());
                Log.d("Save pic", Result);
                //bitmap.recycle();
                //bitmap = null;
                return Result;
            } catch (Exception e) {
                Log.d("ErrorSavePhoto", e.toString());
                return "error";
            }
        }
        return "error";
    }

    private void createNotification(String result) {
        if (!result.equals("[]") && !result.equals("null")) {
            try {
/*
                JSONArray data = new JSONArray(result);
                JSONObject c = data.getJSONObject(0);
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new Notification(R.mipmap.ic_launcher, "มีการแจ้งเตือนไหม่", System.currentTimeMillis());
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                String Title = "ข้อความใหม่";
                String[] splitDot = c.getString("CO_ITEM").split("\\.");
                String Message = "มีงานเข้า " + splitDot[0] + " งาน";
                Intent intent = new Intent(this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent activity = PendingIntent.getActivity(this, 0, intent, 0);
                notification.setLatestEventInfo(this, Title, Message, activity);
                //notification.number += 1;
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notification.defaults = Notification.DEFAULT_SOUND;
                notification.defaults = Notification.DEFAULT_VIBRATE;
                notificationManager.notify(0, notification);
*/

                Intent intent = new Intent(this, Login.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                JSONArray data = new JSONArray(result);
                JSONObject c = data.getJSONObject(0);
                String Title = "ข้อความใหม่";
                String[] splitDot = c.getString("CO_ITEM").split("\\.");
                String Message = "มีงานเข้า " + splitDot[0] + " งาน";

                Notification notification =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(Title)
                                .setContentText(Message)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1000, notification);

            } catch (Exception e) {
                Log.d("Error creNotify", e.toString());
            }
        }
    }

}
