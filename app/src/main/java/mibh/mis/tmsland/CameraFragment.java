package mibh.mis.tmsland;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import mibh.mis.tmsland.Cam.CamTestActivity;
import mibh.mis.tmsland.database.img_tms;
import mibh.mis.tmsland.service.validateLatLng;

/**
 * Created by ponlakiss on 08/10/2015.
 */
public class CameraFragment extends Fragment {

    View rootView;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        ImageView btnOtherCam = (ImageView) rootView.findViewById(R.id.btnOtherCam);
        LinearLayout btnOtherImg = (LinearLayout) rootView.findViewById(R.id.btnOtherImg);
        sp = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        btnOtherCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLatLng.check(sp)) {
                    Intent intent = new Intent(getActivity(), CamTestActivity.class);
                    intent.putExtra("From", img_tms.GTYPE_OTHER);
                    intent.putExtra("WOHEADER_DOCID", sp.getString("lastwork", "OTHER"));
                    intent.putExtra("ITEM", "10");
                    intent.putExtra("Type_Img", img_tms.IMG_OTHER);
                    startActivity(intent);
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
        btnOtherImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GridPic.class);
                intent.putExtra("From", img_tms.GTYPE_OTHER);
                intent.putExtra("WOHEADER_DOCID", sp.getString("lastwork", "OTHER"));
                intent.putExtra("ITEM", "10");
                intent.putExtra("Type_Img", img_tms.IMG_OTHER);
                startActivity(intent);
            }
        });
        return rootView;
    }

}
