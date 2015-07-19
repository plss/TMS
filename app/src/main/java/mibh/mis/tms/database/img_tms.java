package mibh.mis.tms.database;

import android.content.Context;
import android.database.Cursor;

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
    public static final String IMG_WORK_OTHER = "17";

    /* Image GroupType เติมเชื้่อเพลิง GTYPE_FUEL*/
    public static final String IMG_FUELLOCATION = "30";
    public static final String IMG_FUELDOC = "31";
    public static final String IMG_FUELOTHER = "32";

    public static final String IMG_SIGN_RECEIVE = "50";
    public static final String IMG_SIGN_SUBMIT = "51";

    /* Image GroupType  */
    public static final String GTYPE_WORK = "WORK";
    public static final String GTYPE_FUEL = "FUEL";
    public static final String GTYPE_STGNATURE = "SIGNATURE";

    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";


    public String Doc_name(String img_type) {
        String Dname = "";


        if (img_type.equalsIgnoreCase(this.IMG_WORK_STATION)) {
            Dname = "สถานี";
        }
        if (img_type.equalsIgnoreCase(this.IMG_WORK_PRODUCT)) {
            Dname = "สินค้า";
        }
        if (img_type.equalsIgnoreCase(this.IMG_WORK_DOCUMENT)) {
            Dname = "เอกสาร/DO";
        }
        if (img_type.equalsIgnoreCase(this.IMG_WORK_TRUCK)) {
            Dname = "รถ";
        }
        if (img_type.equalsIgnoreCase(this.IMG_WORK_WEIGHT)) {
            Dname = "ห้องชั่ง";
        }
        if (img_type.equalsIgnoreCase(this.IMG_WORK_WASH)) {
            Dname = "ล้างรถ";
        }
        if (img_type.equalsIgnoreCase(this.IMG_WORK_PROBLEM)) {
            Dname = "แจ้งปัญหา";
        }
        if (img_type.equalsIgnoreCase(this.IMG_WORK_OTHER)) {
            Dname = "อื่นๆ";
        }
        if (img_type.equalsIgnoreCase(this.IMG_FUELLOCATION)) {
            Dname = "สถานีเชื้อเพลิง";
        }
        if (img_type.equalsIgnoreCase(this.IMG_FUELDOC)) {
            Dname = "เอกสาร/ใบเสร็จ";
        }
        if (img_type.equalsIgnoreCase(this.IMG_FUELOTHER)) {
            Dname = "อื่นๆ";
        }
        if (img_type.equalsIgnoreCase(this.IMG_SIGN_RECEIVE)) {
            Dname = "เซ็นรับสินค้า";
        }
        if (img_type.equalsIgnoreCase(this.IMG_SIGN_SUBMIT)) {
            Dname = "เซ็นส่งสินค้า";
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


    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
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
    /** RETURN VALUE : BT+yymm+Ck_id + runing 2 digit                                               	**/
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

}


