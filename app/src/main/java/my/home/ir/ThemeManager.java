package my.home.ir;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public final class ThemeManager {

    private static final String PREFS = "myhome_theme";
    private static final String KEY_DARK = "dark_mode";

    private ThemeManager() {
    }

    public static void applySavedTheme(Context context) {
        boolean dark = getDarkMode(context);

        AppCompatDelegate.setDefaultNightMode(
                dark
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    public static boolean getDarkMode(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        return prefs.getBoolean(KEY_DARK, false);
    }

    public static void toggle(Context context) {
        boolean newValue = !getDarkMode(context);

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_DARK, newValue)
                .apply();

        AppCompatDelegate.setDefaultNightMode(
                newValue
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}