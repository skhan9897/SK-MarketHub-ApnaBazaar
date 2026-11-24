package com.sk.markethub.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.core.content.ContextCompat;

import java.util.List;

public class SimVerificationUtil {

    public static String cleanNumber(String rawNumber) {
        if (rawNumber == null) return "";
        String digits = rawNumber.replaceAll("[^0-9]", "");
        if (digits.length() > 10) {
            return digits.substring(digits.length() - 10);
        }
        return digits;
    }

    public static boolean isSimNumberMatching(Context context, String enteredNumber) {
        String cleanEntered = cleanNumber(enteredNumber);
        if (cleanEntered.length() < 10) return false;

        // If phone permission is granted, check active SIM numbers
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    SubscriptionManager subManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                    if (subManager != null) {
                        List<SubscriptionInfo> subList = subManager.getActiveSubscriptionInfoList();
                        if (subList != null && !subList.isEmpty()) {
                            for (SubscriptionInfo info : subList) {
                                String simNumber = cleanNumber(info.getNumber());
                                if (!simNumber.isEmpty() && simNumber.equals(cleanEntered)) {
                                    return true;
                                }
                            }
                        }
                    }
                }

                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) {
                    String simNumber = cleanNumber(tm.getLine1Number());
                    if (!simNumber.isEmpty() && simNumber.equals(cleanEntered)) {
                        return true;
                    }
                }
            } catch (Exception ignored) {}
        }

        // If permission is not granted or carrier hides SIM line number, allow fallback validation if number is 10 digits
        return cleanEntered.length() == 10;
    }
}
