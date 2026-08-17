package my.home.ir;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeManager.applySavedTheme(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        View logo = findViewById(R.id.splashLogo);
        TextView title = findViewById(R.id.splashTitle);
        TextView sub = findViewById(R.id.splashSubtitle);

        logo.setScaleX(0.60f);
        logo.setScaleY(0.60f);
        logo.setAlpha(0f);

        title.setAlpha(0f);
        sub.setAlpha(0f);

        ObjectAnimator logoAlpha =
                ObjectAnimator.ofFloat(logo, View.ALPHA, 0f, 1f);

        ObjectAnimator logoScaleX =
                ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.60f, 1f);

        ObjectAnimator logoScaleY =
                ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.60f, 1f);

        ObjectAnimator titleAlpha =
                ObjectAnimator.ofFloat(title, View.ALPHA, 0f, 1f);

        ObjectAnimator subAlpha =
                ObjectAnimator.ofFloat(sub, View.ALPHA, 0f, 1f);

        AnimatorSet animation = new AnimatorSet();

        animation.playTogether(
                logoAlpha,
                logoScaleX,
                logoScaleY,
                titleAlpha,
                subAlpha
        );

        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        handler.postDelayed(() -> {

            Intent intent =
                    new Intent(SplashActivity.this, MainActivity.class);

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );

            finish();

        }, 1550);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}