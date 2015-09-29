package mibh.mis.tms.service;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by ponlakiss on 08/18/2015.
 */
public class Multifunction {

    public Context context;

    public Multifunction(Context context) {
        this.context = context;
    }

    public void mCreateAndSaveFile(String params, String mJsonResponse) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(params, Context.MODE_PRIVATE));
            outputStreamWriter.write(mJsonResponse);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.d("Error write", e.toString());
        }
    }

    public String mReadJsonData(String params) {
        String mResponse = "";
        try {
            /*File f = new File(context.getFilesDir().getPath() + context + "/" + params);
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String mResponse = new String(buffer);*/


            InputStream inputStream = context.openFileInput(params);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                mResponse = stringBuilder.toString();
            }

            return mResponse;
        } catch (IOException e) {
            Log.d("Error read", e.toString());
            return "error";
        }
    }
}
