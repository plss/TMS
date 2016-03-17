package mibh.mis.tms.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class img_tms {

    /**
     * Database *
     */
    private db_tms DB_TMS;

    /* Image GroupType 1 สำหรับ ปฎิบัติงาน GTYPE_WORK*/
    public static final String IMG_WORK_STATION = "10";
    public static final String IMG_WORK_PRODUCT = "11";
    public static final String IMG_WORK_DOCUMENT = "12";
    public static final String IMG_WORK_TRUCK = "13";
    public static final String IMG_WORK_WEIGHT = "14";
    public static final String IMG_WORK_WASH = "15";
    public static final String IMG_WORK_PROBLEM = "16";
    public static final String IMG_WORK_ACCIDENT = "17";
    public static final String IMG_WORK_OTHER = "91";

    /* Image GroupType เติมเชื้่อเพลิง GTYPE_FUEL*/
    public static final String IMG_FUELLOCATION = "30";
    public static final String IMG_FUELDOC = "31";
    public static final String IMG_FUELCAR = "32";
    public static final String IMG_FUELOTHER = "93";

    public static final String IMG_SIGN_RECEIVE = "50";
    public static final String IMG_SIGN_SUBMIT = "51";

    /* Image Maintenance */
    public static final String IMG_MTNCAR = "100";
    public static final String IMG_MTNASSET = "101";
    public static final String IMG_MTNDRIVERREJECT = "107";
    public static final String IMG_MTNTRUCKREJECT = "108";
    public static final String IMG_MTNOTHER = "109";

    /* Image MTNDriver */
    public static final String IMG_MTNHUMAN = "102";
    public static final String IMG_MTNDRIOTHER = "119";

    /* Image ReqWork */
    public static final String IMG_REQWORKDRIVER = "120";
    public static final String IMG_REQWORKCHOOSE = "128";
    public static final String IMG_REQWORKORTHER = "129";

    /* Image Other */
    public static final String IMG_OTHER = "999";

    /* Image GroupType  */
    public static final String GTYPE_WORK = "WORK";
    public static final String GTYPE_FUEL = "FUEL";
    public static final String GTYPE_STGNATURE = "SIGNATURE";
    public static final String GTYPE_MAINTENANCE = "MAINTENANCE";
    public static final String GTYPE_MTNDRIVER = "MTNDRIVER";
    public static final String GTYPE_REQWORK = "REQWORK";
    public static final String GTYPE_OTHER = "OTHER";

    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";


    public String Doc_name(String img_type) {
        String Dname = "";


        if (img_type.equalsIgnoreCase(IMG_WORK_STATION)) {
            Dname = "สถาที่รับ/ส่ง";
        }
        if (img_type.equalsIgnoreCase(IMG_WORK_PRODUCT)) {
            Dname = "สินค้า";
        }
        if (img_type.equalsIgnoreCase(IMG_WORK_DOCUMENT)) {
            Dname = "เอกสาร/DO";
        }
        if (img_type.equalsIgnoreCase(IMG_WORK_TRUCK)) {
            Dname = "ระหว่างเดินทาง";
        }
        if (img_type.equalsIgnoreCase(IMG_WORK_WEIGHT)) {
            Dname = "ห้องชั่ง";
        }
        if (img_type.equalsIgnoreCase(IMG_WORK_WASH)) {
            Dname = "ล้างรถ";
        }
        if (img_type.equalsIgnoreCase(IMG_WORK_PROBLEM)) {
            Dname = "แจ้งปัญหา/อุบัติเหตุ";
        }
        if (img_type.equalsIgnoreCase(IMG_WORK_ACCIDENT)) {
            Dname = "อุบัติเหตุ";
        }
        if (img_type.equalsIgnoreCase(IMG_WORK_OTHER)) {
            Dname = "อื่นๆ";
        }
        if (img_type.equalsIgnoreCase(IMG_FUELLOCATION)) {
            Dname = "สถานีเชื้อเพลิง";
        }
        if (img_type.equalsIgnoreCase(IMG_FUELDOC)) {
            Dname = "เอกสาร/ใบเสร็จ";
        }
        if (img_type.equalsIgnoreCase(IMG_FUELCAR)) {
            Dname = "รถ";
        }
        if (img_type.equalsIgnoreCase(IMG_FUELOTHER)) {
            Dname = "อื่นๆ";
        }
        if (img_type.equalsIgnoreCase(IMG_SIGN_RECEIVE)) {
            Dname = "เซ็นรับสินค้า";
        }
        if (img_type.equalsIgnoreCase(IMG_SIGN_SUBMIT)) {
            Dname = "เซ็นส่งสินค้า";
        }
        if (img_type.equalsIgnoreCase(IMG_MTNCAR)) {
            Dname = "รถ";
        }
        if (img_type.equalsIgnoreCase(IMG_MTNASSET)) {
            Dname = "อุปกรณ์ประจำรถ";
        }
        if (img_type.equalsIgnoreCase(IMG_MTNHUMAN)) {
            Dname = "พนักงาน";
        }
        if (img_type.equalsIgnoreCase(IMG_MTNOTHER)) {
            Dname = "อื่นๆ";
        }
        if (img_type.equalsIgnoreCase(IMG_MTNDRIOTHER)) {
            Dname = "อื่นๆ";
        }

        return Dname;
    }

    public String Group_name(String Group_type) {
        String Gname = "";

        if (Group_type.equalsIgnoreCase(this.GTYPE_WORK)) {
            Gname = "จัดส่งสินค้า";
        }

        if (Group_type.equalsIgnoreCase(this.GTYPE_FUEL)) {
            Gname = "การเติมเชื้อเพลิง";
        }

        return Gname;
    }

    public img_tms(Context context) {

        DB_TMS = new db_tms(context);
        DB_TMS.getWritableDatabase();

    }

    public static class Image_tms {
        public String WorkHid = "";
        public String Group_Type = "";
        public String Type_img = "";
        public String Filename = "";
        public String Doc_item = "";
        public String Lat_img = "";
        public String Lng_img = "";
        public String Date_img = "";
        public String Stat_Upd = "";
        public String Comment = "";

        Image_tms() {
        }
    }

    public static class Hashtag_TB_TMS {
        public String TB_Name = "";
        public String Server_Date = "";

        Hashtag_TB_TMS() {

        }
    }

    public static class Hashtag_TMS {
        public String list_id = "";
        public String list_name = "";
        public String group_id = "";
        public String type_id = "";
        public String server_date = "";
        public String status = "";

        Hashtag_TMS() {

        }
    }

    public static class WorkList {

        public String list_id = "";
        public String list_name = "";
        public String value_date = "";
        public String server_date = "";
        public String status = "";

        WorkList(){

        }
    }

    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    public ArrayList<Image_tms> Img_GetImageByDoc_item(String vWorkHid, String vDoc_item) {
        ArrayList<Image_tms> Images = new ArrayList<Image_tms>();

        Cursor c = DB_TMS.GetImageByDoc_item(vWorkHid, vDoc_item);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Image_tms m = new Image_tms();

                    m.WorkHid = DB_TMS.GetString(c, DB_TMS.Im_WorkHid, "");
                    m.Group_Type = DB_TMS.GetString(c, DB_TMS.Im_Group_Type, "");
                    m.Type_img = DB_TMS.GetString(c, DB_TMS.Im_Type_img, "");
                    m.Filename = DB_TMS.GetString(c, DB_TMS.Im_Filename, "");
                    m.Doc_item = DB_TMS.GetString(c, DB_TMS.Im_Doc_item, "");
                    m.Lat_img = DB_TMS.GetString(c, DB_TMS.Im_Lat_img, "");
                    m.Lng_img = DB_TMS.GetString(c, DB_TMS.Im_Lng_img, "");
                    m.Date_img = DB_TMS.GetString(c, DB_TMS.Im_Date_img, "");
                    m.Stat_Upd = DB_TMS.GetString(c, DB_TMS.Im_Status_Update, "");
                    m.Comment = DB_TMS.GetString(c, DB_TMS.Im_Comment, "");

                    Images.add(m);

                } while (c.moveToNext());
            }
        }
        c.close();
        return Images;
    }

    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    public ArrayList<Image_tms> Img_GetImageByDoc_itemAndGroupType(String vWorkHid, String vGroup_Type, String vDoc_item) {
        ArrayList<Image_tms> Images = new ArrayList<Image_tms>();

        Cursor c = DB_TMS.GetImageByGroupTypeAndItem(vWorkHid, vGroup_Type, vDoc_item);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Image_tms m = new Image_tms();

                    m.WorkHid = DB_TMS.GetString(c, DB_TMS.Im_WorkHid, "");
                    m.Group_Type = DB_TMS.GetString(c, DB_TMS.Im_Group_Type, "");
                    m.Type_img = DB_TMS.GetString(c, DB_TMS.Im_Type_img, "");
                    m.Filename = DB_TMS.GetString(c, DB_TMS.Im_Filename, "");
                    m.Doc_item = DB_TMS.GetString(c, DB_TMS.Im_Doc_item, "");
                    m.Lat_img = DB_TMS.GetString(c, DB_TMS.Im_Lat_img, "");
                    m.Lng_img = DB_TMS.GetString(c, DB_TMS.Im_Lng_img, "");
                    m.Date_img = DB_TMS.GetString(c, DB_TMS.Im_Date_img, "");
                    m.Stat_Upd = DB_TMS.GetString(c, DB_TMS.Im_Status_Update, "");
                    m.Comment = DB_TMS.GetString(c, DB_TMS.Im_Comment, "");

                    Images.add(m);

                } while (c.moveToNext());
            }
        }
        c.close();
        return Images;
    }

    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    public ArrayList<Image_tms> Img_GetImageByDoc_itemAndGroupTypeAndImg_Type(String vWorkHid, String vGroup_Type, String vDoc_item, String vImg_Type) {
        ArrayList<Image_tms> Images = new ArrayList<Image_tms>();

        Cursor c = DB_TMS.GetImageByGroupTypeAndItemAndImgType(vWorkHid, vGroup_Type, vDoc_item, vImg_Type);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Image_tms m = new Image_tms();

                    m.WorkHid = DB_TMS.GetString(c, DB_TMS.Im_WorkHid, "");
                    m.Group_Type = DB_TMS.GetString(c, DB_TMS.Im_Group_Type, "");
                    m.Type_img = DB_TMS.GetString(c, DB_TMS.Im_Type_img, "");
                    m.Filename = DB_TMS.GetString(c, DB_TMS.Im_Filename, "");
                    m.Doc_item = DB_TMS.GetString(c, DB_TMS.Im_Doc_item, "");
                    m.Lat_img = DB_TMS.GetString(c, DB_TMS.Im_Lat_img, "");
                    m.Lng_img = DB_TMS.GetString(c, DB_TMS.Im_Lng_img, "");
                    m.Date_img = DB_TMS.GetString(c, DB_TMS.Im_Date_img, "");
                    m.Stat_Upd = DB_TMS.GetString(c, DB_TMS.Im_Status_Update, "");
                    m.Comment = DB_TMS.GetString(c, DB_TMS.Im_Comment, "");

                    Images.add(m);

                } while (c.moveToNext());
            }
        }
        c.close();
        return Images;
    }

    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    public ArrayList<Image_tms> Img_GetImageByGroupType(String vWorkHid, String vGroup_Type) {
        ArrayList<Image_tms> Images = new ArrayList<Image_tms>();

        Cursor c = DB_TMS.GetImageByGroupType(vWorkHid, vGroup_Type);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Image_tms m = new Image_tms();

                    m.WorkHid = DB_TMS.GetString(c, DB_TMS.Im_WorkHid, "");
                    m.Group_Type = DB_TMS.GetString(c, DB_TMS.Im_Group_Type, "");
                    m.Type_img = DB_TMS.GetString(c, DB_TMS.Im_Type_img, "");
                    m.Filename = DB_TMS.GetString(c, DB_TMS.Im_Filename, "");
                    m.Doc_item = DB_TMS.GetString(c, DB_TMS.Im_Doc_item, "");
                    m.Lat_img = DB_TMS.GetString(c, DB_TMS.Im_Lat_img, "");
                    m.Lng_img = DB_TMS.GetString(c, DB_TMS.Im_Lng_img, "");
                    m.Date_img = DB_TMS.GetString(c, DB_TMS.Im_Date_img, "");
                    m.Stat_Upd = DB_TMS.GetString(c, DB_TMS.Im_Status_Update, "");
                    m.Comment = DB_TMS.GetString(c, DB_TMS.Im_Comment, "");

                    Images.add(m);

                } while (c.moveToNext());
            }
        }
        c.close();
        return Images;
    }

    public ArrayList<Image_tms> Img_GetImageByGroupType(String vGroup_Type) {
        ArrayList<Image_tms> Images = new ArrayList<Image_tms>();

        Cursor c = DB_TMS.GetImageByGroupType(vGroup_Type);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Image_tms m = new Image_tms();

                    m.WorkHid = DB_TMS.GetString(c, DB_TMS.Im_WorkHid, "");
                    m.Group_Type = DB_TMS.GetString(c, DB_TMS.Im_Group_Type, "");
                    m.Type_img = DB_TMS.GetString(c, DB_TMS.Im_Type_img, "");
                    m.Filename = DB_TMS.GetString(c, DB_TMS.Im_Filename, "");
                    m.Doc_item = DB_TMS.GetString(c, DB_TMS.Im_Doc_item, "");
                    m.Lat_img = DB_TMS.GetString(c, DB_TMS.Im_Lat_img, "");
                    m.Lng_img = DB_TMS.GetString(c, DB_TMS.Im_Lng_img, "");
                    m.Date_img = DB_TMS.GetString(c, DB_TMS.Im_Date_img, "");
                    m.Stat_Upd = DB_TMS.GetString(c, DB_TMS.Im_Status_Update, "");
                    m.Comment = DB_TMS.GetString(c, DB_TMS.Im_Comment, "");

                    Images.add(m);

                } while (c.moveToNext());
            }
        }
        c.close();
        return Images;
    }

    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    public ArrayList<Image_tms> Img_GetImageFromFeild(String vFeild, String vFeildFilter) {
        ArrayList<Image_tms> Images = new ArrayList<Image_tms>();

        Cursor c = DB_TMS.GetImageFromFeild(vFeild, vFeildFilter);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Image_tms m = new Image_tms();

                    m.WorkHid = DB_TMS.GetString(c, DB_TMS.Im_WorkHid, "");
                    m.Group_Type = DB_TMS.GetString(c, DB_TMS.Im_Group_Type, "");
                    m.Type_img = DB_TMS.GetString(c, DB_TMS.Im_Type_img, "");
                    m.Filename = DB_TMS.GetString(c, DB_TMS.Im_Filename, "");
                    m.Doc_item = DB_TMS.GetString(c, DB_TMS.Im_Doc_item, "");
                    m.Lat_img = DB_TMS.GetString(c, DB_TMS.Im_Lat_img, "");
                    m.Lng_img = DB_TMS.GetString(c, DB_TMS.Im_Lng_img, "");
                    m.Date_img = DB_TMS.GetString(c, DB_TMS.Im_Date_img, "");
                    m.Stat_Upd = DB_TMS.GetString(c, DB_TMS.Im_Status_Update, "");
                    m.Comment = DB_TMS.GetString(c, DB_TMS.Im_Comment, "");

                    Images.add(m);

                } while (c.moveToNext());
            }
        }
        c.close();
        return Images;
    }

    public ArrayList<Image_tms> Img_GetAllInactive() {
        ArrayList<Image_tms> Images = new ArrayList<Image_tms>();

        Cursor c = DB_TMS.GetAllInactive();
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Image_tms m = new Image_tms();

                    m.WorkHid = DB_TMS.GetString(c, DB_TMS.Im_WorkHid, "");
                    m.Group_Type = DB_TMS.GetString(c, DB_TMS.Im_Group_Type, "");
                    m.Type_img = DB_TMS.GetString(c, DB_TMS.Im_Type_img, "");
                    m.Filename = DB_TMS.GetString(c, DB_TMS.Im_Filename, "");
                    m.Doc_item = DB_TMS.GetString(c, DB_TMS.Im_Doc_item, "");
                    m.Lat_img = DB_TMS.GetString(c, DB_TMS.Im_Lat_img, "");
                    m.Lng_img = DB_TMS.GetString(c, DB_TMS.Im_Lng_img, "");
                    m.Date_img = DB_TMS.GetString(c, DB_TMS.Im_Date_img, "");
                    m.Stat_Upd = DB_TMS.GetString(c, DB_TMS.Im_Status_Update, "");
                    m.Comment = DB_TMS.GetString(c, DB_TMS.Im_Comment, "");

                    Images.add(m);

                } while (c.moveToNext());
            }
        }
        c.close();
        return Images;
    }

    public ArrayList<Image_tms> Img_GetActiveAndWoheader(String vWorkHid) {
        ArrayList<Image_tms> Images = new ArrayList<Image_tms>();

        Cursor c = DB_TMS.GetActiveAndWoheader(vWorkHid);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Image_tms m = new Image_tms();

                    m.WorkHid = DB_TMS.GetString(c, DB_TMS.Im_WorkHid, "");
                    m.Group_Type = DB_TMS.GetString(c, DB_TMS.Im_Group_Type, "");
                    m.Type_img = DB_TMS.GetString(c, DB_TMS.Im_Type_img, "");
                    m.Filename = DB_TMS.GetString(c, DB_TMS.Im_Filename, "");
                    m.Doc_item = DB_TMS.GetString(c, DB_TMS.Im_Doc_item, "");
                    m.Lat_img = DB_TMS.GetString(c, DB_TMS.Im_Lat_img, "");
                    m.Lng_img = DB_TMS.GetString(c, DB_TMS.Im_Lng_img, "");
                    m.Date_img = DB_TMS.GetString(c, DB_TMS.Im_Date_img, "");
                    m.Stat_Upd = DB_TMS.GetString(c, DB_TMS.Im_Status_Update, "");
                    m.Comment = DB_TMS.GetString(c, DB_TMS.Im_Comment, "");

                    Images.add(m);

                } while (c.moveToNext());
            }
        }
        c.close();
        return Images;
    }

    public void deleteFilename(String vFilename) {
        DB_TMS.deleteFilename(vFilename);
    }


    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    public void UpdateByFilename(String vFilename) {
         /* Create new img data */
        DB_TMS.Update_status_Ct_Upload_req(vFilename);
    }


    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    public void SaveImg(String vWorkHid, String vGroup_Type, String vType_img, String vFilename, String vDoc_item, String vLat_img, String vLng_img, String vComment) {
         /* Create new img data */
        DB_TMS.InsertImage(vWorkHid, vGroup_Type, vType_img, vFilename, vDoc_item, vLat_img, vLng_img, this.INACTIVE, vComment);

    }


    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : BT+yymm+Ck_id + runing 2 digit                       **/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    public String Gen_imgName(String vTruck, String vGroup_type) {

		/* Add image capture time */
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = date.format(c.getTime());
        return "I" + vGroup_type + "_" + formattedDate + "_" + vTruck + ".jpg";

    }

    public void close() {
        DB_TMS.close();
    }

    public ArrayList<Hashtag_TB_TMS> HT_GetDateTbHashtag(String Name) {
        ArrayList<Hashtag_TB_TMS> ArrList = new ArrayList<>();
        Cursor c = DB_TMS.GetDateHashtag(Name);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Hashtag_TB_TMS m = new Hashtag_TB_TMS();
                    m.TB_Name = DB_TMS.GetString(c, DB_TMS.ht_tb_name, "");
                    m.Server_Date = DB_TMS.GetString(c, DB_TMS.ht_last_date, "");
                    ArrList.add(m);

                } while (c.moveToNext());
            }
        }
        c.close();
        return ArrList;
    }

    public ArrayList<Hashtag_TMS> HT_GetHashtag() {
        ArrayList<Hashtag_TMS> ArrList = new ArrayList<>();
        Cursor c = DB_TMS.GetHashtag();
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Hashtag_TMS m = new Hashtag_TMS();
                    m.list_id = DB_TMS.GetString(c, DB_TMS.htv_list_id, "");
                    m.list_name = DB_TMS.GetString(c, DB_TMS.htv_list_name, "");
                    m.group_id = DB_TMS.GetString(c, DB_TMS.htv_group_id, "");
                    m.type_id = DB_TMS.GetString(c, DB_TMS.htv_type_id, "");
                    m.server_date = DB_TMS.GetString(c, DB_TMS.htv_server_date, "");
                    m.status = DB_TMS.GetString(c, DB_TMS.htv_status, "");
                    ArrList.add(m);
                } while (c.moveToNext());
            }
        }
        c.close();
        return ArrList;
    }

    public ArrayList<Hashtag_TMS> GetHashtagByGtypeAndImgType(String vGroup_Type, String vImg_Type) {
        ArrayList<Hashtag_TMS> ArrList = new ArrayList<>();
        Cursor c = DB_TMS.GetHashtagByGtypeAndImgType(vGroup_Type, vImg_Type);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Hashtag_TMS m = new Hashtag_TMS();
                    m.list_id = DB_TMS.GetString(c, DB_TMS.htv_list_id, "");
                    m.list_name = DB_TMS.GetString(c, DB_TMS.htv_list_name, "");
                    m.group_id = DB_TMS.GetString(c, DB_TMS.htv_group_id, "");
                    m.type_id = DB_TMS.GetString(c, DB_TMS.htv_type_id, "");
                    m.server_date = DB_TMS.GetString(c, DB_TMS.htv_server_date, "");
                    m.status = DB_TMS.GetString(c, DB_TMS.htv_status, "");
                    ArrList.add(m);
                } while (c.moveToNext());
            }
        }
        c.close();
        return ArrList;
    }

    public ArrayList<Hashtag_TMS> GetHashtagByGtype(String vGroup_Type) {
        ArrayList<Hashtag_TMS> ArrList = new ArrayList<>();
        Cursor c = DB_TMS.GetHashtagByGtype(vGroup_Type);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    Hashtag_TMS m = new Hashtag_TMS();
                    m.list_id = DB_TMS.GetString(c, DB_TMS.htv_list_id, "");
                    m.list_name = DB_TMS.GetString(c, DB_TMS.htv_list_name, "");
                    m.group_id = DB_TMS.GetString(c, DB_TMS.htv_group_id, "");
                    m.type_id = DB_TMS.GetString(c, DB_TMS.htv_type_id, "");
                    m.server_date = DB_TMS.GetString(c, DB_TMS.htv_server_date, "");
                    m.status = DB_TMS.GetString(c, DB_TMS.htv_status, "");
                    ArrList.add(m);
                } while (c.moveToNext());
            }
        }
        c.close();
        return ArrList;
    }

    public void UpsertTbHashtag(String SystemCode,String DateServer) {
        DB_TMS.UpsertTbHashtag(SystemCode, DateServer);
    }

    public void UpsertHashtag(String jsonResult) {
        String lastDateServer = jsonResult.substring(0, 12);
        String strHashtag = jsonResult.substring(12);
        try {
            JSONArray data = new JSONArray(strHashtag);
            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                DB_TMS.UpsertHashtag(c.getString("LIST_ID"),
                        c.getString("LIST_NAME"),
                        c.getString("GROUP_ID"),
                        c.getString("TYPE_ID"),
                        c.getString("SERVERDATE"),
                        c.getString("STATUS"));
            }
            UpsertTbHashtag("TbHashtag",lastDateServer);
        } catch (Exception e) {
            Log.d("Convert Hashtag", e.toString());
        }
    }

    public void UpsertWorkList(String jsonResult) {
        String lastDateServer = jsonResult.substring(0, 12);
        String strHashtag = jsonResult.substring(12);
        try {
            JSONArray data = new JSONArray(strHashtag);
            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                DB_TMS.UpsertWorkList(c.getString("LIST_ID"),
                        c.getString("LIST_NAME"),
                        c.getString("VALUE_DATE"),
                        c.getString("SERVERDATE"),
                        c.getString("STATUS"));
            }
            UpsertTbHashtag("TbWorkList",lastDateServer);
        } catch (Exception e) {
            Log.d("Convert WorkList", e.toString());
        }
    }

    public ArrayList<WorkList> GetAllWorkList() {
        ArrayList<WorkList> ArrList = new ArrayList<>();
        Cursor c = DB_TMS.GetAllWorkList();
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    WorkList m = new WorkList();
                    m.list_id = DB_TMS.GetString(c, DB_TMS.wo_list_id, "");
                    m.list_name = DB_TMS.GetString(c, DB_TMS.wo_list_name, "");
                    m.value_date = DB_TMS.GetString(c, DB_TMS.wo_value_date, "");
                    m.server_date = DB_TMS.GetString(c, DB_TMS.wo_serverdate, "");
                    m.status = DB_TMS.GetString(c, DB_TMS.wo_status, "");
                    ArrList.add(m);
                } while (c.moveToNext());
            }
        }
        c.close();
        return ArrList;
    }

    public ArrayList<WorkList> GetActiveWorkList() {
        ArrayList<WorkList> ArrList = new ArrayList<>();
        Cursor c = DB_TMS.GetActiveWorkList();
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    WorkList m = new WorkList();
                    m.list_id = DB_TMS.GetString(c, DB_TMS.wo_list_id, "");
                    m.list_name = DB_TMS.GetString(c, DB_TMS.wo_list_name, "");
                    m.value_date = DB_TMS.GetString(c, DB_TMS.wo_value_date, "");
                    m.server_date = DB_TMS.GetString(c, DB_TMS.wo_serverdate, "");
                    m.status = DB_TMS.GetString(c, DB_TMS.wo_status, "");
                    ArrList.add(m);
                } while (c.moveToNext());
            }
        }
        c.close();
        return ArrList;
    }

}


