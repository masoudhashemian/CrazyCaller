package ir.xiollet.crazycaller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by cafebazaar on 4/16/17.
 */

public class Utils {

    public final static String PHONE_NUMBERS_KEY = "phone_numbers_key";

    public static void addPhoneNumber(Context context, String phoneNumber) {
        phoneNumber = phoneNumber + ",";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String phoneNumbers = sharedPreferences.getString(PHONE_NUMBERS_KEY, "");
        if (!phoneNumbers.contains(phoneNumber)) {
            phoneNumbers = phoneNumbers + phoneNumber;
            sharedPreferences.edit().putString(PHONE_NUMBERS_KEY, phoneNumbers).commit();
        }
    }

    public static void removePhoneNumber(Context context, String phoneNumber) {
        phoneNumber = phoneNumber + ",";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String phoneNumbers = sharedPreferences.getString(PHONE_NUMBERS_KEY, "");
        if (phoneNumbers.contains(phoneNumber)) {
            phoneNumbers = phoneNumbers.replace(phoneNumber, "");
            sharedPreferences.edit().putString(PHONE_NUMBERS_KEY, phoneNumbers).commit();
        }
    }

    public static String[] getPhoneNumbers(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String phoneNumbers = sharedPreferences.getString(PHONE_NUMBERS_KEY, "");
        return phoneNumbers.split(",");
    }


}
