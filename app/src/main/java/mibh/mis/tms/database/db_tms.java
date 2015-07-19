package mibh.mis.tms.database;

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
    static final int versionnew = 2;
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
            onUpgrade(db, versionnew, db.getVersion());
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
                this.Im_Type_img + " ='" + vImg_Type + "' ", null);
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

}
