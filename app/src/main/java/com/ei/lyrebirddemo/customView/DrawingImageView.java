package com.ei.lyrebirddemo.customView;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.ei.lyrebirddemo.R;
import com.ei.lyrebirddemo.model.CropModel;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class DrawingImageView extends android.support.v7.widget.AppCompatImageView {
    Context context;

    Path clipPath;
    Bitmap bmp;
    Bitmap alteredBitmap;
    Bitmap baseBitmap;
    Canvas canvas;
    Paint paint;
    float downx = 0;
    float downy = 0;
    float tdownx = 0;
    float tdowny = 0;
    float upx = 0;
    float upy = 0;
    long lastTouchDown = 0;
    int CLICK_ACTION_THRESHHOLD = 100;
    Display display;
    Point size;
    int screen_width, screen_height;
    Button btn_ok;
    ArrayList<CropModel> cropModelArrayList;
    float smallx, smally, largex, largey;
    Paint cpaint;
    Bitmap temporary_bitmap;
    private ProgressDialog pDialog;

    //Draw Bitmap
    int startX=0;
    int startY=0;
    int endX=0;
    int endY=0;


    public DrawingImageView(Context context) {
        super(context);
        init(context);
    }

    public DrawingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.context = context;

        pDialog = new ProgressDialog(context);
        cropModelArrayList = new ArrayList<>();
        //btn_ok = (Button) findViewById(R.id.btn_ok);
        //btn_ok.setOnClickListener(this);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        //display = getDisplay();
        size = new Point();
        display.getSize(size);
        screen_width = size.x;
        screen_height = size.y;

        initCanvas();

        int cx = (screen_width - bmp.getWidth()) >> 1;
        int cy = (screen_height - bmp.getHeight()) >> 1;
        canvas.drawBitmap(bmp, cx, cy, null);
        baseBitmap = alteredBitmap.copy(alteredBitmap.getConfig(), true);
        setImageBitmap(alteredBitmap);
        setOnTouchListener(onTouchDraw());
    }

    public void initCanvas() {

        Drawable d = getResources().getDrawable(R.drawable.indir);
        bmp = ((BitmapDrawable) d).getBitmap();
        //bmp=Bitmap.createBitmap(screen_width, screen_height, bmp.getConfig());
        alteredBitmap = Bitmap.createBitmap(screen_width, screen_height, bmp.getConfig());

        canvas = new Canvas(alteredBitmap);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{15.0f, 15.0f}, 0));
    }

    public View.OnTouchListener onTouchDraw() {

        return new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        downx = event.getX();
                        downy = event.getY();
                        clipPath = new Path();
                        clipPath.moveTo(downx, downy);
                        tdownx = downx;
                        tdowny = downy;
                        smallx = downx;
                        smally = downy;
                        largex = downx;
                        largey = downy;
                        lastTouchDown = System.currentTimeMillis();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        upx = event.getX();
                        upy = event.getY();
                        cropModelArrayList.add(new CropModel(upx, upy));
                        clipPath = new Path();
                        clipPath.moveTo(tdownx, tdowny);
                        for (int i = 0; i < cropModelArrayList.size(); i++) {
                            clipPath.lineTo(cropModelArrayList.get(i).getY(), cropModelArrayList.get(i).getX());
                        }
                        canvas.drawPath(clipPath, paint);
                        invalidate();
                        downx = upx;
                        downy = upy;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHHOLD) {

                            cropModelArrayList.clear();
                            initCanvas();

                            int cx = (screen_width - bmp.getWidth()) >> 1;
                            int cy = (screen_height - bmp.getHeight()) >> 1;
                            canvas.drawBitmap(bmp, cx, cy, null);
                            setImageBitmap(alteredBitmap);

                        } else {
                            if (upx != upy) {
                                upx = event.getX();
                                upy = event.getY();


                                canvas.drawLine(downx, downy, upx, upy, paint);
                                clipPath.lineTo(upx, upy);
                                invalidate();

                                crop();
                            }

                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }
                return true;
            }
        };

    }

    public View.OnTouchListener onTouchAfterDraw() {


        return new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        Timber.d("ACTION_DOWN");
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        /*
                        clipPath = new Path();
                        clipPath.moveTo(downx, downy);
                        tdownx = downx;
                        tdowny = downy;
                        smallx = downx;
                        smally = downy;
                        largex = downx;
                        largey = downy;
                        lastTouchDown = System.currentTimeMillis();
                        */
                        break;

                    case MotionEvent.ACTION_MOVE:
                        endX = (int) event.getX();
                        endY = (int) event.getY();
                        /*
                        upx = event.getX();
                        upy = event.getY();
                        cropModelArrayList.add(new CropModel(upx, upy));
                        clipPath = new Path();
                        clipPath.moveTo(tdownx, tdowny);
                        for (int i = 0; i < cropModelArrayList.size(); i++) {
                            clipPath.lineTo(cropModelArrayList.get(i).getY(), cropModelArrayList.get(i).getX());
                        }
                        canvas.drawPath(clipPath, paint);
                        invalidate();
                        downx = upx;
                        downy = upy;
                        */
                        setImageBitmap(overlay(baseBitmap,alteredBitmap));
                        //drawCroppingBitmap(alteredBitmap);
                        break;
                    case MotionEvent.ACTION_UP:
                        Timber.d("ACTION_Up");
                        /*
                        if (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHHOLD) {

                            cropModelArrayList.clear();
                            initCanvas();

                            int cx = (screen_width - bmp.getWidth()) >> 1;
                            int cy = (screen_height - bmp.getHeight()) >> 1;
                            canvas.drawBitmap(bmp, cx, cy, null);
                            setImageBitmap(alteredBitmap);

                        } else {
                            if (upx != upy) {
                                upx = event.getX();
                                upy = event.getY();


                                canvas.drawLine(downx, downy, upx, upy, paint);
                                clipPath.lineTo(upx, upy);
                                invalidate();

                                crop();
                            }

                        }
                        */
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }
                return true;
            }
        };

    }

    public void crop() {

        clipPath.close();
        clipPath.setFillType(Path.FillType.INVERSE_WINDING);

        for (int i = 0; i < cropModelArrayList.size(); i++) {
            if (cropModelArrayList.get(i).getY() < smallx) {

                smallx = cropModelArrayList.get(i).getY();
            }
            if (cropModelArrayList.get(i).getX() < smally) {

                smally = cropModelArrayList.get(i).getX();
            }
            if (cropModelArrayList.get(i).getY() > largex) {

                largex = cropModelArrayList.get(i).getY();
            }
            if (cropModelArrayList.get(i).getX() > largey) {

                largey = cropModelArrayList.get(i).getX();
            }
        }

        temporary_bitmap = alteredBitmap;
        cpaint = new Paint();
        cpaint.setAntiAlias(true);
        cpaint.setColor(getResources().getColor(R.color.colorAccent));
        cpaint.setAlpha(100);
        canvas.drawPath(clipPath, cpaint);

        canvas.drawBitmap(temporary_bitmap, 0, 0, cpaint);

        save();

    }
/*
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                save();

            default:
                break;
        }

    }
*/

    private void save() {

        if (clipPath != null) {
            final int color = 0xff424242;
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPath(clipPath, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            canvas.drawBitmap(alteredBitmap, 0, 0, paint);

            float w = largex - smallx;
            float h = largey - smally;
            alteredBitmap = Bitmap.createBitmap(alteredBitmap, (int) smallx, (int) smally, (int) w, (int) h);

        } else {
            alteredBitmap = bmp;
        }
        /*
        pDialog.show();

        Thread mThread = new Thread() {
            @Override
            public void run() {


                Bitmap bitmap = alteredBitmap;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
                byte[] byteArray = stream.toByteArray();
                pDialog.dismiss();

                Intent intent = new Intent(CropActivity.this, DisplayCropActivity.class);
                intent.putExtra("image", byteArray);
                startActivity(intent);

            }
        };
        mThread.start();
*/

        setImageBitmap(bmp);
        setOnTouchListener(onTouchAfterDraw());
    }

    private void drawCroppingBitmap(Bitmap bitmap){
        int positionLeft=0;
        int positionTop=0;
        Bitmap newBitmap =Bitmap.createBitmap(bmp.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bmp, positionLeft, positionTop,null);
        positionLeft=100;
        positionTop=100;
        canvas.drawBitmap(bitmap,positionLeft,positionTop,null);
        setImageBitmap(newBitmap);
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0,0, null);
        for (int [] tmp : calculateDrawMatrix(startX,startY,endX,endY,bmp2)){
            canvas.drawBitmap(bmp2,tmp[0] ,tmp[1], null);
        }
        //canvas.drawBitmap(bmp2, new Matrix(), null);
        return bmOverlay;
    }

    public List<int[]> calculateDrawMatrix(int startX, int startY, int endX, int endY,Bitmap bitmap){
        List<int[]> matrixList = new ArrayList<>();

        /*
        int bitmapX= bitmap.getWidth();
        int bitmapY= bitmap.getHeight();
        startX=startX+bitmapX;
        startY=startY-bitmapY;
*/

        matrixList.add(new int[]{startX,startY});

        int difX = -(startX-endX)/10;
        int difY = -(startY-endY)/10;
        for(int i = 1 ; i<11-1;i++){
            matrixList.add(new int[]{startX+difX*i,startY+difY*i});
        }


        return matrixList;
    }


}
