package com.sk.markethub.util;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREF_NAME = "sk_markethub_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";

    private static SharedPreferences prefs;

    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void saveToken(String token) {
        if (prefs != null) {
            prefs.edit().putString(KEY_TOKEN, token).apply();
        }
    }

    public static String getToken() {
        return prefs != null ? prefs.getString(KEY_TOKEN, null) : null;
    }

    public static void saveUserInfo(String email, String name) {
        if (prefs != null) {
            prefs.edit().putString(KEY_USER_EMAIL, email).putString(KEY_USER_NAME, name).apply();
        }
    }

    public static String getUserEmail() {
        return prefs != null ? prefs.getString(KEY_USER_EMAIL, "") : "";
    }

    public static String getUserName() {
        return prefs != null ? prefs.getString(KEY_USER_NAME, "") : "";
    }

    public static void saveServerIp(String ip) {
        if (prefs != null) {
            prefs.edit().putString("server_ip", ip).apply();
        }
    }

    public static String getServerIp() {
        return prefs != null ? prefs.getString("server_ip", "https://sk-markethub-apnabazaar.onrender.com/api/") : "https://sk-markethub-apnabazaar.onrender.com/api/";
    }

    public static void clear() {
        if (prefs != null) {
            prefs.edit().clear().apply();
        }
    }
}
