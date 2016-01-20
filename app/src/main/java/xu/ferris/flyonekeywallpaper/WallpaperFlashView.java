package xu.ferris.flyonekeywallpaper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by ferris on 2016/1/19.
 */
public class WallpaperFlashView extends View {

    public static final int limtTime = 15000;
    private PorterDuffXfermode xfermode;
    private Paint mPaint;
    private Drawable bg1;
    private Drawable bg2;
    private Bitmap wave;
    int waveadd;
    private Rect mMaskRect;
    int waveheight;
    int wavelength;
    int wavewidth;
    int speed = 3;
    private Bitmap srcwave;
    private Rect mWaveRect;
    private Rect mWaveSrcRect;
    private int waveColor;
    private Paint mBgColorPaint;
    private Rect mBgRect;
    private Bitmap iconMask;
    int padding_bottom;
    int padding_left;
    int padding_right;
    int padding_top;
    int width;
    int height;
    private Rect mBgColorRect;

    private boolean isFlash = false;
    private boolean isUp = true;
    private long startTime = 0L;
    private int downProgress = 0;
    private int progress = 0;
    private ObjectAnimator mAnimator;
    private boolean isStart = false;
    ObjectAnimator.AnimatorListener mListener;
    private Handler handler = new Handler();

    public WallpaperFlashView(Context context) {
        super(context);
        init();
    }

    public WallpaperFlashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WallpaperFlashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


    }

    private void init() {
        SizeUtils.reset(getContext());
        this.mPaint = new Paint();
        this.xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        this.mPaint.setXfermode(this.xfermode);
        bg1 = getResources().getDrawable(R.drawable.widget_wallpaper_wave_mask);
        bg2 = getResources().getDrawable(R.drawable.widget_wallpaper_wave_bg);
        wave = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.widget_wallpaper_wave);
        this.srcwave = Bitmap.createBitmap(2 * this.wave.getWidth(), this.wave.getHeight(), this.wave.getConfig());
        Canvas localCanvas = new Canvas(this.srcwave);
        localCanvas.drawBitmap(this.wave, 0.0F, 0.0F, null);
        localCanvas.drawBitmap(this.wave, this.wave.getWidth(), 0.0F, null);
        localCanvas.save();
        this.wavewidth = this.srcwave.getWidth();
        this.waveheight = this.srcwave.getHeight();
        this.wavelength = (this.wavewidth / 7);
        this.speed = (this.wavelength / 15);
        this.mWaveSrcRect = new Rect(0, 0, this.wavelength, this.waveheight / 5);
        this.waveColor = this.srcwave.getPixel(this.srcwave.getWidth() / 2, 3 * this.srcwave.getHeight() / 4);
        this.mBgColorPaint = new Paint();
        this.mBgColorPaint.setColor(this.waveColor);

    }


    private void drawBackGroud(Canvas paramCanvas) {
        this.mBgRect = new Rect(this.padding_left, this.padding_top, this.width - this.padding_right, this.height - this.padding_bottom);
        paramCanvas.save();
        paramCanvas.clipRect(this.mBgRect);
        this.bg1.setBounds(this.mBgRect);
        this.bg1.draw(paramCanvas);
        this.bg2.setBounds(this.mBgRect);
        this.bg2.draw(paramCanvas);
        paramCanvas.restore();
    }

    private void drawBgColor(Canvas paramCanvas) {
        paramCanvas.save();
        paramCanvas.clipRect(this.mBgColorRect);
        paramCanvas.drawRect(this.mBgColorRect, this.mBgColorPaint);
        paramCanvas.restore();
    }

    private void drawMask(Canvas paramCanvas) {
        this.mMaskRect = new Rect(this.padding_left, this.padding_top, this.width - this.padding_right, this.height - this.padding_bottom);
        this.iconMask = drawableToBitmap(bg1);
        paramCanvas.save();
        paramCanvas.clipRect(this.mMaskRect);
        paramCanvas.drawBitmap(this.iconMask, null, this.mMaskRect, this.mPaint);
        paramCanvas.restore();
    }


    private void drawWave(Canvas paramCanvas) {
        paramCanvas.save();
        paramCanvas.clipRect(this.mWaveRect);
        paramCanvas.drawBitmap(this.srcwave, this.mWaveSrcRect, this.mWaveRect, null);
        paramCanvas.restore();
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
        refresh();
        drawBackGroud(canvas);
        drawWave(canvas);
        drawBgColor(canvas);
        drawMask(canvas);
    }

    private void refresh() {
        //壁纸没有结束，界面有水瓶并且已经超过了15秒，就停止
        if ((!this.isFlash) && (this.isUp) && (System.currentTimeMillis() - this.startTime > 15000L))
            stopPlay();


        if (this.isUp)//如果有水瓶状态

            if (this.downProgress > this.progress) {
                this.progress = (1 + this.progress);
                if ((this.isFlash) && (this.progress >= 90)) {
                    stopPlay();
                }

            } else {
                if (this.mWaveSrcRect.left + this.speed < this.wavewidth / 2){
                    this.mWaveSrcRect.left += this.speed;
                    this.mWaveSrcRect.right += this.speed;
                }else{
                    this.waveadd = (this.mWaveSrcRect.left + this.speed - this.wavewidth / 2);
                    this.mWaveSrcRect.left = this.waveadd;
                    this.mWaveSrcRect.right = (this.wavelength + this.waveadd);
                }

            }
//        while (true) {
//
            int i = (this.height - 2 * this.padding_bottom) / 3;
            int j = (int) (this.progress / 100.0F * (this.height - 2 * this.padding_bottom - i));
            int k = this.height - this.padding_bottom - i;
            int m = this.height - this.padding_bottom;
            this.mWaveRect = new Rect(this.padding_left, k - j, this.width - this.padding_right, m - j);
            this.mBgColorRect = new Rect(this.padding_left, this.mWaveRect.bottom, this.width - this.padding_right, j + this.mWaveRect.bottom);

//            return;
//
//            if (this.progress < 90)
//                break;
//            this.isUp = false;
//            this.downProgress = 35;
//            break;
//            if (this.downProgress >= this.progress)
//                break;
//            this.progress = (-1 + this.progress);
//            if (this.progress > 35)
//                break;
//            stopPlay();
//
//            break;
//        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
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


    public void startAnimator() {
        if ((this.mAnimator != null) && (this.mAnimator.isRunning()))
            this.mAnimator.cancel();
        this.isUp = true;
//        setVisibility(0);
        this.mAnimator = ObjectAnimator.ofFloat(this, "interpolate", new float[]{0.0F, 1.0F});
        this.mAnimator.setDuration(1000L);
        this.mAnimator.setRepeatMode(2);
        this.mAnimator.setRepeatCount(-1);
        this.mAnimator.start();
    }

    public void stopPlay() {
        if ((this.mAnimator != null) && (this.mAnimator.isRunning()))
            this.mAnimator.cancel();
        if (this.isStart)
            this.mListener.onAnimationEnd(mAnimator);
        this.isStart = false;
        this.downProgress = 0;
        this.progress = 0;
        this.mWaveRect = new Rect(this.padding_left, this.padding_top, this.width - this.padding_right, this.height - this.padding_bottom);
        invalidate();
    }

    public void setWallpaperFlashViewListener(ObjectAnimator.AnimatorListener paramf) {
        this.mListener = paramf;
    }

    /**
     * 处理新的插值
     *
     * @param paramFloat
     */
    public void setInitInterpolate(float paramFloat) {
        this.handler.post(new d(this, paramFloat));
    }

    /**
     * 只刷新界面
     *
     * @param paramFloat
     */
    public void setInterpolate(float paramFloat) {
        this.handler.post(new e(this));
    }

    public class d implements Runnable {
        WallpaperFlashView a;
        float b;

        public d(WallpaperFlashView paramWallpaperFlashView, float paramFloat) {
            this.a = paramWallpaperFlashView;
            this.b = paramFloat;
        }

        public void run() {
//            WallpaperFlashView.access$4(this.a, (int)(100.0F * this.b));
//            if (WallpaperFlashView.access$5(this.a) > 90)
//                WallpaperFlashView.access$3(this.a, false);
            this.a.invalidate();
        }
    }

    class e implements Runnable {
        WallpaperFlashView a;

        e(WallpaperFlashView paramWallpaperFlashView) {
            this.a = paramWallpaperFlashView;
        }

        public void run() {
            this.a.invalidate();
        }
    }

    public void setProgress(int paramInt) {
        if ((this.isStart) && (this.downProgress < paramInt))
            this.downProgress = paramInt;
    }


    public void playerFlash(int paramInt) {
        this.isFlash = true;
        this.downProgress = 100;
        this.isUp = true;
//        setVisibility(0);
        this.mAnimator = ObjectAnimator.ofFloat(this, "initInterpolate", new float[]{0.0F, 1.0F, 0.0F});
        this.mAnimator.addListener(new PlayAnimatorListenerAdapter(this));
        this.mAnimator.setDuration(5000L);
        this.mAnimator.setRepeatMode(2);
        this.mAnimator.setRepeatCount(1);
        this.mAnimator.start();
    }

    class PlayAnimatorListenerAdapter extends AnimatorListenerAdapter {
        WallpaperFlashView a;

        PlayAnimatorListenerAdapter(WallpaperFlashView paramWallpaperFlashView) {
            this.a = paramWallpaperFlashView;
        }

        public void onAnimationEnd(Animator paramAnimator) {
//            if (WallpaperFlashView.access$0(this.a))
//            {
//                WallpaperFlashView.access$1(this.a, ObjectAnimator.ofFloat(this.a, "initInterpolate", new float[] { 0.0F, 0.4F }));
//                WallpaperFlashView.access$2(this.a).addListener(new c(this));
//                WallpaperFlashView.access$2(this.a).setDuration(500L);
//                WallpaperFlashView.access$2(this.a).start();
//            }
        }
    }


    public void start() {
        if (!this.isStart) {
            AccelerateDecelerateInterpolator localAccelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
            AnimatorSet localAnimatorSet = new AnimatorSet();
            ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(this, "scaleX", new float[]{0.0F, 1.0F});
            ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(this, "scaleY", new float[]{0.0F, 1.0F});
            localObjectAnimator1.setInterpolator(localAccelerateDecelerateInterpolator);
            localObjectAnimator2.setInterpolator(localAccelerateDecelerateInterpolator);
            localObjectAnimator1.setDuration(300L);
            localObjectAnimator2.setDuration(300L);
            ObjectAnimator localObjectAnimator3 = ObjectAnimator.ofFloat(this, "rotation", new float[]{15.0F, 0.0F});
            ObjectAnimator localObjectAnimator4 = ObjectAnimator.ofFloat(this, "rotation", new float[]{15.0F, 15.0F});
            localObjectAnimator3.setInterpolator(new OvershootInterpolator(5.0F));
            localObjectAnimator3.setDuration(200L).setStartDelay(200L);
            localObjectAnimator4.setDuration(0L);
            localAnimatorSet.playTogether(new Animator[]{localObjectAnimator1, localObjectAnimator2, localObjectAnimator4, localObjectAnimator3});
            localAnimatorSet.start();
            this.startTime = System.currentTimeMillis();
            this.isStart = true;
            this.isFlash = false;
            startAnimator();
        }
    }
}
