package xu.ferris.flyonekeywallpaper;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;

/**
 * Created by ferris on 2016/1/19.
 */
public class OneKeyWallpaperView extends View  implements ValueAnimator.AnimatorUpdateListener {
    Bitmap widget_wallpaper_wave_mask, widget_wallpaper_wave_bg, widget_wallpaper_wave, srcwave;
    int waveheight;
    int wavelength;
    int wavewidth;
    int speed = 3;
    Rect mWaveSrcRect;
    int waveColor;
    Paint mBgColorPaint;

    ObjectAnimator mAnimator;
     int downProgress = 0;
     int progress = 0;
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


    public void init() {
        widget_wallpaper_wave_mask = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.widget_wallpaper_wave_mask);
        widget_wallpaper_wave_bg = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.widget_wallpaper_wave_bg);
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
        wavelength = (wavewidth / 7);
        speed = (wavelength / 15);

        mWaveSrcRect = new Rect(0, 0, this.wavelength, this.waveheight / 5);

        //获取水波底部颜色
        waveColor = this.srcwave.getPixel(this.srcwave.getWidth() / 2, 3 * this.srcwave.getHeight() / 4);
        mBgColorPaint = new Paint();
        mBgColorPaint.setColor(this.waveColor);
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
        mAnimator.setDuration(1000L);
        mAnimator.setRepeatMode(Animation.REVERSE);
        mAnimator.setRepeatCount(Animation.INFINITE);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 0.0-- 1.0f
     * @param animation
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
            float offsetX=(wavelength/2)*getInterpolate();
            float offsetY=0;
            if(downProgress<progress){

            }
    }
}
