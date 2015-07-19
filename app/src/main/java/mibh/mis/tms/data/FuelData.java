package mibh.mis.tms.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ponlakiss on 06/08/2015.
 */
public class FuelData {

    private static ArrayList<String> fuelId = new ArrayList<>();
    private static ArrayList<Boolean> fuelState = new ArrayList<>();
    public static String DocId;
    public static HashMap<String,Boolean> hash = new HashMap<>();

    public void convert(String result, String docId) {
        try {
            JSONArray data = new JSONArray(result);
            for (int i = 0; i < data.length(); ++i) {
                JSONObject c = data.getJSONObject(i);
                fuelId.add(i, c.getString("FUEL_ID"));
            }
            DocId = docId;
        } catch (Exception e) {
            Log.d("Error convert Fuel", e.toString());
            fuelId.clear();
        }
    }

    public void addFuelId(String fuelid){
        fuelId.add(fuelid);
    }

    public ArrayList<String> getFuelData() {
        return fuelId;
    }

    public void clearFuelData() {
        fuelId.clear();
        hash.clear();
        DocId="";
    }

    public String getDocId() {
        return DocId;
    }

    public String getFuelIdIndex(int index) {
        return fuelId.get(index);
    }

    public void setFuelState(String docId) {
        if(!hash.containsKey(docId)){
            hash.put(docId,true);
        }
    }

    public void setFuelState(int position) {
        if(!hash.containsKey(fuelId.get(position))){
            hash.put(fuelId.get(position),true);
        }
    }

    public boolean getFuelState(int position) {
        if(hash.containsKey(fuelId.get(position))){
            return hash.get(fuelId.get(position));
        }
        else return false;
    }
}
