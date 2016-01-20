package xu.ferris.flyonekeywallpaper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private WallpaperFlashView oneKeyWallpaperView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onekey_wallpaper_view);
        oneKeyWallpaperView=(WallpaperFlashView)findViewById(R.id.wallpaperFlashView);
        oneKeyWallpaperView.startAnimator();
    }
}
