package mibh.mis.tms;

/**
 * Created by ponlakiss on 06/12/2015.
 */
public class CheckLocationDist {

    /****************************< Function comment >*************************/
    /** NAME		 : µÃÇ¨ÊÍºÃÐÂÐ ËèÒ§¢Í§¨Ø´			                                      	**/
    /** PARAMETERS	 : StationCheck Class Ê¶Ò¹Õ		                                           	**/
    /** RETURN VALUE : near station ( ¹éÍÂ¡ÇèÒ 1500 m)                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */

    public String CheckPointInStaion(StationCheck vSt1, StationCheck vSt2, String vLatLng) {
        double dif1, dif2;
        String vStation = "";
        LatLng p1, p2, p3;
        p1 = ConvertStrToLatLng(vSt1.vLatLng);
        p2 = ConvertStrToLatLng(vSt2.vLatLng);
        p3 = ConvertStrToLatLng(vLatLng);
        dif1 = CheckPointDiff(p1, p3);
        dif2 = CheckPointDiff(p2, p3);
        if (dif1 > dif2) {
            vStation = "ปลายทาง: " + vSt2.vStationName;
        } else {
            vStation = "ต้นทาง: " + vSt1.vStationName;
        }
        //äÁèà¢éÒ Case
        if ((dif1 > 3000) && (dif2 > 3000)) {
            vStation = "No";
        }
        return vStation;
    }


    private LatLng ConvertStrToLatLng(String vLatLng) {
        LatLng vL = new LatLng();
        vL.d_lat = Double.parseDouble(vLatLng.split(",")[0]);
        vL.d_lng = Double.parseDouble(vLatLng.split(",")[1]);
        return vL;
    }


    /****************************< Function comment >*************************/
    /** NAME		 : µÃÇ¨ÊÍºÃÐÂÐ ËèÒ§¢Í§¨Ø´			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */

    public double CheckPointDiff(LatLng Ori, LatLng Des) {
        double lat2, lon2;
        double lat1, lon1;

        lat1 = Ori.d_lat;
        lon2 = Ori.d_lng;
        lat2 = Des.d_lat;
        lon1 = Des.d_lng;
        double theta = lon1 - lon2;
        Double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        dist = dist * 1000;

        return dist;

    }


    public static class StationCheck {
        public String vLatLng = "13,100";
        public String vkeyStation = "";
        public String vStationName = "";
        public double vRad = 800;

        StationCheck() {
        }
    }


    public static class LatLng {
        //Active, Cancel
        public double d_lat;
        public double d_lng;

        LatLng() {
        }
    }


    double deg2rad(Double deg) {

        return (deg * Math.PI / 180.0);

    }

    double rad2deg(Double rad) {

        return (rad / Math.PI * 180.0);

    }


}
