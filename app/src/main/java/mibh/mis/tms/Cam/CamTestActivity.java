package mibh.mis.tms.Cam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mibh.mis.tms.R;

public class CamTestActivity extends Activity implements SurfaceHolder.Callback {
    private Camera mCamera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageButton capture_image;
    private ImageButton capture_flash;
    PictureCallback rawCallback;
    ShutterCallback shutterCallback;
    PictureCallback jpegCallback;
    private String OutputDirectory;
    private FocusView FocusDraw;
    private boolean SetSurface = false;
    private int FocusWinSize = 50;
    private float Round_focus = (float) 1;
    int WcCam;
    int HcCam;

    private Rect focusArea = new Rect();
    private Rect TouchArea = new Rect();
    private Rect DocArea = new Rect();
    FrameLayout CameraFrame;

    private FaceDetector.Face[] faces;

    private int FocusOfsX = 0;
    private int FocusOfsY = 0;
    private int CameraUsage = Camera.CameraInfo.CAMERA_FACING_BACK;
    int Wc = 640;
    int Hc = 480;
    int wcc = 0;
    int Hcc = 0;
    boolean flashState = false;
    boolean isConfirm = false;
    boolean wirteImg = false;

    String Req_id = "";
    String Img_type = "";
    String Detail_Type = "";
    String Detail_Type2 = "";
    int img_int;

    String TextHeader = "¶èÒÂÀÒ¾";

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    @SuppressLint("InlinedApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_test);
        //setContentView(R.layout.carmera );

        InitCapture();

        capture_image = (ImageButton) findViewById(R.id.capture_image);
        capture_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture_image.setEnabled(false);
                capture();
            }
        });

        capture_flash = (ImageButton) findViewById(R.id.Flash);
        capture_flash.setBackground(null);

        capture_flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mCamera != null)
                        && (getPackageManager()
                        .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))) {
                    Parameters parameters = mCamera.getParameters();

                    if (flashState == false) {
                        capture_flash.setImageResource(R.drawable.pnflash);
                        flashState = true;
                        parameters.setFlashMode(Parameters.FLASH_MODE_ON);
                        SaveFocusMode(Parameters.FLASH_MODE_ON);
                    } else {
                        capture_flash.setImageResource(R.drawable.csflash);
                        flashState = false;
                        parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                        SaveFocusMode(Parameters.FLASH_MODE_OFF);
                    }

                    mCamera.setParameters(parameters);
                }
            }
        });



		/* Camera switch button */
        ImageButton switchcamera = (ImageButton) findViewById(R.id.switchcamera);
        switchcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Current is back camera */
                if (CameraUsage == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    System.gc();

                    CameraUsage = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    closeCamera();
                    OpenCamera();
                }
				/* Current is front camera */
                else {
                    System.gc();

                    CameraUsage = Camera.CameraInfo.CAMERA_FACING_BACK;
                    closeCamera();
                    OpenCamera();
                }
            }
        });


        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(CamTestActivity.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        FocusDraw = new FocusView(this, true);

//		FocusDraw.setLayoutParams(new TableLayout.LayoutParams(
//				TableLayout.LayoutParams.WRAP_CONTENT,
//				TableLayout.LayoutParams.WRAP_CONTENT));


        // FocusDraw.setLayoutParams(surfaceView.getLayoutParams());

        CameraFrame = (FrameLayout) findViewById(R.id.CameraFrame);
        CameraFrame.addView(FocusDraw);

        OpenCamera();
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    void InitCapture() {
        shutterCallback = new ShutterCallback() {
            public void onShutter() { /* No operation */
            }
        };

        rawCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) { /*
																	 * No
																	 * operation
																	 */
            }
        };

        jpegCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {

            }
        };
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle, float rescale) {


        int width = source.getWidth();
        int height = source.getHeight();
        float scaleWidth = (float) .4;
        float scaleHeight = (float) .4;
        scaleWidth = rescale;
        scaleHeight = rescale;
        System.gc();

		    /* CREATE A MATRIX FOR THE MANIPULATION */
        Matrix matrix = new Matrix();

		    /* RESIZE THE BIT MAP */
        matrix.postScale(scaleWidth, scaleHeight);
        if (angle != 0) {
            matrix.postRotate(angle);
        }


		    /* "RECREATE" THE NEW BITMAP */
        Bitmap resizedBitmap = Bitmap.createBitmap(source, 0, 0, width, height, matrix, false);
        return resizedBitmap;

    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    private void capture() {
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }


    /****************************< Function comment >*************************/
    /** NAME		 : surfaceChanged                                      	**/
    /** PARAMETERS	 : none		                                           	**/
    /** RETURN VALUE : none                                                	**/
    /** DESCRIPTION  : -					                               	**/
    /**
     * *********************************************************************
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //mCamera.setDisplayOrientation(90);
        Parameters pm = mCamera.getParameters();
        int pheight = pm.getPictureSize().height;
        int pwidth = pm.getPictureSize().width;
        int CamViewW = 0;
        int CamViewH = 0;
        try {
            if (SetSurface == false) {
                SetSurface = true;

				/* Get current screen size */
                int disp_width = 0;
                int disp_height = 0;
                FrameLayout CameraLayout = (FrameLayout) findViewById(R.id.CameraLayout);

                if (CameraLayout != null) {
                    disp_width = CameraLayout.getWidth();
                    disp_height = CameraLayout.getHeight();
                } else {
                    Display disp = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    disp_width = disp.getWidth();
                    disp_height = disp.getHeight();
                }

				/* Screen rotation flip */
                if (disp_width < disp_height) {
                    int temp = disp_height;
                    disp_height = disp_width;
                    disp_width = temp;
                }

				/*CamViewW  = (pwidth*height)/pheight;*/
				/*CamViewH  = height;*/
                CamViewW = (int) ((float) disp_width * 0.8);
                CamViewH = ((CamViewW * pheight) / pwidth);

                if (CamViewH > disp_height) {
                    CamViewH = disp_height;
                    CamViewW = ((CamViewH * pwidth) / pheight);
                }

                LayoutParams mp = (LayoutParams) surfaceView.getLayoutParams();

                mp.width = CamViewW;
                mp.height = CamViewH;
                //pm.setJpegQuality(100);

                surfaceView.setLayoutParams(mp);

                LayoutParams fw = (LayoutParams) FocusDraw.getLayoutParams();

                fw.width = CamViewW;
                fw.height = CamViewH;
                FocusDraw.setLayoutParams(fw);

                FocusOfsX = (disp_width - CamViewW) / 2;
                FocusOfsY = (disp_height - CamViewH) / 2;

                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException e) { /*e.printStackTrace();*/ }


        int FocusCenterX = (width / 2);
        int FocusCenterY = (height / 2);
        TouchArea.set(FocusCenterX - FocusWinSize, FocusCenterY - FocusWinSize, FocusCenterX + FocusWinSize, FocusCenterY + FocusWinSize);
        FocusDraw.SetFocusArea(TouchArea);
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
		/* Log.e("Surface Created", ""); */
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { /*
														 * Log.e("Surface Destroyed"
														 * , "");
														 */
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        System.gc();
        OpenCamera();
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    @SuppressWarnings("deprecation")
    void OpenCamera() {
        try {

            System.gc();
            mCamera = Camera.open(CameraUsage);

            mCamera.setPreviewDisplay(surfaceHolder);

            Parameters parameters = mCamera.getParameters();

			/* Configure image format. RGB_565 is the most common format. */
            List<Integer> formats = parameters.getSupportedPictureFormats();
            if (formats.contains(PixelFormat.RGB_565)) {
                parameters.setPictureFormat(PixelFormat.RGB_565);
            } else {
                parameters.setPictureFormat(PixelFormat.JPEG);
            }

			/* parameters.setPictureSize(Wc, Hc); */
            if (CameraUsage == Camera.CameraInfo.CAMERA_FACING_BACK) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
			/*parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);*/

            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }

            if ((getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
                    && (capture_flash != null)) {
                capture_flash.setVisibility(View.VISIBLE);
                String fMode = GetFocusMode();

                if (fMode.compareTo(Parameters.FLASH_MODE_ON) == 0) {
                    capture_flash.setImageResource(R.drawable.pnflash);
                    parameters.setFlashMode(Parameters.FLASH_MODE_ON);
                    flashState = true;
                } else {
                    capture_flash.setImageResource(R.drawable.csflash);
                    parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                    flashState = false;
                }
            } else {
                capture_flash.setVisibility(View.INVISIBLE);
            }

            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    void RefreshOpenCamera() {
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
                capture_image.setEnabled(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    public void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        try {
            System.gc();

            if (event.getAction() == MotionEvent.ACTION_UP) {
                float x = (float) (event.getX() - FocusOfsX);
                float y = (float) (event.getY() - FocusOfsY);

                Log.d("march", "x =" + String.valueOf(x));

//               WcCam
//               WcCam


                TouchArea.set((int) (x - FocusWinSize),
                        (int) (y - FocusWinSize), (int) (x + FocusWinSize),
                        (int) (y + FocusWinSize));
                FocusDraw.SetFocusArea(TouchArea);

                this.submitFocusAreaRect(TouchArea);
            }
        } catch (Exception e) {
            Log.d("Oceanus", e.getMessage());
        }
        return false;
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    private void submitFocusAreaRect(final Rect touchRect) {
        try {
            capture_image.setEnabled(false);
            Camera.Parameters cameraParameters = mCamera.getParameters();

            if (cameraParameters.getMaxNumFocusAreas() == 0) {
                capture_image.setEnabled(true);
                return;
            }

			/* Convert from View's width and height to +/- 1000 */

            focusArea.set(
                    touchRect.left * 2000 / surfaceView.getWidth() - 1000,
                    touchRect.top * 2000 / surfaceView.getHeight() - 1000,
                    touchRect.right * 2000 / surfaceView.getWidth() - 1000,
                    touchRect.bottom * 2000 / surfaceView.getHeight() - 1000);

			/* Submit focus area to camera */

            ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
            focusAreas.add(new Camera.Area(focusArea, 1000));

            cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            cameraParameters.setFocusAreas(focusAreas);
            mCamera.setParameters(cameraParameters);

			/* Start the autofocus operation */

            mCamera.autoFocus(new AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean arg0, Camera arg1) {
                    capture_image.setEnabled(true);
                    Log.d("Oceanus", "Camera focused");
                }

            });
        } catch (Exception e) {
            capture_image.setEnabled(true);
            Log.d("Oceanus", e.getMessage());
        }
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    public class FocusView extends View {
        Rect focusArea;
        Rect DocArea;
        Boolean DocM = false;

        public FocusView(Context context, boolean doc) {
            super(context);
            focusArea = new Rect();
            DocM = doc;

            DocArea = new Rect();
            focusArea.set(0, 0, 100, 100);
        }

        @SuppressLint("DrawAllocation")
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

			/* custom drawing code here */
			/* remember: y increases from top to bottom */
			/* x increases from left to right */
			/* int w = canvas.getWidth(); */
			/* int h = canvas.getHeight(); */
			/* int cx = w/2; */
			/* int cy = h/2; */

            Paint paint = new Paint();

			/* Original point */
            paint.setColor(0xbb888888);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            canvas.drawRect(focusArea, paint);

            if (DocM == true) {
                Paint P2 = new Paint();

			/* Original point */
                paint.setColor(Color.rgb(64, 64, 183));
                paint.setStyle(Paint.Style.STROKE);

                paint.setStrokeWidth(5);
                canvas.drawRect(DocArea, paint);
            }


        }

        void SetFocusArea(Rect rect) {
            focusArea.set(rect);
            invalidate();
        }

        void SetDocuArea(Rect rect) {
            DocArea.set(rect);
            invalidate();
        }

    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        System.gc();

//		 /* CREATE A MATRIX FOR THE MANIPULATION */
//		 Matrix matrix = new Matrix();
//
//		 /* RESIZE THE BIT MAP */
//		 matrix.postScale(scaleWidth, scaleHeight);
//
//		 /* "RECREATE" THE NEW BITMAP */
//		 Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
//		 matrix, false);

        //Log.d("march",String.format("resized % , %", width ,height));
        Bitmap resizedBitmap;

        resizedBitmap = Bitmap
                .createScaledBitmap(bm, (int) (newWidth),
                        (int) (newHeight), false);


        return resizedBitmap;
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    void SaveFocusMode(String Mode) {
		/* Save password */
        SharedPreferences AppData = getSharedPreferences("CDS_CAMERA",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = AppData.edit();
        editor.putString("Focus", Mode);
        editor.commit();
    }

    /**************************** < Function comment > *************************/
    /** NAME : - **/
    /** PARAMETERS : none **/
    /** RETURN VALUE : none **/
    /** DESCRIPTION : - **/
    /**
     * *********************************************************************
     */
    String GetFocusMode() {
        String Res = Parameters.FLASH_MODE_OFF;

		/* Check password is save or not from application data */
        SharedPreferences AppData = getSharedPreferences("CDS_CAMERA",
                MODE_PRIVATE);

		/* Password is save */
        if (AppData.contains("Focus") == true) {
            Res = AppData.getString("Focus", Parameters.FLASH_MODE_OFF);
        }

        return Res;
    }


    public void updateImage(String image_fn) {
        // Set internal configuration to RGB_565

        int MAX_FACES = 10;
        String IMAGE_FN = "face.jpg";
        Bitmap background_image;
        FaceDetector.Face[] faces;
        int face_count;


        BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
        bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565;

        background_image = BitmapFactory.decodeFile(image_fn, bitmap_options);
        FaceDetector face_detector = new FaceDetector(
                background_image.getWidth(), background_image.getHeight(),
                MAX_FACES);

        faces = new FaceDetector.Face[MAX_FACES];
        // The bitmap must be in 565 format (for now).
        face_count = face_detector.findFaces(background_image, faces);
        Log.d("Face_Detection", "Face Count: " + String.valueOf(face_count));
    }


}
