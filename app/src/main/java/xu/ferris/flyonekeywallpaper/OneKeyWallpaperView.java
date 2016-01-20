package xu.ferris.flyonekeywallpaper;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

/**
 * Created by ferris on 2016/1/19.
 */
public class OneKeyWallpaperView extends View  implements ValueAnimator.AnimatorUpdateListener {
    Bitmap  widget_wallpaper_wave, srcwave;
    Drawable widget_wallpaper_wave_bg,widget_wallpaper_wave_mask;
    int waveheight;
    int wavelength;
    int wavewidth;
    int speed = 3;
    Rect mWaveSrcRect;
    int waveColor;
    Paint mBgColorPaint;
    Paint mPaint;
    ObjectAnimator mAnimator;
     int downProgress = 0;
     int progress = 0;

    int padding_bottom;
    int padding_left;
    int padding_right;
    int padding_top;
    int width;
    int height;

     Rect mWaveRect=new Rect();
     Rect mBgRect;
     Rect mBgColorRect;
     Rect mMaskRect;
    Bitmap iconMask;
    Rect mWavedstRect;
    public OneKeyWallpaperView(Context context) {
        super(context);
        init();
    }

    public OneKeyWallpaperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public OneKeyWallpaperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        return drawableToBitmap(drawable, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public void init() {
        SizeUtils.reset(getContext());
        mPaint = new Paint();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        widget_wallpaper_wave_mask = getResources().getDrawable(R.drawable.widget_wallpaper_wave_mask);
        iconMask = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.widget_wallpaper_wave_mask);
        widget_wallpaper_wave_bg = getResources().getDrawable(R.drawable.widget_wallpaper_wave_bg) ;
        widget_wallpaper_wave = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.widget_wallpaper_wave);

        //2倍宽度的水波
        srcwave = Bitmap.createBitmap(2 * this.widget_wallpaper_wave.getWidth(), this.widget_wallpaper_wave.getHeight(), this.widget_wallpaper_wave.getConfig());

        //将水波画到src wave里面
        Canvas mCanvas = new Canvas(srcwave);
        mCanvas.drawBitmap(widget_wallpaper_wave, 0.0F, 0.0F, null);
        mCanvas.drawBitmap(widget_wallpaper_wave, widget_wallpaper_wave.getWidth(), 0.0F, null);
        mCanvas.save();

        if (widget_wallpaper_wave != null && !widget_wallpaper_wave.isRecycled()) {
            widget_wallpaper_wave.recycle();
            widget_wallpaper_wave = null;
        }

        wavewidth = srcwave.getWidth();
        waveheight = srcwave.getHeight();

        wavelength = wavewidth / 7;

        speed = (wavelength / 15);
        mWaveSrcRect = new Rect(0, 0, this.wavelength, this.waveheight /2);

        //获取水波底部颜色
        waveColor = this.srcwave.getPixel(this.srcwave.getWidth() / 2,  3 * this.srcwave.getHeight() / 4);
        mBgColorPaint = new Paint();
        mBgColorPaint.setColor(waveColor);

    }


    private float interpolate = 0f;

    public float getInterpolate() {
        return interpolate;
    }

    public void setInterpolate(float interpolate) {
        this.interpolate = interpolate;
    }


    public void startAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }

        mAnimator = ObjectAnimator.ofFloat(this, "interpolate", new float[]{0.0F, 1.0F});
        mAnimator.setDuration(2000L);
        mAnimator.setRepeatMode(Animation.RESTART);
        mAnimator.setRepeatCount(Animation.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.width = getWidth();
        this.height = getHeight();
        this.padding_left = ((this.width - SizeUtils.dip2px(38)) / 2);
        this.padding_right = this.padding_left;
        this.padding_top = ((this.height - SizeUtils.dip2px(58)) / 2);
        this.padding_bottom = this.padding_top;
        drawBackGroud(canvas);

        drawBgColordrawMask(canvas);

    }



    /**
     * 0.0-- 1.0f
     * @param animation
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

            int offsetX= (int)( (wavelength*7)*getInterpolate());
            //画布位置
            mWavedstRect = new Rect(padding_left, padding_top, width - padding_right,  height - padding_bottom);
            //图片偏移位置
            mWaveRect.set(mWaveSrcRect.left + offsetX, mWaveSrcRect.top, mWaveSrcRect.left + offsetX + mWaveSrcRect.width(), mWaveSrcRect.bottom);
            invalidate();
    }


    private void drawBackGroud(Canvas mCanvas) {
        mBgRect= new Rect(padding_left, padding_top, width - padding_right, height - padding_bottom);
        mCanvas.save();
        mCanvas.clipRect(mBgRect);
        widget_wallpaper_wave_mask.setBounds(mBgRect);
        widget_wallpaper_wave_mask.draw(mCanvas);
        widget_wallpaper_wave_bg.setBounds(mBgRect);
        widget_wallpaper_wave_bg.draw(mCanvas);
        mCanvas.restore();
    }



    private void drawBgColordrawMask(Canvas paramCanvas) {
        int sc = paramCanvas.saveLayer(padding_left, padding_top, width - padding_right, height - padding_bottom, null,
                Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
                        | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                        | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                        | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        mWavedstRect.top=50;
        paramCanvas.drawBitmap(srcwave, mWaveRect, mWavedstRect, null);
        mBgColorRect = new Rect(this.padding_left, 100, this.width - this.padding_right, this.height - this.padding_bottom);
        paramCanvas.drawRect(this.mBgColorRect, this.mBgColorPaint);
        mMaskRect = new Rect(padding_left, padding_top, width - padding_right, height - padding_bottom);
        paramCanvas.drawBitmap(this.iconMask, null, this.mMaskRect, this.mPaint);
        paramCanvas.restoreToCount(sc);
    }

}
