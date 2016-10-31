package mibh.mis.tmsland;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import mibh.mis.tmsland.service.FileDownloader;

/**
 * Created by ponlakiss on 06/09/2015.
 */
public class DocfuelPage extends Fragment {
    TextView downloadStatus;
    ProgressBar downloadProgress;
    SharedPreferences sp;
    String url, fileUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.doc_preview, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sp = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        downloadStatus = (TextView) rootView.findViewById(R.id.downloadStatus);
        downloadProgress = (ProgressBar) rootView.findViewById(R.id.downloadProgress);
        downloadProgress.setVisibility(View.INVISIBLE);
        url = sp.getString("DOCFUEL_URL", "");
        fileUrl = url.substring(40);
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/TMS_FUEL/" + fileUrl);
        Uri path = Uri.fromFile(pdfFile);
        Log.d("test path", pdfFile.exists() + " " + pdfFile.toString());
        if(pdfFile.exists()){
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try{
                startActivity(pdfIntent);
                downloadStatus.setText("เปิดไฟล์เอกสาร");
            }catch(ActivityNotFoundException e){
                //Toast.makeText(getActivity(), "ไฟล์เอกสารไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
                new DownloadFile().execute(url,fileUrl);
            }
        }
        else new DownloadFile().execute(url,fileUrl);

        return rootView;
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloadStatus.setText("กำลังดาวน์โหลดเอกสาร");
            downloadProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "/TMS_FUEL/");
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
            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/TMS_HEADER/" + fileUrl);  // -> filename = maven.pdf
            Uri path = Uri.fromFile(pdfFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                openDialog();
                //Toast.makeText(getActivity(), "ไฟล์เอกสารไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
            }
            downloadStatus.setText("เปิดไฟล์เอกสาร");
            downloadProgress.setVisibility(View.INVISIBLE);
        }
    }

    private void openDialog() {
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

}
