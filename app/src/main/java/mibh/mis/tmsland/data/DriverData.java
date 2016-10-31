package mibh.mis.tmsland.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ponlakiss on 09/25/2015.
 */
public class DriverData {

    private String EMP_ID;
    private String F_NAME;
    private String L_NAME;
    private String TEL;

    public DriverData() {
        this.EMP_ID = "";
        this.F_NAME = "";
        this.L_NAME = "";
        this.TEL = "";
    }

    public void convert(String result) {
        try {
            JSONArray data = new JSONArray(result);
            JSONObject c = data.getJSONObject(0);
            EMP_ID = c.getString("EMP_ID");
            F_NAME = c.getString("F_NAME");
            L_NAME = c.getString("L_NAME");
            TEL = c.getString("TEL");
        } catch (Exception e) {
            Log.d("Error convert Driver", e.toString());
        }
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public void setF_NAME(String F_NAME) {
        this.F_NAME = F_NAME;
    }

    public void setL_NAME(String L_NAME) {
        this.L_NAME = L_NAME;
    }

    public void setTEL(String TEL) {
        this.TEL = TEL;
    }

    public String getEMP_ID() {
        return EMP_ID;
    }

    public String getF_NAME() {
        return F_NAME;
    }

    public String getL_NAME() {
        return L_NAME;
    }

    public String getTEL() {
        return TEL;
    }

}
