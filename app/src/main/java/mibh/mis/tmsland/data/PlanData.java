package mibh.mis.tmsland.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ponlakiss on 06/06/2015.
 */
public class PlanData {

    private static ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

    public void convert(String result) {
        try {
            JSONArray data = new JSONArray(result);
            Log.d("SIZE PLAN", data.length() + result);
            HashMap<String, String> map;
            String pre = "", post = "";
            String pro = "";
            int count = 0;
            JSONObject test = data.getJSONObject(0);
            Log.d("test noPlan", test.getString("PLHEADERDOCID").equalsIgnoreCase("NOPLAN") + "");
            Log.d("test noPlan2", test.getString("EMP_ID") + " " + test.getString("PLHEADERTRUCK") + " " + test.getString("F_NAME") + " " + test.getString("L_NAME"));
            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                if (i + 1 == data.length()) {
                    post = "";
                } else {
                    JSONObject c2 = data.getJSONObject(i + 1);
                    post = c2.getString("PLITEMDOCID");
                }

                if (!c.getString("PLITEMDOCID").equals(post)) {
                    map = new HashMap<String, String>();
                    if (count == 0) {
                        pro = c.getString("PLITEMPRODUCTPRONAME");
                    } else {
                        Log.i("true", c.getString("PLITEMPRODUCTSOURCELOAD"));
                        pro += ("" + (++count) + ". " + c.getString("PLITEMPRODUCTPRONAME") + "");
                    }
                    if (c.getString("PLITEMPRODUCTSOURCELOAD").equalsIgnoreCase("true")) {
                        pro += " ขึ้น";
                    }
                    if (c.getString("PLITEMPRODUCTDESTUNLOAD").equalsIgnoreCase("true")) {
                        pro += " ลง";
                    }
                    pro += "";

                    map.put("PLHEADERDOCID", c.getString("PLHEADERDOCID"));
                    map.put("PLHEADERDATESTARTPLAN", c.getString("PLHEADERDATESTARTPLAN"));
                    map.put("PLHEADERDATEENDPLAN", c.getString("PLHEADERDATEENDPLAN"));
                    map.put("PLHEADERREMARK", c.getString("PLHEADERREMARK"));
                    map.put("PLHEADEREMPOPEN", c.getString("PLHEADEREMPOPEN"));
                    map.put("PLHEADERSTATUS", c.getString("PLHEADERSTATUS"));
                    map.put("PLHEADERAMTITEMPLAN", c.getString("PLHEADERAMTITEMPLAN"));
                    map.put("PLHEADERDATECREATE", c.getString("PLHEADERDATECREATE"));
                    map.put("PLHEADERCOMPANYIDPLAN", c.getString("PLHEADERCOMPANYIDPLAN"));
                    map.put("PLHEADERCOMPANYNAMEPLAN", c.getString("PLHEADERCOMPANYNAMEPLAN"));
                    map.put("PLHEADERFLEETID", c.getString("PLHEADERFLEETID"));
                    map.put("PLHEADERFLEETNAME", c.getString("PLHEADERFLEETNAME"));
                    map.put("PLHEADERTRUCK", c.getString("PLHEADERTRUCK"));
                    map.put("PLHEADERDISTANCE", c.getString("PLHEADERDISTANCE"));
                    map.put("PLHEADERFUEL", c.getString("PLHEADERFUEL"));
                    map.put("PLITEMDOCID", c.getString("PLITEMDOCID"));
                    map.put("PLITEMITEMDISPLAY", c.getString("PLITEMITEMDISPLAY"));
                    map.put("PLITEMSOURCEID", c.getString("PLITEMSOURCEID"));
                    map.put("PLITEMDESTID", c.getString("PLITEMDESTID"));
                    map.put("PLITEMDISTANCEPLAN", c.getString("PLITEMDISTANCEPLAN"));
                    map.put("PLITEMFUELUSEDPLAN", c.getString("PLITEMFUELUSEDPLAN"));
                    map.put("PLITEMFUELRATEPLAN", c.getString("PLITEMFUELRATEPLAN"));
                    map.put("PLITEMEMPPLAN", c.getString("PLITEMEMPPLAN"));
                    map.put("PLITEMPLANOPEN", c.getString("PLITEMPLANOPEN"));
                    map.put("PLITEMSOURCENAME", c.getString("PLITEMSOURCENAME"));
                    map.put("PLITEMDESTNAME", c.getString("PLITEMDESTNAME"));
                    map.put("PLITEMREMARK", c.getString("PLITEMREMARK"));
                    map.put("PLITEMSTATUS", c.getString("PLITEMSTATUS"));
                    map.put("PLITEMAMTPRODUCT", c.getString("PLITEMAMTPRODUCT"));
                    map.put("ORHEADERDOCID", c.getString("ORHEADERDOCID"));
                    map.put("PLITEMPRODUCTPROID", c.getString("PLITEMPRODUCTPROID"));
                    map.put("PLITEMPRODUCTPROUNITID", c.getString("PLITEMPRODUCTPROUNITID"));
                    map.put("PLITEMPRODUCTSOURCEIDLOAD", c.getString("PLITEMPRODUCTSOURCEIDLOAD"));
                    map.put("PLITEMPRODUCTDESTIDUNLOAD", c.getString("PLITEMPRODUCTDESTIDUNLOAD"));
                    map.put("PLITEMPRODUCTLOADING", c.getString("PLITEMPRODUCTLOADING"));
                    map.put("PLITEMPRODUCTSOURCELOAD", c.getString("PLITEMPRODUCTSOURCELOAD"));
                    map.put("PLITEMPRODUCTDESTUNLOAD", c.getString("PLITEMPRODUCTDESTUNLOAD"));
                    map.put("PLITEMPRODUCTSTATUS", c.getString("PLITEMPRODUCTSTATUS"));
                    map.put("PLITEMPRODUCTOPEN", c.getString("PLITEMPRODUCTOPEN"));
                    map.put("PLITEMPRODUCTCLOSE", c.getString("PLITEMPRODUCTCLOSE"));
                    map.put("PLITEMPRODUCTEMPOPEN", c.getString("PLITEMPRODUCTEMPOPEN"));
                    map.put("PLITEMPRODUCTEMPCLOSE", c.getString("PLITEMPRODUCTEMPCLOSE"));
                    map.put("PLITEMPRODUCTPRONAME", pro);
                    map.put("PLITEMPRODUCTPROUNITNAME", c.getString("PLITEMPRODUCTPROUNITNAME"));
                    map.put("PLITEMPRODUCTSOURCENAMELOAD", c.getString("PLITEMPRODUCTSOURCENAMELOAD"));
                    map.put("PLITEMPRODUCTDESTNAMEUNLOAD", c.getString("PLITEMPRODUCTDESTNAMEUNLOAD"));
                    map.put("PLITEMPRODUCTREMARK", c.getString("PLITEMPRODUCTREMARK"));
                    map.put("PLITEMPRODUCTPAYMENTALLOWANCE", c.getString("PLITEMPRODUCTPAYMENTALLOWANCE"));
                    map.put("PLITEMPRODUCTUNITIDALLOWANCE", c.getString("PLITEMPRODUCTUNITIDALLOWANCE"));
                    map.put("PLITEMPRODUCTUNITNAMEALLOWANCE", c.getString("PLITEMPRODUCTUNITNAMEALLOWANCE"));
                    map.put("PLITEMPRODUCTWORKTYPEID", c.getString("PLITEMPRODUCTWORKTYPEID"));
                    map.put("PLITEMPRODUCTWORKTYPENAME", c.getString("PLITEMPRODUCTWORKTYPENAME"));
                    map.put("PLCUSTOMERCUSTID", c.getString("PLCUSTOMERCUSTID"));
                    map.put("PLCUSTOMERPROID", c.getString("PLCUSTOMERPROID"));
                    map.put("PLCUSTOMERTCUNITID", c.getString("PLCUSTOMERTCUNITID"));
                    map.put("PLCUSTOMERCUSTNAME", c.getString("PLCUSTOMERCUSTNAME"));
                    map.put("PLCUSTOMERTCUNITNAME", c.getString("PLCUSTOMERTCUNITNAME"));
                    map.put("PLCUSTOMERSOURCEID", c.getString("PLCUSTOMERSOURCEID"));
                    map.put("PLCUSTOMERDESTID", c.getString("PLCUSTOMERDESTID"));
                    map.put("PLCUSTOMERDISTANCETC", c.getString("PLCUSTOMERDISTANCETC"));
                    map.put("PLCUSTOMERPRONAME", c.getString("PLCUSTOMERPRONAME"));
                    map.put("PLCUSTOMERSOURCENAME", c.getString("PLCUSTOMERSOURCENAME"));
                    map.put("PLCUSTOMERDESTNAME", c.getString("PLCUSTOMERDESTNAME"));
                    map.put("PLCUSTOMERSTATUS", c.getString("PLCUSTOMERSTATUS"));
                    map.put("PLCUSTOMERREMARK", c.getString("PLCUSTOMERREMARK"));
                    map.put("EMP_ID", c.getString("EMP_ID"));
                    map.put("DRIVERID1", c.getString("DRIVERID1"));
                    map.put("DRIVERID2", c.getString("DRIVERID2"));
                    map.put("DOCHEADER_URL", c.getString("DOCHEADER_URL"));
                    map.put("DOCITEM_URL", c.getString("DOCITEM_URL"));
                    map.put("DOCFUEL_URL", c.getString("DOCFUEL_URL"));
                    map.put("F_NAME", c.getString("F_NAME"));
                    map.put("L_NAME", c.getString("L_NAME"));
                    PlanData.data.add(map);
                    pro = "";
                    count = 0;
                } else {
                    Log.i("true2", c.getString("PLITEMPRODUCTSOURCELOAD"));
                    pro += ("" + (++count) + ". " + c.getString("PLITEMPRODUCTPRONAME") + "");
                    if (c.getString("PLITEMPRODUCTSOURCELOAD").equalsIgnoreCase("true")) {
                        pro += " ขึ้น";
                    }
                    if (c.getString("PLITEMPRODUCTDESTUNLOAD").equalsIgnoreCase("true")) {
                        pro += " ลง";
                    }
                    pro += "\n";
                }
            }
        } catch (Exception e) {
            PlanData.data.clear();
            Log.d("Error ResultPlan", e.toString());
        }

    }

    public void clearPlanData() {
        data.clear();
    }

    public ArrayList<HashMap<String, String>> getPlanData() {
        return PlanData.data;
    }
}
