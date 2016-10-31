package mibh.mis.tmsland.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by ponlakiss on 06/08/2015.
 */
public class db_tms extends SQLiteOpenHelper {
    /* Database name */
    static final String dbName = "TMS_DB";
    static final String Tb_Img = "IMG_TMSMOBILE";
    static final String Tb_Hashtag = "HASHTAG";
    static final String Tb_HashtagValue = "HASHTAG_VALUE";
    static final String Tb_WorkList = "WORKLIST";
    static final int versionnew = 4;
    static int versionold;

    /* field name for IMAGE_TBL */
    static final String Im_WorkHid = "Work_id";
    static final String Im_Group_Type = "Group_Type"; //fuel
    static final String Im_Type_img = "Type_img";
    static final String Im_Filename = "Filename";
    static final String Im_Doc_item = "Doc_item"; //
    static final String Im_Lat_img = "Lat";
    static final String Im_Lng_img = "Lng";
    static final String Im_Date_img = "Date_img";
    static final String Im_Status_Update = "Stat_Upd";
    static final String Im_Comment = "Comment_img";

    /* field name for HASHTAG */
    static final String ht_tb_name = "TABLENAME";
    static final String ht_last_date = "LASTSERVERDATE";

    /* field name for HASHTAG_VALUE */
    static final String htv_list_id = "LIST_ID";
    static final String htv_list_name = "LIST_NAME";
    static final String htv_group_id = "GROUP_ID";
    static final String htv_type_id = "TYPE_ID";
    static final String htv_server_date = "SERVERDATE";
    static final String htv_status = "STATUS";

    /* field name for WORKLIST */
    static final String wo_list_id = "LIST_ID";
    static final String wo_list_name = "LIST_NAME";
    static final String wo_value_date = "VALUE_DATE";
    static final String wo_serverdate = "SERVERDATE";
    static final String wo_status = "STATUS";

    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    public db_tms(Context context) {

        super(context, dbName, null, versionnew);
        SQLiteDatabase db = this.getWritableDatabase();


        Log.d("march", "versionnew " + String.valueOf(versionnew) + " nowVersion " + String.valueOf(db.getVersion()));
        if (versionnew != db.getVersion()) {
            onUpgrade(db, db.getVersion(), versionnew);
        }

    }

    public void finalize() throws Throwable {
        SQLiteDatabase db = this.getReadableDatabase();

        if (null != db) {
            db.close();
            Log.d("march", "db.close");
        }

        super.finalize();
    }
    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.setLocale(Locale.getDefault());
        db.setLockingEnabled(true);

        db.execSQL("CREATE TABLE " + Tb_Img + " (" + Im_WorkHid + " TEXT ,"
                + Im_Group_Type + " TEXT ,"
                + Im_Type_img + " TEXT ,"
                + Im_Filename + " TEXT ,"
                + Im_Doc_item + " TEXT ,"
                + Im_Lat_img + " TEXT ,"
                + Im_Lng_img + " TEXT ,"
                + Im_Date_img + " TEXT ,"
                + Im_Status_Update + " TEXT ,"
                + Im_Comment + " TEXT )");

        db.execSQL("CREATE TABLE " + Tb_Hashtag + " (" + ht_tb_name + " TEXT ," + ht_last_date + " TEXT )");

        db.execSQL("CREATE TABLE " + Tb_HashtagValue + " (" + htv_list_id + " TEXT ,"
                + htv_list_name + " TEXT ,"
                + htv_group_id + " TEXT ,"
                + htv_type_id + " TEXT ,"
                + htv_server_date + " TEXT ,"
                + htv_status + " TEXT )");

        db.execSQL("CREATE TABLE " + Tb_WorkList + " (" + wo_list_id + " TEXT ,"
                + wo_list_name + " TEXT ,"
                + wo_value_date + " TEXT ,"
                + wo_serverdate + " TEXT ,"
                + wo_status + " TEXT )");

        Log.d("march", "create Db");

    }


    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("march", "onUpgrade " + String.valueOf(oldVersion) + "," + String.valueOf(newVersion));
        Log.d("march", "create Db");
        if (oldVersion < 2) {
            String ALTER_TBL = "ALTER TABLE " + Tb_Img + " ADD COLUMN " + Im_Comment + " TEXT";
            db.execSQL(ALTER_TBL);
        }
        if (oldVersion < 3) {
            db.setLocale(Locale.getDefault());
            db.setLockingEnabled(true);
            db.execSQL("CREATE TABLE " + Tb_Hashtag + " (" + ht_tb_name + " TEXT ,"
                    + ht_last_date + " TEXT )");
            Log.d("march", "create Db1");

            db.execSQL("CREATE TABLE " + Tb_HashtagValue + " (" + htv_list_id + " TEXT ,"
                    + htv_list_name + " TEXT ,"
                    + htv_group_id + " TEXT ,"
                    + htv_type_id + " TEXT ,"
                    + htv_server_date + " TEXT ,"
                    + htv_status + " TEXT )");
            Log.d("march", "create Db2");
        }
        if(oldVersion < 4) {
            db.execSQL("CREATE TABLE " + Tb_WorkList + " (" + wo_list_id + " TEXT ,"
                    + wo_list_name + " TEXT ,"
                    + wo_value_date + " TEXT ,"
                    + wo_serverdate + " TEXT ,"
                    + wo_status + " TEXT )");
        }

//		 db.execSQL("CREATE TABLE IF NOT EXISTS "+ this.Tb_Station +" ("
//					+ this.St_id			+  " TEXT PRIMARY KEY ,"
//					+ this.St_ImageMap				+  " TEXT    ,"
//					+ this.St_lat					+  " FLOAT ,"
//					+ this.St_lng					+  " FLOAT ,"
//					+ this.St_Station_name			+  " TEXT ,"
//					+ this.St_Station_address		+  " TEXT ,"
//					+ this.St_Upload_status			+  " TEXT ,"
//					+ this.St_Download_status		+  " TEXT ,"
//					+ date_server	 				+  " TEXT ,"
//					+ this.St_Type					+  " INTEGER )");
//

		/* Remove if delivery */
        /*db.execSQL("DROP TABLE IF EXISTS "+CONTRACT_TBL);*/
        /*db.execSQL("DROP TABLE IF EXISTS "+IMAGE_TBL);*/
        /*db.execSQL("DROP TABLE IF EXISTS "+MAP_TBL);*/

		/*onCreate(db);*/
    }

    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    public void EmptyAllTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + this.Tb_Img);

		/*db.close();*/
    }

    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    @SuppressLint("SimpleDateFormat")
    String GetCurrentDateTime() {
        /* Add image capture time */
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = date.format(c.getTime());
        return formattedDate;
    }


    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    @SuppressLint("SimpleDateFormat")
    String GetKey() {
        /* Add image capture time */
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = date.format(c.getTime());

        return formattedDate;
    }


    @SuppressLint("SimpleDateFormat")
    String GetKeyReqTemp(String Id_ck) {
        /* Add image capture time */
        Calendar c = Calendar.getInstance();
        //SimpleDateFormat date = new SimpleDateFormat("yyMM");
        SimpleDateFormat date = new SimpleDateFormat("yyyy");

        int year = Integer.parseInt(date.format(c.getTime()).toString());

        if (year < 2500) {
            year = year + 543;
        }
        SimpleDateFormat date1 = new SimpleDateFormat("MM");
        String Key_str = String.valueOf(year) + date1.format(c.getTime()).toString();


        String formattedDate = "BT" + Key_str.substring(2, 6) + Id_ck;

        return formattedDate;
    }


    @SuppressLint("SimpleDateFormat")
    String GetKeyContract_req() {
        /* Add image capture time */
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyMM");
        String formattedDate = date.format(c.getTime());
        return formattedDate;
    }

    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    String GetString(Cursor vCursor, String vindex, String vDefault) {
        int c = vCursor.getColumnIndex(vindex);

        if (c >= 0) {
            return vCursor.getString(c);
        } else {
            return vDefault;
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
    float GetFloat(Cursor vCursor, String vindex, float vDefault) {
        int c = vCursor.getColumnIndex(vindex);

        if (c >= 0) {
            return vCursor.getFloat(c);
        } else {
            return vDefault;
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
    int GetInteger(Cursor vCursor, String vindex, int vDefault) {
        int c = vCursor.getColumnIndex(vindex);

        if (c >= 0) {
            return vCursor.getInt(c);
        } else {
            return vDefault;
        }
    }

    /*************************************************************************************************************/
    /**                                                    IMAGE_TBL												**/
    /*************************************************************************************************************/
    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    String InsertImage(String vWorkHid,
                       String vGroup_Type,
                       String vType_img,
                       String vFilename,
                       String vDoc_item,
                       String vLat_img,
                       String vLng_img,
                       String vStat_Upd,
                       String vComment) {
        String Date = GetCurrentDateTime();
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(this.Im_WorkHid, vWorkHid);
        cv.put(this.Im_Group_Type, vGroup_Type);
        cv.put(this.Im_Type_img, vType_img);
        cv.put(this.Im_Filename, vFilename);
        cv.put(this.Im_Doc_item, vDoc_item);
        cv.put(this.Im_Lat_img, vLat_img);
        cv.put(this.Im_Lng_img, vLng_img);
        cv.put(this.Im_Date_img, Date);
        cv.put(this.Im_Status_Update, vStat_Upd);
        cv.put(this.Im_Comment, vComment);

        db.insert(this.Tb_Img, null, cv);
        /*db.close();*/

        return Date;
    }


    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    Cursor GetImageFromFeild(String vFeild, String vFeildFilter) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cur = db.rawQuery("Select * from " + this.Tb_Img + " WHERE " + vFeild + " = '" + vFeildFilter + "'", null);
        return cur;
    }


    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    Cursor GetImageByGroupType(String vWorkHid, String vGroup_Type) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + this.Tb_Img + " WHERE " + this.Im_WorkHid + " = '" + vWorkHid + "' and " +
                this.Im_Group_Type + " ='" + vGroup_Type + "' ", null);
        return cur;
    }

    Cursor GetImageByGroupType(String vGroup_Type) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + this.Tb_Img + " WHERE " + this.Im_Group_Type + " ='" + vGroup_Type + "' ORDER BY " + this.Im_Date_img + " DESC", null);
        return cur;
    }

    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    Cursor GetImageByGroupTypeAndItem(String vWorkHid, String vGroup_Type, String vDoc_item) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + this.Tb_Img + " WHERE " + this.Im_WorkHid + " = '" + vWorkHid + "' and " +
                this.Im_Group_Type + " ='" + vGroup_Type + "' and " +
                this.Im_Doc_item + " ='" + vDoc_item + "' ", null);
        return cur;
    }

    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    Cursor GetAllInactive() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + this.Tb_Img + " WHERE " + this.Im_Status_Update + " = 'INACTIVE' ", null);
        return cur;
    }


    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    Cursor GetActiveAndWoheader(String WOHEADERDOCID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + this.Tb_Img + " WHERE " + this.Im_Status_Update + " = 'ACTIVE' and " +
                this.Im_WorkHid + " <>'" + WOHEADERDOCID + "' ", null);
        return cur;
    }


    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    Cursor GetImageByDoc_item(String vWorkHid, String vDoc_item) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + this.Tb_Img + " WHERE " + this.Im_WorkHid + " = '" + vWorkHid + "' and " +
                this.Im_Doc_item + " ='" + vDoc_item + "' ", null);
        return cur;
    }

    /****************************< Function comment >*************************/
    /** NAME		 : -			                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    Cursor GetImageByGroupTypeAndItemAndImgType(String vWorkHid, String vGroup_Type, String vDoc_item, String vImg_Type) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + this.Tb_Img + " WHERE " + this.Im_WorkHid + " = '" + vWorkHid + "' and " +
                this.Im_Group_Type + " ='" + vGroup_Type + "' and " +
                this.Im_Doc_item + " ='" + vDoc_item + "' and " +
                this.Im_Type_img + " ='" + vImg_Type + "' ORDER BY " + this.Im_Date_img + " DESC", null);
        return cur;
    }

    void deleteFilename(String vfileName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + this.Tb_Img + " WHERE " + this.Im_Filename + " = '" + vfileName + "' ");
        db.close();
    }

    /****************************< Function comment >*************************/
    /** NAME   : สถานะการ Upload เอกสารคำขอNo_upload=ไม่พร้อมเอกสารไม่สมบูรณ์,Ready_upload =พร้อม Up แต่ยังไม่ได้up,Comp_upload = Upload ไปแล้ว                                          **/
    /** PARAMETERS  : none                                              **/
    /** RETURN VALUE : none                                                 **/
    /** DESCRIPTION  : -                                     **/
    /**
     * *********************************************************************
     */
    long Update_status_Ct_Upload_req(String filename) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();

        if (filename != null) {
            cv.put(this.Im_Filename, filename);
            cv.put(this.Im_Status_Update, "ACTIVE");

        }
        String where = this.Im_Filename + "=?";
        String[] whereArgs = new String[]{filename};
        long l;
        l = db.update(this.Tb_Img, cv, where, whereArgs);
        return l;
  /*db.close();*/
    }

    void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (null != db) {
            db.close();
            Log.d("march", "db.close");
        }
    }

    public long UpsertTbHashtag(String name, String lastdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (lastdate != null) {
            cv.put(ht_last_date, lastdate);
        }
        String where = ht_tb_name + "=?";
        String[] whereArgs = new String[]{name};
        long i = db.update(Tb_Hashtag, cv, where, whereArgs);
        if (i == 0) {
            ContentValues cv2 = new ContentValues();
            cv2.put(ht_tb_name, name);
            cv2.put(ht_last_date, lastdate);
            i = db.insert(Tb_Hashtag, null, cv2);
            Log.d("Insert", i + "");
        }
        db.close();
        return i;
    }

    public long UpsertHashtag(String LIST_ID, String LIST_NAME, String GROUP_ID, String TYPE_ID, String SERVERDATE, String STATUS) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(htv_list_name, LIST_NAME);
        cv.put(htv_group_id, GROUP_ID);
        cv.put(htv_type_id, TYPE_ID);
        cv.put(htv_server_date, SERVERDATE);
        cv.put(htv_status, STATUS);
        String where = htv_list_id + "=?";
        String[] whereArgs = new String[]{LIST_ID};
        long i = db.update(Tb_HashtagValue, cv, where, whereArgs);
        if (i == 0) {
            ContentValues cv2 = new ContentValues();
            cv2.put(htv_list_id, LIST_ID);
            cv2.put(htv_list_name, LIST_NAME);
            cv2.put(htv_group_id, GROUP_ID);
            cv2.put(htv_type_id, TYPE_ID);
            cv2.put(htv_server_date, SERVERDATE);
            cv2.put(htv_status, STATUS);
            i = db.insert(Tb_HashtagValue, null, cv2);
        }
        db.close();
        return i;
    }

    Cursor GetDateHashtag(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + Tb_Hashtag + " WHERE " + ht_tb_name + " = '" + name + "' ", null);
        return cur;
    }

    Cursor GetHashtag() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + Tb_HashtagValue + " ORDER BY " + htv_list_name + " ASC", null);
        return cur;
    }

    Cursor GetHashtagByGtypeAndImgType(String vGroup_Type, String vImg_Type) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + Tb_HashtagValue + " WHERE " + htv_status + " = 'Active' and "
                + htv_group_id + " ='" + vGroup_Type + "' and "
                + htv_type_id + " ='" + vImg_Type + "' ORDER BY " + htv_list_name + " ASC", null);
        return cur;
    }

    Cursor GetHashtagByGtype(String vGroup_Type) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + Tb_HashtagValue + " WHERE " + htv_status + " = 'Active' and "
                + htv_group_id + " ='" + vGroup_Type + "' ORDER BY " + htv_list_name + " ASC", null);
        return cur;
    }

    Cursor GetAllWorkList() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + Tb_WorkList + " ", null);
        return cur;
    }

    Cursor GetActiveWorkList() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("Select * from " + Tb_WorkList + " WHERE " + wo_status + " = 'Active' ", null);
        return cur;
    }

    public long UpsertWorkList(String LIST_ID, String LIST_NAME, String VALUE_DATE, String SERVER_DATE, String STATUS) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(wo_list_name, LIST_NAME);
        cv.put(wo_value_date, VALUE_DATE);
        cv.put(wo_serverdate, SERVER_DATE);
        cv.put(wo_status, STATUS);
        String where = wo_list_id + "=?";
        String[] whereArgs = new String[]{LIST_ID};
        long i = db.update(Tb_WorkList, cv, where, whereArgs);
        if (i == 0) {
            ContentValues cv2 = new ContentValues();
            cv2.put(wo_list_id, LIST_ID);
            cv2.put(wo_list_name, LIST_NAME);
            cv2.put(wo_value_date, VALUE_DATE);
            cv2.put(wo_serverdate, SERVER_DATE);
            cv2.put(wo_status, STATUS);
            i = db.insert(Tb_WorkList, null, cv2);
        }
        db.close();
        return i;
    }
}
