package my.home.ir;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private TextView greeting;
    private TextView streak;
    private TextView best;
    private TextView level;
    private TextView xp;
    private TextView score;
    private TextView workoutStatus;
    private TextView worshipStatus;
    private TextView cleanStatus;

    private Button reportButton;
    private ImageButton themeButton;

    private static final int NOTIFICATION_PERMISSION_REQUEST = 77;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeManager.applySavedTheme(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bindViews();

        if (!Prefs.hasName(this)) {
            showNameDialog(true);
        }

        ReminderScheduler.scheduleDaily(this);

        requestNotificationPermissionIfNeeded();

        render();
        updateThemeIcon();
    }

    private void bindViews() {

        greeting = findViewById(R.id.greeting);
        streak = findViewById(R.id.streakValue);
        best = findViewById(R.id.bestValue);
        level = findViewById(R.id.levelValue);
        xp = findViewById(R.id.xpValue);
        score = findViewById(R.id.todayScore);

        workoutStatus = findViewById(R.id.workoutStatus);
        worshipStatus = findViewById(R.id.worshipStatus);
        cleanStatus = findViewById(R.id.cleanStatus);

        reportButton = findViewById(R.id.reportButton);
        themeButton = findViewById(R.id.themeButton);

        findViewById(R.id.editNameButton)
                .setOnClickListener(v -> showNameDialog(false));

        findViewById(R.id.cleanCard)
                .setOnClickListener(v -> markClean());

        findViewById(R.id.workoutCard)
                .setOnClickListener(v -> markWorkout());

        findViewById(R.id.worshipCard)
                .setOnClickListener(v -> markWorship());

        findViewById(R.id.resetButton)
                .setOnClickListener(v -> confirmReset());

        findViewById(R.id.notificationHint)
                .setOnClickListener(v ->
                        requestNotificationPermissionIfNeeded());

        reportButton.setOnClickListener(v -> reportDay());

        themeButton.setOnClickListener(v -> {

            ThemeManager.toggle(this);

            // AppCompat خودش Activity را recreation می‌کند.
        });
    }

    private void updateThemeIcon() {

        if (themeButton == null) {
            return;
        }

        if (ThemeManager.getDarkMode(this)) {
            themeButton.setImageResource(R.drawable.ic_sun);
            themeButton.setContentDescription("تغییر به حالت روشن");
        } else {
            themeButton.setImageResource(R.drawable.ic_moon);
            themeButton.setContentDescription("تغییر به حالت تاریک");
        }
    }

    private void render() {

        Prefs.refreshScore(this);

        greeting.setText(
                "سلام، " + Prefs.getName(this) + " 👋"
        );

        streak.setText(
                String.valueOf(Prefs.getStreak(this))
        );

        best.setText(
                String.valueOf(Prefs.getBestStreak(this))
        );

        level.setText(
                "LVL " + Prefs.getLevel(this)
        );

        xp.setText(
                Prefs.getXp(this) + " XP"
        );

        score.setText(
                Prefs.getTodayScore(this) + "%"
        );

        cleanStatus.setText(
                Prefs.isCleanToday(this)
                        ? "امروز ثبت شد ✓"
                        : "امروز را قدرتمند ثبت کن"
        );

        workoutStatus.setText(
                Prefs.isWorkoutDone(this)
                        ? "تمرین امروز انجام شد ✓"
                        : "حداقل ۲۰ دقیقه حرکت"
        );

        worshipStatus.setText(
                Prefs.isWorshipDone(this)
                        ? "امروز ثبت شد ✓"
                        : "نماز + قرآن + ذکر"
        );

        reportButton.setText(
                "گزارش امروز را ثبت کن  →"
        );

        updateThemeIcon();
    }

    private void showNameDialog(boolean firstRun) {

        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_name);

        if (dialog.getWindow() != null) {

            dialog.getWindow()
                    .setBackgroundDrawable(
                            new ColorDrawable(Color.TRANSPARENT)
                    );
        }

        dialog.setCancelable(!firstRun);

        EditText input =
                dialog.findViewById(R.id.nameInput);

        Button save =
                dialog.findViewById(R.id.saveNameButton);

        TextView title =
                dialog.findViewById(R.id.nameDialogTitle);

        title.setText(
                firstRun
                        ? "شروع قوی از همین‌جا 🔥"
                        : "نامت را ویرایش کن"
        );

        if (Prefs.hasName(this)) {
            input.setText(Prefs.getName(this));
            input.setSelection(input.length());
        }

        save.setOnClickListener(v -> {

            String name =
                    input.getText()
                            .toString()
                            .trim();

            if (name.length() < 2) {

                input.setError(
                        "حداقل ۲ حرف وارد کن"
                );

                return;
            }

            Prefs.saveName(this, name);

            dialog.dismiss();

            render();

            Toast.makeText(
                    this,
                    "ثبت شد؛ بزن بریم 🔥",
                    Toast.LENGTH_SHORT
            ).show();
        });

        dialog.show();
    }

    private void markClean() {

        if (Prefs.markClean(this)) {

            Toast.makeText(
                    this,
                    "امروز را محکم ثبت کردی 💪 +25 XP",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "امروز قبلاً ثبت شده ✅",
                    Toast.LENGTH_SHORT
            ).show();
        }

        render();
    }

    private void markWorkout() {

        if (Prefs.markWorkout(this)) {

            Toast.makeText(
                    this,
                    "تمرین امروز ثبت شد 💪 +20 XP",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "تمرین امروز قبلاً ثبت شده ✅",
                    Toast.LENGTH_SHORT
            ).show();
        }

        render();
    }

    private void markWorship() {

        if (Prefs.markWorship(this)) {

            Toast.makeText(
                    this,
                    "بخش معنوی امروز ثبت شد 🤲 +20 XP",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "امروز قبلاً ثبت شده ✅",
                    Toast.LENGTH_SHORT
            ).show();
        }

        render();
    }

    private void reportDay() {

        if (Prefs.markReport(this)) {

            Toast.makeText(
                    this,
                    "گزارش روزانه ثبت شد ✨ +10 XP",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "گزارش امروز قبلاً ثبت شده ✅",
                    Toast.LENGTH_SHORT
            ).show();
        }

        render();
    }

    private void confirmReset() {

        new AlertDialog.Builder(this)

                .setTitle("ریست رکورد پاکی؟")

                .setMessage(
                        "این کار رکورد فعلی را صفر می‌کند. " +
                        "بهترین رکوردت حفظ می‌شود."
                )

                .setPositiveButton(
                        "ریست کن",
                        (dialog, which) -> {

                            Prefs.resetStreak(this);

                            render();

                            Toast.makeText(
                                    this,
                                    "شروع دوباره هم شجاعته. ادامه بده 🔥",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                )

                .setNegativeButton(
                        "بیخیال",
                        null
                )

                .show();
    }

    private void requestNotificationPermissionIfNeeded() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS
                    },
                    NOTIFICATION_PERMISSION_REQUEST
            );
        }
    }
}