package mibh.mis.tms.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkData {

    private static ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
    private final String partUrl = "http://www.mibholding.com/App_Store/TMS/";

    public void convert(String result) {
        //Log.d("test work", result);
        try {
            JSONArray data = new JSONArray(result);
            HashMap<String, String> map;
            String pre = "", post = "";
            String pro = "";
            int count = 0;
            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                if (i + 1 == data.length()) {
                    post = "";
                } else {
                    JSONObject c2 = data.getJSONObject(i + 1);
                    post = c2.getString("WOITEM_DOCID");
                }

                if (!c.getString("WOITEM_DOCID").equals(post)) {
                    map = new HashMap<String, String>();
                    if (count == 0) {
                        pro = c.getString("PRO_NAME");
                    } else {
                        Log.i("true", c.getString("STATUS_LOAD"));
                        pro += ("" + (++count) + ". " + c.getString("PRO_NAME") + "");
                    }
                    if (c.getString("STATUS_LOAD").equalsIgnoreCase("true")) {
                        pro += " ขึ้น";
                    }
                    if (c.getString("STATUS_UNLOAD").equalsIgnoreCase("true")) {
                        pro += " ลง";
                    }
                    pro += "";
                    map.put("WOHEADER_DOCID", c.getString("WOHEADER_DOCID"));
                    map.put("WOHEADER_OPEN", c.getString("WOHEADER_OPEN"));
                    map.put("COMPANY_ID", c.getString("COMPANY_ID"));
                    map.put("COMPANY_NAME", c.getString("COMPANY_NAME"));
                    map.put("WOITEM_DOCID", c.getString("WOITEM_DOCID"));
                    map.put("SOURCE_ID", c.getString("SOURCE_ID"));
                    map.put("SOURCE_NAME", c.getString("SOURCE_NAME"));
                    map.put("DEST_ID", c.getString("DEST_ID"));
                    map.put("DEST_NAME", c.getString("DEST_NAME"));
                    map.put("DISTANCE_PLAN", c.getString("DISTANCE_PLAN"));
                    map.put("WOITEMTRUCK_DOCID", c.getString("WOITEMTRUCK_DOCID"));
                    map.put("TRUCK_ID", c.getString("TRUCK_ID"));
                    map.put("TRUCK_LICENSE", c.getString("TRUCK_LICENSE"));
                    map.put("TRUCK_LICENSE_PROVINCE", c.getString("TRUCK_LICENSE_PROVINCE"));
                    map.put("TAIL_ID", c.getString("TAIL_ID"));
                    map.put("TAIL_LICENSE", c.getString("TAIL_LICENSE"));
                    map.put("TAIL_LICENSE_PROVINCE", c.getString("TAIL_LICENSE_PROVINCE"));
                    map.put("EMP_ID", c.getString("EMP_ID"));
                    map.put("DRIVER_FIRSTNAME", c.getString("DRIVER_FIRSTNAME"));
                    map.put("DRIVER_LASTNAME", c.getString("DRIVER_LASTNAME"));
                    map.put("PRO_ID", c.getString("PRO_ID"));
                    map.put("PRO_NAME", pro);
                    map.put("PRO_UNIT_NAME", c.getString("PRO_UNIT_NAME"));
                    map.put("CUSTOMER_NAME", c.getString("CUSTOMER_NAME"));
                    map.put("STATUS_LOAD", c.getString("STATUS_LOAD"));
                    map.put("STATUS_UNLOAD", c.getString("STATUS_UNLOAD"));
                    map.put("PLANSTARTSOURCE", c.getString("PLANSTARTSOURCE"));
                    map.put("PLANSTARTWORK", c.getString("PLANSTARTWORK"));
                    map.put("PLANARRIVEDEST", c.getString("PLANARRIVEDEST"));
                    map.put("Remark_ProductDetail", c.getString("Remark_ProductDetail"));
                    map.put("DOCHEADER_URL", partUrl + c.getString("WOHEADER_DOCID") + "_HEADER.pdf");
                    map.put("DOCITEM_URL", partUrl + c.getString("WOHEADER_DOCID") + "_ITEM.pdf");
                    map.put("DOCFUEL_URL", partUrl + c.getString("WOHEADER_DOCID") + "_FUEL.pdf");
                    map.put("SoureLatLng", c.getString("SoureLatLng"));
                    map.put("DestLatLng", c.getString("DestLatLng"));
                    WorkData.data.add(map);
                    pro = "";
                    count = 0;
                } else {
                    Log.i("true2", c.getString("STATUS_LOAD"));
                    pro += ("" + (++count) + ". " + c.getString("PRO_NAME") + "");
                    if (c.getString("STATUS_LOAD").equalsIgnoreCase("true")) {
                        pro += " ขึ้น";
                    }
                    if (c.getString("STATUS_UNLOAD").equalsIgnoreCase("true")) {
                        pro += " ลง";
                    }
                    pro += "\n";
                }
            }
        } catch (Exception e) {
            WorkData.data.clear();
            Log.d("Error ResultWork", e.toString());
        }
    }

    public void clearWorkData() {
        data.clear();
    }

    public ArrayList<HashMap<String, String>> getWorkData() {
        return data;
    }
}
