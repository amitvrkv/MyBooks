package in.shopy.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by am361000 on 27/01/18.
 */

public class MySharedPreference {

    public static String getAddress(Context context) {
        String address = "";

        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(context.getString(in.shopy.R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);

        if (
                sharedPreferences.getString("Name", null) == null
                        || sharedPreferences.getString("Name", null).equalsIgnoreCase("null")
                        || sharedPreferences.getString("contact", null) == null
                        || sharedPreferences.getString("contact", null).equalsIgnoreCase("null")
                        || sharedPreferences.getString("addressline1", null) == null
                        || sharedPreferences.getString("addressline1", null).equalsIgnoreCase("null")
                        || sharedPreferences.getString("addressline2", null) == null
                        || sharedPreferences.getString("addressline2", null).equalsIgnoreCase("null")
                        || sharedPreferences.getString("city", null) == null
                        || sharedPreferences.getString("city", null).equalsIgnoreCase("null")
                        || sharedPreferences.getString("pincode", null) == null
                        || sharedPreferences.getString("pincode", null).equalsIgnoreCase("null")
                        || sharedPreferences.getString("state", null) == null
                        || sharedPreferences.getString("state", null).equalsIgnoreCase("null")
                ) {

            address = "";
        } else {

            address = address + sharedPreferences.getString("Name", null);
            address = address + "\n" + sharedPreferences.getString("contact", null);
            address = address + "\n" + sharedPreferences.getString("addressline1", null);
            address = address + "\n" + sharedPreferences.getString("addressline2", null);
            address = address + " - " + sharedPreferences.getString("pincode", null);
            address = address + "\n" + sharedPreferences.getString("state", null);
        }
        return address;
    }

    public static void updateAddress(Context context, String key, String value) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(context.getString(in.shopy.R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDataFromAddress(Context context, String key) {
        String result = null;
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(context.getString(in.shopy.R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        result = sharedPreferences.getString(key, null);
        return result;
    }

    public static boolean isDeliveryAddressCorrect(Context context) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(context.getString(in.shopy.R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);

        if (
                sharedPreferences.getString("Name", null) == null
                        || sharedPreferences.getString("Name", null).equalsIgnoreCase("null")
                        || sharedPreferences.getString("contact", null) == null
                        || sharedPreferences.getString("contact", null).equalsIgnoreCase("null")
                        || sharedPreferences.getString("addressline1", null) == null
                        || sharedPreferences.getString("addressline1", null).equalsIgnoreCase("null")
                        || sharedPreferences.getString("addressline2", null) == null
                        || sharedPreferences.getString("addressline2", null).equalsIgnoreCase("null")
                        || sharedPreferences.getString("city", null) == null
                        || sharedPreferences.getString("city", null).equalsIgnoreCase("null")
                        || sharedPreferences.getString("pincode", null) == null
                        || sharedPreferences.getString("pincode", null).equalsIgnoreCase("null")
                        || sharedPreferences.getString("state", null) == null
                        || sharedPreferences.getString("state", null).equalsIgnoreCase("null")
                ) {

            return false;

        } else {

            return true;
        }
    }

    public static void clearSharedPreference(Context context) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(context.getString(in.shopy.R.string.sharedPrefDeliveryAddress), MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }
}