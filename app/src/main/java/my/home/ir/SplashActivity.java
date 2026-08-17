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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View logo = findViewById(R.id.splashLogo);
        TextView title = findViewById(R.id.splashTitle);
        TextView sub = findViewById(R.id.splashSubtitle);

        logo.setScaleX(0.65f);
        logo.setScaleY(0.65f);
        logo.setAlpha(0f);
        title.setAlpha(0f);
        sub.setAlpha(0f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(logo, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(logo, View.SCALE_X, 0.65f, 1f),
                ObjectAnimator.ofFloat(logo, View.SCALE_Y, 0.65f, 1f),
                ObjectAnimator.ofFloat(title, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(sub, View.ALPHA, 0f, 1f)
        );
        set.setDuration(900);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();

        handler.postDelayed(() -> {
            Intent next = new Intent(this, MainActivity.class);
            startActivity(next);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1450);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
