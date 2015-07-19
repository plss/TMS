package mibh.mis.tms.service;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import LibClass.SOAPWebserviceProperty;

/**
 * Created by ponlakiss on 06/06/2015.
 */

public class CallService {

    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String urlNick = "http://www.mibholding.com/InterfaceTmsView.svc";
    private static final String urlMarch = "http://www.mibholding.com/TMSMOBILE.asmx";
    private static String SOAP_ACTION_NICK = "http://tempuri.org/IInterfaceTmsView/";
    private static String SOAP_ACTION_MARCH = "http://tempuri.org/";
    private static String METHOD_NAME = "";
    private static SOAPWebserviceProperty soap_property = null;

    public String callWork(String TruckId, String DriverEmpID) {
        try {
            METHOD_NAME = "Get_DataWorkOpen";
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = urlMarch;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("Truck_id", TruckId);
            request.addProperty("Emp_id", DriverEmpID);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(urlMarch);
            androidHttpTransport.call(SOAP_ACTION_MARCH + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            return result.toString();
        } catch (Exception e) {
            Log.d("Error CallWork", e.toString());
            return "error";
        }
    }

    public String callPlan(String TruckId, String DriverEmpID) {
        try {
            METHOD_NAME = "GetPlanOpenWorkAuth";
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = urlNick;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("DriverEmpId", DriverEmpID);
            request.addProperty("TruckId", TruckId);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(urlNick);
            androidHttpTransport.call(SOAP_ACTION_NICK + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            return result.toString();
        } catch (Exception e) {
            Log.d("Error", e.toString());
            return "error";
        }
    }

    public String checkLogin(String TruckId, String DriverEmpID) {
        try {
            METHOD_NAME = "getAuthLoginArrange";
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = urlNick;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("DriverEmpId", DriverEmpID);
            request.addProperty("TruckId", TruckId);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(urlNick);
            androidHttpTransport.call(SOAP_ACTION_NICK + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            return result.toString();
        } catch (Exception e) {
            Log.d("Error CallLogin", e.toString());
            return "error";
        }
    }

    public String callFuel(String WOHEADER_DOCID) {
        try {
            METHOD_NAME = "Get_FuelData";
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = urlMarch;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("WOHEADER_DOCID", WOHEADER_DOCID);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(urlMarch);
            androidHttpTransport.call(SOAP_ACTION_MARCH + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            String resultData = result.toString();
            return resultData;
        } catch (Exception e) {
            Log.d("Error CallFuel", e.toString());
            return "error";
        }
    }

    public String getActiveVersion() {
        try {
            METHOD_NAME = "GetActiveVersion";
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = urlNick;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("AppId", "M004");
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(urlNick);
            androidHttpTransport.call(SOAP_ACTION_NICK + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            String resultData = result.toString();
            return resultData;
        } catch (Exception e) {
            Log.d("Error version", e.toString());
        }
        return "";
    }

    public String genNewFuel(String WOHEADER_DOCID, String EMP_ID) {
        try {
            METHOD_NAME = "GenNewFuel";
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = urlMarch;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("WOHEADER_DOCID", WOHEADER_DOCID);
            request.addProperty("EMP_ID", EMP_ID);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(urlMarch);
            androidHttpTransport.call(SOAP_ACTION_MARCH + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            String resultData = result.toString();
            Log.d("result GenNewFuel", resultData);
            return resultData;
        } catch (Exception e) {
            Log.d("Error GenNewFuel", e.toString());
            return "null";
        }
    }

    public String checkNotify(String TruckId) {
        try {
            METHOD_NAME = "Get_NotifyNewWork";
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = urlMarch;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("Truck_id", TruckId);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(urlMarch);
            androidHttpTransport.call(SOAP_ACTION_MARCH + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            String resultData = result.toString();
            return resultData;
        } catch (Exception e) {
            Log.d("Error Notify", e.toString());
            return "null";
        }
    }

    public String savePic(String json_photo, String json_Img_ct) {
        try {
            String URL_SAVEPIC = "http://www.mibholding.com/dabt.asmx";
            METHOD_NAME = "SavePhoto_json";
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = URL_SAVEPIC;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("json_photo", json_photo);
            request.addProperty("json_Img_ct", json_Img_ct);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_SAVEPIC);
            androidHttpTransport.call(SOAP_ACTION_MARCH + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            String resultData = result.toString();
            Log.d("SavePhoto", resultData);
            return resultData;
        } catch (Exception e) {
            Log.d("Error SavePhoto", e.toString());
            return "error";
        }
    }

    public String saveLog(String MOBILE_IDREF, String DRIVER_ID, String DRIVER_NAME, String TRUCK_ID, String LAST_WORK) {
        try {
            METHOD_NAME = "Save_LogMobile";
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = urlMarch;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            JSONObject polydata = new JSONObject();
            polydata.put("MOBILE_IDREF", MOBILE_IDREF);
            polydata.put("DRIVER_ID", DRIVER_ID);
            polydata.put("TRUCK_ID", TRUCK_ID);
            polydata.put("DRIVER_NAME", DRIVER_NAME);
            polydata.put("TRUCK_ID", TRUCK_ID);
            polydata.put("LAST_WORK", LAST_WORK);
            request.addProperty("Json_MobileData", polydata.toString());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(urlMarch);
            androidHttpTransport.call(SOAP_ACTION_MARCH + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            String resultData = result.toString();
            return resultData;
        } catch (Exception e) {
            Log.d("Error", e.toString());
            return "error";
        }
    }

    public String setState(String WOHEADER_DOCID, String WOITEM_DOCID, String TRUCK_ID, String LAT_LNG, String LOCATION_NAME, String WORK_TYPE, String TYPE_IMG, String DRIVER_ID, String DRIVER_NAME, String FILE_NAME, String Comment) {
        try {
            METHOD_NAME = "Save_StateWork";
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = urlMarch;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            JSONObject polydata = new JSONObject();
            polydata.put("WOHEADER_DOCID", WOHEADER_DOCID);
            polydata.put("WOITEM_DOCID", WOITEM_DOCID);
            polydata.put("TRUCK_ID", TRUCK_ID);
            polydata.put("LAT_LNG", LAT_LNG);
            polydata.put("LOCATION_NAME", LOCATION_NAME);
            polydata.put("WORK_TYPE", WORK_TYPE);
            polydata.put("TYPE_IMG", TYPE_IMG);
            polydata.put("DRIVER_ID", DRIVER_ID);
            polydata.put("DRIVER_NAME", DRIVER_NAME);
            polydata.put("FILE_NAME", FILE_NAME);
            polydata.put("COMMENT_PHOTO", Comment);

            request.addProperty("Json_StateWork", polydata.toString());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(urlMarch);
            androidHttpTransport.call(SOAP_ACTION_MARCH + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            String resultData = result.toString();
            return resultData;
        } catch (Exception e) {
            Log.d("Error", e.toString());
            return "false";
        }
    }

}
