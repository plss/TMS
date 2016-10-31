package mibh.mis.tmsland;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;

import mibh.mis.tmsland.service.CallService;

/**
 * Created by ponlakiss on 12/03/2015.
 */
public class ScanFragment extends StatedFragment {

    View rootView;
    LinearLayout layoutScan;
    TextView textScanResultHead, textScanResultDetail;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scanqr, container, false);

        layoutScan = (LinearLayout) rootView.findViewById(R.id.layoutScan);
        textScanResultHead = (TextView) rootView.findViewById(R.id.txtScanResult);
        textScanResultDetail = (TextView) rootView.findViewById(R.id.txtScanResult2);

        layoutScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.setPackage("com.google.zxing.client.android");
                intent.putExtra("SCAN_FORMATS", "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF,CODABAR,EAN_13,EAN_8,UPC_A,QR_CODE");
                startActivityForResult(intent, 7);
            }
        });

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TEST", requestCode + " " + resultCode);
        if ((requestCode == 65536 || requestCode == 7) && resultCode == -1) {
            String contents = data.getStringExtra("SCAN_RESULT");
            Log.d("Content", contents);
            String format = data.getStringExtra("SCAN_RESULT_FORMAT");
            textScanResultDetail.setVisibility(View.VISIBLE);
            textScanResultDetail.setText(contents);
            textScanResultHead.setText("สแกนสำเร็จ");
            new SaveRecieveDoc().execute(contents);
            //new Loading(txtSearch.getText().toString().trim()).execute();
        }
    }

    private class SaveRecieveDoc extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return new CallService().Save_ReciveDoc(params[0]);
        }
    }

}
