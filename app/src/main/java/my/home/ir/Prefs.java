package my.home.ir;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Prefs {
    private static final String FILE = "myhome_prefs";
    private static final String KEY_NAME = "name";
    private static final String KEY_STREAK = "streak";
    private static final String KEY_BEST = "best_streak";
    private static final String KEY_XP = "xp";
    private static final String KEY_LAST_REPORT = "last_report";
    private static final String KEY_TODAY_SCORE_DATE = "today_score_date";
    private static final String KEY_TODAY_SCORE = "today_score";
    private static final String KEY_WORKOUT = "workout_done_date";
    private static final String KEY_WORSHIP = "worship_done_date";
    private static final String KEY_CLEAN = "clean_done_date";

    private Prefs() { }

    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static String todayKey() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    public static String getName(Context context) {
        return get(context).getString(KEY_NAME, "قهرمان");
    }

    public static boolean hasName(Context context) {
        return get(context).contains(KEY_NAME) && !getName(context).trim().isEmpty();
    }

    public static void saveName(Context context, String name) {
        get(context).edit().putString(KEY_NAME, name.trim()).apply();
    }

    public static int getStreak(Context context) {
        normalizeStreak(context);
        return get(context).getInt(KEY_STREAK, 0);
    }

    public static int getBestStreak(Context context) {
        return get(context).getInt(KEY_BEST, 0);
    }

    public static int getXp(Context context) {
        return get(context).getInt(KEY_XP, 0);
    }

    public static int getLevel(Context context) {
        return Math.max(1, getXp(context) / 100 + 1);
    }

    public static int getTodayScore(Context context) {
        String today = todayKey();
        if (!today.equals(get(context).getString(KEY_TODAY_SCORE_DATE, ""))) return 0;
        return get(context).getInt(KEY_TODAY_SCORE, 0);
    }

    public static boolean isCleanToday(Context context) {
        return todayKey().equals(get(context).getString(KEY_CLEAN, ""));
    }

    public static boolean isWorkoutDone(Context context) {
        return todayKey().equals(get(context).getString(KEY_WORKOUT, ""));
    }

    public static boolean isWorshipDone(Context context) {
        return todayKey().equals(get(context).getString(KEY_WORSHIP, ""));
    }

    public static void addXp(Context context, int amount) {
        int next = Math.min(99999, getXp(context) + Math.max(0, amount));
        get(context).edit().putInt(KEY_XP, next).apply();
    }

    public static boolean markClean(Context context) {
        String today = todayKey();
        SharedPreferences p = get(context);
        if (today.equals(p.getString(KEY_CLEAN, ""))) return false;
        int newStreak = getStreak(context) + 1;
        int best = Math.max(getBestStreak(context), newStreak);
        p.edit().putString(KEY_CLEAN, today).putInt(KEY_STREAK, newStreak).putInt(KEY_BEST, best).apply();
        addXp(context, 25);
        return true;
    }

    public static void resetStreak(Context context) {
        get(context).edit().putInt(KEY_STREAK, 0).remove(KEY_CLEAN).apply();
    }

    public static boolean markWorkout(Context context) {
        SharedPreferences p = get(context);
        String today = todayKey();
        if (today.equals(p.getString(KEY_WORKOUT, ""))) return false;
        p.edit().putString(KEY_WORKOUT, today).apply();
        addXp(context, 20);
        return true;
    }

    public static boolean markWorship(Context context) {
        SharedPreferences p = get(context);
        String today = todayKey();
        if (today.equals(p.getString(KEY_WORSHIP, ""))) return false;
        p.edit().putString(KEY_WORSHIP, today).apply();
        addXp(context, 20);
        return true;
    }

    public static boolean markReport(Context context) {
        SharedPreferences p = get(context);
        String today = todayKey();
        if (today.equals(p.getString(KEY_LAST_REPORT, ""))) return false;
        p.edit().putString(KEY_LAST_REPORT, today).apply();
        addXp(context, 10);
        return true;
    }

    public static void refreshScore(Context context) {
        int score = 0;
        if (isCleanToday(context)) score += 40;
        if (isWorkoutDone(context)) score += 25;
        if (isWorshipDone(context)) score += 25;
        if (todayKey().equals(get(context).getString(KEY_LAST_REPORT, ""))) score += 10;
        get(context).edit().putString(KEY_TODAY_SCORE_DATE, todayKey()).putInt(KEY_TODAY_SCORE, score).apply();
    }

    private static void normalizeStreak(Context context) {
        SharedPreferences p = get(context);
        String clean = p.getString(KEY_CLEAN, "");
        if (clean.isEmpty()) return;
        long cleanTime = parseDate(clean);
        long today = parseDate(todayKey());
        long days = (today - cleanTime) / 86400000L;
        if (days > 1) p.edit().putInt(KEY_STREAK, 0).apply();
    }

    private static long parseDate(String value) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(value).getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}
