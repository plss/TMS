package mibh.mis.tms.service;

import android.content.SharedPreferences;

/**
 * Created by ponlakiss on 09/28/2015.
 */
public class validateLatLng {
    public static boolean check(SharedPreferences sp){
        if (sp.getString("latitude", "0").equalsIgnoreCase("0") || sp.getString("longtitude", "0").equalsIgnoreCase("0")
                || Double.valueOf(sp.getString("latitude", "0")) == 0 || Double.valueOf(sp.getString("longtitude", "0")) == 0) {
            return false;
        } else {
            return true;
        }
    }
}
