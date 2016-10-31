package mibh.mis.tmsland.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ponlakiss on 09/16/2015.
 */
public class LastworkData {
    private static String truckID = "";
    private static String lastWork = "";
    private static String companyID = "";

    public void convert(String result) {
        try {
            JSONArray data = new JSONArray(result);
            JSONObject c = data.getJSONObject(0);
            truckID = c.getString("TRUCK_ID");
            lastWork = c.getString("LAST_WORK");
            companyID = c.getString("COMPANY_ID");
        } catch (Exception e) {
            Log.d("Error convert Lastwork", e.toString());
        }
    }

    public String getTruckID() {
        return truckID;
    }

    public String getLastWork() {
        return lastWork;
    }

    public String getCompanyID() {
        return companyID;
    }

}
