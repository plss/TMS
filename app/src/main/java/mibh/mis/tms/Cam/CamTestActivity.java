package mibh.mis.tms.Cam;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import mibh.mis.tms.DividerItemDecoration;
import mibh.mis.tms.R;
import mibh.mis.tms.database.img_tms;
import mibh.mis.tms.service.CallService;

public class CamTestActivity extends AppCompatActivity {


    // Native camera.
    private Camera mCamera;

    // View to display the camera output.
    private CameraPreview mPreview;

    // Reference to the containing view.
    //private View mCameraView;

    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK,
            frontCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT,
            backCameraId = Camera.CameraInfo.CAMERA_FACING_BACK,
            rotateAngle,
            countCapture;

    private String flashOn = Camera.Parameters.FLASH_MODE_ON,
            flashOff = Camera.Parameters.FLASH_MODE_OFF,
            currentFlash = flashOff;

    private static final int FOCUS_AREA_SIZE = 300;

    private OrientationEventListener cOrientationEventListener;

    private Boolean cameraState = true;

    private Bitmap bitmapHolder, bitmap;

    private AlertDialog dialogList2;
    private AutoCompleteTextView input;
    private RecyclerView recyclerView;

    private SharedPreferences sp;
    private SharedPreferences.Editor edit;

    private img_tms imgTms;

    private String fileName = "", from = "", modeName = "", stat = "", comment = "", WOHEADER_DOCID2 = "", ITEM = "", DETAIL = "", TYPE_IMG = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_test);

        sp = getSharedPreferences("info", Context.MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("WOHEADER_DOCID")) {
            WOHEADER_DOCID2 = extras.getString("WOHEADER_DOCID");
        }
        if (extras.containsKey("MODE")) {
            modeName = extras.getString("MODE");
        }
        if (extras.containsKey("STATUS")) {
            stat = extras.getString("STATUS");
        }
        if (extras.containsKey("DETAIL")) {
            DETAIL = extras.getString("DETAIL");
        }
        if (extras.containsKey("From")) {
            from = extras.getString("From");
        }
        if (extras.containsKey("ITEM")) {
            ITEM = extras.getString("ITEM");
        }
        if (extras.containsKey("Type_Img")) {
            TYPE_IMG = extras.getString("Type_Img");
        }

        Log.d("TEST DATA", WOHEADER_DOCID2 + " " + modeName + " " + stat + " " + DETAIL + " " + from + " " + ITEM + " " + TYPE_IMG);

        imgTms = new img_tms(this);

        fileName = imgTms.Gen_imgName(sp.getString("truckid", ""), from);

        boolean opened = safeCameraOpenInView();

        if (!opened) {
            Log.d("CameraGuide", "Error, Camera failed to open");
            //return view;
        }

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.camera_previewCT);
        ImageView btnTakePicture = (ImageView) findViewById(R.id.btnTakePictureCT);
        ImageView btnComment = (ImageView) findViewById(R.id.btnCommentCT);
        final ImageView btnFlash = (ImageView) findViewById(R.id.btnFlashCT);
        ImageView btnSwitchCamera = (ImageView) findViewById(R.id.btnSwitchCameraCT);
        btnTakePicture.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cameraState) {
                            mCamera.takePicture(null, null, mPicture);
                            cameraState = false;
                            countCapture = 0;
                        }
                    }
                }
        );
        btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCameraId = (currentCameraId == backCameraId ? frontCameraId : backCameraId);
                safeCameraOpenInView();
            }
        });
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFlash.equalsIgnoreCase(flashOff) && hasFlash()) {
                    currentFlash = flashOn;
                    btnFlash.setBackground(getResources().getDrawable(R.drawable.pnflash));
                } else {
                    currentFlash = flashOff;
                    btnFlash.setBackground(getResources().getDrawable(R.drawable.csflash));
                }

                Camera.Parameters pm = mPreview.mCamera.getParameters();
                pm.setFlashMode(currentFlash);
                mPreview.mCamera.setParameters(pm);
            }
        });
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit = sp.edit();
                AlertDialog.Builder alert = new AlertDialog.Builder(CamTestActivity.this);
                alert.setTitle("ใส่ข้อความ");
                View view = CamTestActivity.this.getLayoutInflater().inflate(R.layout.dialog_comment, null);
                input = (AutoCompleteTextView) view.findViewById(R.id.inputComment);
                input.setThreshold(1);
                input.setLines(3);
                input.setPaddingRelative(16, 0, 16, 0);
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (sp.getString("listcomment", "").equals("")) {
                            edit.putString("listcomment", "ซ่อมรถ&เติมก๊าซ&ชั่งเข้า&ชั่งออก&รายงานตัวครับ&ลงสินค้า&ขึ้นสินค้า&ล้างรถ&บิลก็าซ&รอลงตู้&กำลังเติมก๊าซ&X-Ray");
                            edit.apply();
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String filter = s.toString().toLowerCase();
                        ArrayList<String> listItems = new ArrayList<String>();
                        int countItems = 0;
                        for (String listItem : sp.getString("listcomment", "").split("&")) {
                            if (listItem.toLowerCase().contains(filter)) {
                                if (countItems >= 3) {
                                    break;
                                }
                                listItems.add(listItem);
                                countItems++;
                            }

                        }
                        ArrayAdapter<String> adapt = new ArrayAdapter<String>(CamTestActivity.this, android.R.layout.simple_list_item_1, listItems);
                        input.setAdapter(adapt);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                alert.setView(view);
                alert.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        comment = input.getText().toString();
                        if (sp.getString("listcomment", "").equals("")) {
                            edit.putString("listcomment", comment);
                        } else {
                            String str = comment;
                            List<String> items = Arrays.asList(sp.getString("listcomment", "").split("&"));
                            if (!items.contains(comment) && !comment.equals("")) {
                                for (int x = 0, i = 0; x < items.size(); ++x, i++) {
                                    if (i >= 20) {
                                        break;
                                    }
                                    str += "&";
                                    str += items.get(x);
                                }
                                Log.d("test Comment", str + " " + items.size());
                                edit.putString("listcomment", str);
                            }
                        }
                        edit.apply();
                    }
                });
                alert.setNeutralButton("# รูปแบบข้อความ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                final AlertDialog alert2 = alert.create();
                alert2.show();
                Button btn = alert2.getButton(DialogInterface.BUTTON_NEUTRAL);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialogList = new AlertDialog.Builder(CamTestActivity.this);
                        dialogList.setTitle("เลือกรูปแบบข้อความ");
                        View dialoglist_view = CamTestActivity.this.getLayoutInflater().inflate(R.layout.dialoglist, null);
                        recyclerView = (RecyclerView) dialoglist_view.findViewById(R.id.dialoglist_rv);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(CamTestActivity.this));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(new DividerItemDecoration(CamTestActivity.this, DividerItemDecoration.VERTICAL_LIST));
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CamTestActivity.this, android.R.layout.select_dialog_singlechoice);

                        ArrayList<img_tms.Hashtag_TMS> Hash = imgTms.GetHashtagByGtypeAndImgType(from, TYPE_IMG);
                        arrayAdapter.clear();
                        for (int i = 0; i < Hash.size(); ++i) {
                            arrayAdapter.add(Hash.get(i).list_name);
                        }
                        /*ArrayList<img_tms.Hashtag_TMS> Hash = imgTms.HT_GetHashtag();
                        for (int i = 0; i < Hash.size(); ++i) {
                            arrayAdapter.add(Hash.get(i).list_name);
                        }*/
                        Adapter adapter = new Adapter(CamTestActivity.this, arrayAdapter);
                        recyclerView.setAdapter(adapter);
                        dialogList.setView(dialoglist_view);
                        dialogList.setNegativeButton("ยกเลิก",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        dialogList2 = dialogList.create();
                        dialogList2.show();
                    }
                });
            }
        });
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mPreview.focusOnTouch(motionEvent);
                }
                return true;
            }
        });

    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            countCapture++;
            if (countCapture == 1) {
                bitmapHolder = BitmapFactory.decodeByteArray(data, 0, data.length);
                bitmapHolder = RotateBitmap(bitmapHolder, rotateAngle, (float) 1);
                currentCameraId = frontCameraId;
                safeCameraOpenInView();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCamera.takePicture(null, null, mPicture);
                    }
                }, 2000);
            } else {
                cameraState = true;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                bitmap = RotateBitmap(bitmap, rotateAngle, (float) 1);
                bitmap = DrawBitmapborder(CamTestActivity.this, bitmap, bitmapHolder, modeName, DETAIL, stat, comment, WOHEADER_DOCID2);
                bitmapHolder.recycle();
                bitmapHolder = null;
                System.gc();
                final Dialog dialog = new Dialog(CamTestActivity.this);
                dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.camera_preview);
                dialog.setCancelable(false);
                final ImageView previewImg = (ImageView) dialog.findViewById(R.id.previewPic);
                Button yes = (Button) dialog.findViewById(R.id.yes);
                Button no = (Button) dialog.findViewById(R.id.no);
                previewImg.setImageBitmap(bitmap);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int imageNum = 0;
                        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "DCIM/TMS");
                        imagesFolder.mkdirs();
                        File output = new File(imagesFolder, fileName);
                        Log.i("PATH", output.toString());
                        while (output.exists()) {
                            imageNum++;
                            fileName += "_" + String.valueOf(imageNum);
                        }
                        output = new File(imagesFolder, fileName);
                        Uri uri = Uri.fromFile(output);
                        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        ContentValues image = new ContentValues();
                        String dateTaken = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        image.put(MediaStore.Images.Media.TITLE, output.toString());
                        image.put(MediaStore.Images.Media.DISPLAY_NAME, output.toString());
                        image.put(MediaStore.Images.Media.DATE_ADDED, dateTaken);
                        image.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
                        image.put(MediaStore.Images.Media.DATE_MODIFIED, dateTaken);
                        image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                        image.put(MediaStore.Images.Media.ORIENTATION, 90);
                        String path = output.getParentFile().toString().toLowerCase();
                        String name = output.getParentFile().getName().toLowerCase();
                        image.put(MediaStore.Images.ImageColumns.BUCKET_ID, path.hashCode());
                        image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
                        image.put(MediaStore.Images.Media.SIZE, output.length());
                        image.put(MediaStore.Images.Media.DATA, output.getAbsolutePath());
                        OutputStream os;
                        try {
                            os = getContentResolver().openOutputStream(uri);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 35, os);
                            os.flush();
                            os.close();
                        } catch (Exception e) {
                            Log.d("", e.toString());
                        }
                        imgTms.SaveImg(WOHEADER_DOCID2, from, TYPE_IMG, fileName, ITEM, sp.getString("latitude", "0"), sp.getString("longtitude", "0"), comment);
                        new Loading().execute();
                        currentCameraId = backCameraId;
                        countCapture = 0;
                        bitmap.recycle();
                        dialog.dismiss();
                        System.gc();
                        Intent intent = new Intent();
                        intent.putExtra("fileName", fileName);
                        setResult(RESULT_OK, intent);
                        CamTestActivity.this.finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentCameraId = backCameraId;
                        countCapture = 0;
                        bitmap.recycle();
                        dialog.dismiss();
                        System.gc();
                        safeCameraOpenInView();
                    }
                });
                dialog.show();
            }
        }
    };

    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

        // SurfaceHolder
        private SurfaceHolder mHolder;

        // Our Camera.
        private Camera mCamera;

        // Parent Context.
        private Context mContext;

        // Camera Sizing (For rotation, orientation changes)
        private Camera.Size mPreviewSize;

        // List of supported preview sizes
        private List<Camera.Size> mSupportedPreviewSizes;

        // Flash modes supported by this camera
        private List<String> mSupportedFlashModes;

        // View holding this camera.
        //private View mCameraView;

        public CameraPreview(Context context, Camera camera) {
            super(context);

            // Capture the context
            //mCameraView = cameraView;
            mContext = context;
            setCamera(camera);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setKeepScreenOn(true);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        /**
         * Begin the preview of the camera input.
         */
        public void startCameraPreview() {
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Extract supported preview and flash modes from the camera.
         *
         * @param camera
         */
        private void setCamera(Camera camera) {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            mCamera = camera;
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedFlashModes = mCamera.getParameters().getSupportedFlashModes();

            // Set the camera to Auto Flash mode.
            /*if (mSupportedFlashModes != null && mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                mCamera.setParameters(parameters);
            }*/

            requestLayout();
        }

        /**
         * The Surface has been created, now tell the camera where to draw the preview.
         *
         * @param holder
         */
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Dispose of the camera preview.
         *
         * @param holder
         */
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        }

        /**
         * React to surface changed events
         *
         * @param holder
         * @param format
         * @param w
         * @param h
         */
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            //Log.e("surfaceChanged", "surfaceChanged => w=" + w + ", h=" + h);
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }
            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }
            // set preview size and make any resize, rotate or reformatting changes here
            // start preview with new settings
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                Log.d("PreviewSize", mPreviewSize.width + " " + mPreviewSize.height);
                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                parameters.setFlashMode(currentCameraId == frontCameraId ? flashOff : currentFlash);
                List<Camera.Size> pictureSize = parameters.getSupportedPictureSizes();
                int maxWidth = 0, maxIndex = 0;
                for (int i = 0; i < pictureSize.size(); ++i) {
                    //Log.d("Size", pictureSize.get(i).width + " " + pictureSize.get(i).height + " " + (Math.round((pictureSize.get(i).width * 1.0 / pictureSize.get(i).height) * 10000)));
                    if ((Math.round((pictureSize.get(i).width * 1.0 / pictureSize.get(i).height) * 10000) == 13333)
                            && pictureSize.get(i).width < 2000
                            && pictureSize.get(i).width > maxWidth) {
                        maxWidth = pictureSize.get(i).width;
                        maxIndex = i;
                    }
                }
                parameters.setPictureSize(pictureSize.get(maxIndex).width, pictureSize.get(maxIndex).height);
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e) {
                Log.d("surfaceChanged", "Error starting camera preview: " + e.getMessage());
            }
        }

        /**
         * Calculate the measurements of the layout
         *
         * @param widthMeasureSpec
         * @param heightMeasureSpec
         */
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = resolveSize((int) this.getSuggestedMinimumWidth(), (int) widthMeasureSpec);
            int height = resolveSize((int) this.getSuggestedMinimumHeight(), (int) heightMeasureSpec);
            this.setMeasuredDimension(width, height);
            if (this.mSupportedPreviewSizes != null) {
                this.mPreviewSize = this.getOptimalPreviewSize(this.mSupportedPreviewSizes, width, height);
            }
            float ratio = this.mPreviewSize.height >= this.mPreviewSize.width ? (float) this.mPreviewSize.height / (float) this.mPreviewSize.width : (float) this.mPreviewSize.width / (float) this.mPreviewSize.height;
            if (currentCameraId == backCameraId) {
                this.setMeasuredDimension(width, (int) ((float) width * ratio));
            } else if ((int) ((float) height / ratio) < width) {
                int plusWidth = width - (int) ((float) height / ratio);
                this.setMeasuredDimension(width, height + (int) ((float) plusWidth * ratio));
            } else {
                this.setMeasuredDimension((int) ((float) height / ratio), height);
            }

        }

        /**
         * Update the layout based on rotation and orientation changes.
         *
         * @param changed
         * @param left
         * @param top
         * @param right
         * @param bottom
         */
        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
            if (changed) {
                final int width = right - left;
                final int height = bottom - top;

                int previewWidth = width;
                int previewHeight = height;

                if (mPreviewSize != null) {
                    Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                    switch (display.getRotation()) {
                        case Surface.ROTATION_0:
                            previewWidth = mPreviewSize.height;
                            previewHeight = mPreviewSize.width;
                            mCamera.setDisplayOrientation(90);
                            break;
                        case Surface.ROTATION_90:
                            previewWidth = mPreviewSize.width;
                            previewHeight = mPreviewSize.height;
                            break;
                        case Surface.ROTATION_180:
                            previewWidth = mPreviewSize.height;
                            previewHeight = mPreviewSize.width;
                            break;
                        case Surface.ROTATION_270:
                            previewWidth = mPreviewSize.width;
                            previewHeight = mPreviewSize.height;
                            mCamera.setDisplayOrientation(180);
                            break;
                    }
                }

                final int scaledChildHeight = previewHeight * width / previewWidth;
                //mCameraView.layout(0, height - scaledChildHeight, width, height);
            }
        }

        /**
         * @param sizes
         * @param w
         * @param h
         * @return
         */
        private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) h / w;

            if (sizes == null)
                return null;

            Camera.Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;

            for (Camera.Size size : sizes) {
                double ratio = (double) size.height / size.width;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                    continue;

                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Camera.Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }

            return optimalSize;
        }

        @Override
        public void onAutoFocus(boolean b, Camera camera) {

        }

        private void focusOnTouch(MotionEvent event) {
            try {
                if (this.mCamera != null) {
                    Camera.Parameters parameters = this.mCamera.getParameters();
                    if (parameters.getMaxNumMeteringAreas() > 0) {

                        Rect rect = calculateFocusArea(event.getX(), event.getY());

                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                        meteringAreas.add(new Camera.Area(rect, 800));
                        parameters.setFocusAreas(meteringAreas);

                        this.mCamera.setParameters(parameters);
                        this.mCamera.autoFocus(this);
                    } else {
                        this.mCamera.autoFocus(this);
                    }
                }
            } catch (Exception e) {
                Log.d("Error Touch Focus", e.toString());
            }

        }

        private Rect calculateFocusArea(float x, float y) {
            int left = clamp(Float.valueOf((x / mPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
            int top = clamp(Float.valueOf((y / mPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

            return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
        }

        private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
            int result;
            if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
                if (touchCoordinateInCameraReper > 0) {
                    result = 1000 - focusAreaSize / 2;
                } else {
                    result = -1000 + focusAreaSize / 2;
                }
            } else {
                result = touchCoordinateInCameraReper - focusAreaSize / 2;
            }
            return result;
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle, float rescale) {
        int width = source.getWidth();
        int height = source.getHeight();
        float scaleWidth = (float) .4;
        float scaleHeight = (float) .4;
        scaleWidth = rescale;
        scaleHeight = rescale;
        System.gc();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        if (angle != 0) {
            matrix.postRotate(angle);
        }
        return Bitmap.createBitmap(source, 0, 0, width, height, matrix, false);
    }

    private static Bitmap DrawBitmapborder(Context context, Bitmap fornt, Bitmap src, String modeName, String DETAIL, String stat, String comment, String WOHEADER_DOCID2) {
        //Bitmap workingBitmap = Bitmap.createBitmap(src);
        SharedPreferences sp = sp = context.getSharedPreferences("info", Context.MODE_PRIVATE);
        Bitmap mutableBitmap = src.copy(Bitmap.Config.ARGB_8888, true);  // Bitmap.createBitmap(workingBitmap);
        Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        int rw = mutableBitmap.getWidth();
        int rh = mutableBitmap.getHeight();
        if (rw > rh) {
            rw = mutableBitmap.getHeight();
            rh = mutableBitmap.getWidth();
        }
        int w = mutableBitmap.getWidth();
        int h = mutableBitmap.getHeight();

        // workingBitmap.recycle();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String formattedDate = date.format(c.getTime());
        SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.US);
        String formattedTime = time.format(c.getTime());

        int globalOffsetR = (int) (w * 0.965);
        int globalOffsetL = (int) (w * 0.02);

        Paint paintPic = new Paint(Paint.FILTER_BITMAP_FLAG);
        paintPic.setAlpha(200);
        Rect destinationRect = new Rect();
        destinationRect.set(0, 0, (int) (w * 0.2), (int) (h * 0.2));
        destinationRect.offsetTo((int) (w * 0.02), ((int) (h * 0.95) - (int) (h * 0.2)));
        canvas.drawBitmap(fornt, null, destinationRect, paintPic);
        int clr = Color.parseColor("#FFFFFF");

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setColor(clr);
        paint.setTextSize((int) (rh * 0.04));
        paint.setShadowLayer((float) 3, 2, 2, Color.BLACK);
        canvas.drawText(formattedDate, globalOffsetR, (int) (rh * 0.06), paint);

        paint.setTextSize((int) (rh * 0.04));
        canvas.drawText(formattedTime, globalOffsetR, (int) (rh * 0.10), paint);

        paint.setTextSize((int) (rh * 0.025));
        canvas.drawText(modeName, globalOffsetR, (int) (rh * 0.14), paint);

        paint.setTextSize((int) (rh * 0.025));
        String pro = DETAIL.replace("ขึ้น", "").replace("ลง", "");
        int x = globalOffsetR, y = (int) (rh * 0.18);
        String[] linePro = pro.split("\n");
        for (int i = 0; i < linePro.length; ++i) {
            canvas.drawText(linePro[i], x, y, paint);
            if (i != linePro.length - 1) {
                y += -paint.ascent() + paint.descent();
            }
        }

        int Begin = 0;
        y += (int) (rh * 0.01);
        for (int i = 0; i <= stat.length(); ++i) {
            if (i % 23 == 0 || i == stat.length()) {
                canvas.drawText(stat.substring(Begin, i), x, y, paint);
                if (i != stat.length()) {
                    y += -paint.ascent() + paint.descent();
                }
                Begin = i;
            }
        }

        y += (int) (rh * 0.01);
        if (comment != null && !comment.equalsIgnoreCase("")) {
            paint.setTextSize((int) (rh * 0.03));
            if (comment.contains("\n")) {
                y += -paint.ascent() + paint.descent();
                for (String line : comment.split("\n")) {

                    /*canvas.drawText(line, x, y, paint);
                    y += -paint.ascent() + paint.descent();*/
                    int Start = 0;
                    for (int i = 1; i <= line.length() + 1; ++i) {
                        if (i % 21 == 0 || i == line.length() + 1) {
                            //Log.d("TEST Hash", line + " " + y + " " + (i - 1));
                            canvas.drawText(line.substring(Start, i - 1), x, y, paint);
                            y += -paint.ascent() + paint.descent();
                            Start = i - 1;
                        }
                    }
                }
            } else {
                int Start = 0, End = 0;
                for (int i = 0; i <= comment.length(); ++i) {
                    if (i % 20 == 0 || i == comment.length()) {
                        canvas.drawText(comment.substring(Start, i), x, y, paint);
                        y += -paint.ascent() + paint.descent();
                        Start = i;
                    }
                }
            }
        }

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize((int) (h * 0.03));
        canvas.drawText(WOHEADER_DOCID2, globalOffsetL, (int) (h * 0.05), paint);

        paint.setTextSize((int) (h * 0.03));
        canvas.drawText(sp.getString("firstname", "") + " " + sp.getString("lastname", ""), ((int) (w * 0.02) + (int) (w * 0.22)), (int) (h * 0.775), paint);

        paint.setTextSize((int) (h * 0.03));
        canvas.drawText(sp.getString("empid", ""), ((int) (w * 0.02) + (int) (w * 0.22)), (int) (h * 0.815), paint);

        paint.setTextSize((int) (h * 0.03));
        canvas.drawText("เบอร์รถ " + sp.getString("truckid", ""), ((int) (w * 0.02) + (int) (w * 0.22)), (int) (h * 0.855), paint);

        paint.setTextSize((int) (h * 0.025));
        canvas.drawText(String.format("GPS: %.5f,%.5f", Double.parseDouble(sp.getString("latitude", "0")), Double.parseDouble(sp.getString("longtitude", "0"))), ((int) (w * 0.02) + (int) (w * 0.22)), (int) (h * 0.91), paint);

        paint.setTextSize((int) (h * 0.025));
        canvas.drawText(sp.getString("locationname", "Location Not Found"), ((int) (w * 0.02) + (int) (w * 0.22)), (int) (h * 0.945), paint);

        paint.setTextSize((int) (h * 0.025));
        canvas.drawText(sp.getString("tel", "0800000000"), ((int) (w * 0.02)), (int) (h * 0.98), paint);

        /* Paint edge */
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((int) (rh * 0.02));
        paint.setColor(Color.WHITE);
        paint.setShadowLayer((float) 5, 2, 2, 0xFF000000);
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShadowLayer((float) 5, -2, -2, 0xFF000000);
        canvas.drawRect(0, 0, w, h, paint);

        //bitmap = mutableBitmap;
        /*mutableBitmap.recycle();
        mutableBitmap = null;
        System.gc();*/
        //mutableBitmap.recycle();
        return mutableBitmap;
    }

    private class Loading extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            String loName;
            if (sp.getString("locationname", "").length() > 200) {
                loName = sp.getString("locationname", "").substring(0, 198);
            } else loName = sp.getString("locationname", "");
            if (comment.length() >= 296) {
                comment = comment.substring(0, 295) + "..";
            }
            String result = new CallService().setState(WOHEADER_DOCID2, ITEM, sp.getString("truckid", ""), String.format("%.5f,%.5f", Double.parseDouble(sp.getString("latitude", "0")), Double.parseDouble(sp.getString("longtitude", "0"))), loName, from, TYPE_IMG, sp.getString("empid", ""), sp.getString("firstname", "") + " " + sp.getString("lastname", ""), fileName, comment);
            Log.d("Result savestate", result);

            imgTms.close();
            /*bmp.recycle();
            bmpFront.recycle();
            bmpRear.recycle();*/
            System.gc();
            return null;

        }
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        Context context;
        ArrayAdapter<String> arrayAdapter;

        public Adapter(Context context, ArrayAdapter<String> arrayAdapter) {
            this.context = context;
            this.arrayAdapter = arrayAdapter;
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
            holder.dialogTxt.setText(arrayAdapter.getItem(position));
        }

        @Override
        public int getItemCount() {
            return arrayAdapter.getCount();
        }

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialoglist_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView dialogTxt;

            public ViewHolder(View itemView) {
                super(itemView);
                dialogTxt = (TextView) itemView.findViewById(R.id.dialoglist_txt);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                input.setText(arrayAdapter.getItem(getAdapterPosition()));
                dialogList2.dismiss();
            }
        }


    }

    private boolean safeCameraOpenInView() {
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance(currentCameraId);
        //mCameraView = view;
        qOpened = (mCamera != null);
        if (qOpened) {
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_previewCT);
            preview.setBackgroundColor(Color.BLACK);
            preview.addView(mPreview);
            mPreview.startCameraPreview();
        }
        return qOpened;
    }

    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mPreview != null) {
            mPreview.mCamera = null;
            mPreview.surfaceDestroyed(mPreview.getHolder());
            mPreview.getHolder().removeCallback(mPreview);
            mPreview.destroyDrawingCache();
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_previewCT);
            preview.removeView(mPreview);
            mPreview.mCamera = null;
            mPreview = null;
        }
    }

    public Camera getCameraInstance(int param) {
        Camera c = null;
        try {
            c = Camera.open(param); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    public boolean hasFlash() {
        if (mCamera == null) {
            return false;
        }

        Camera.Parameters parameters = mCamera.getParameters();

        if (parameters.getFlashMode() == null) {
            return false;
        }

        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
            return false;
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (cOrientationEventListener == null) {
            cOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
                public void onOrientationChanged(int orientation) {
                    if (orientation == ORIENTATION_UNKNOWN) return;
                    Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
                    android.hardware.Camera.getCameraInfo(currentCameraId, info);
                    orientation = (orientation + 45) / 90 * 90;
                    int rotation = 0;
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        rotation = (info.orientation - orientation + 360) % 360;
                    } else {  // back-facing camera
                        rotation = (info.orientation + orientation) % 360;
                    }
                    rotateAngle = rotation;
                }

            };
        }
        if (cOrientationEventListener.canDetectOrientation()) {
            cOrientationEventListener.enable();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCameraAndPreview();
        if (imgTms != null) {
            imgTms.close();
        }
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if (bitmapHolder != null) {
            bitmapHolder.recycle();
            bitmapHolder = null;
        }
        System.gc();
        Intent intent = new Intent();
        intent.putExtra("fileName", fileName);
        setResult(RESULT_CANCELED, intent);
        this.finish();

    }
}
