package com.jaimovie.acube.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeDateUtils {

    /*public static String changeDateFormat1(String date) {
        try {
            //Mar 23, 2016
            Log.e("Actual format", "-->" + date);
//            DateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
//            DateFormat df1 = new SimpleDateFormat("MMM dd,yyyy");
            @SuppressLint("SimpleDateFormat") DateFormat df1 = new SimpleDateFormat("MMM dd, yyyy");
            @SuppressLint("SimpleDateFormat") DateFormat df3 = new SimpleDateFormat("dd MMM yyyy");

//            SimpleDateFormat format = new SimpleDateFormat("MMM dd,yyyy  hh:mm a");
            @SuppressLint("SimpleDateFormat") DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");

            return df2.format(df1.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }*/

    public static String changeDateFormat1(String date) {
        String returnVal = "";
        /*
         * Set the permissible formats.
         * A better approach here would be to define all formats in a .properties file
         * and load the file during execution.
         */
        String[] permissFormats = new String[]{"MMM dd, yyyy", "dd MMM yyyy", "yyyy-MM-dd hh:mm:ss.sss", "yyyy-MM-dd hh:mm", "dd/MM/yyyy",
                "dd-MM-yyyy", "MM-dd-yyyy", "ddMMyyyy", "dd-MMM-yyyy"};
        for (int i = 0; i < permissFormats.length; i++) {
            try {
                DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat sdfObj = new SimpleDateFormat(permissFormats[i]);
                sdfObj.setLenient(false);
                sdfObj.parse(date);
                returnVal = df2.format(sdfObj.parse(date));
                Log.e("Date--", "Looks like a valid date for Date Value :" + date + ": For Format:" + permissFormats[i]);
                break;
            } catch (ParseException e) {
                Log.e("Date--", "Parse Exception Occured for Date Value :" + date + ":And Format:" + permissFormats[i]);
            }
        }
        return returnVal;
    }

    public static String isDateValid(String dateValue) {
        String returnVal = "";
        /*
         * Set the permissible formats.
         * A better approach here would be to define all formats in a .properties file
         * and load the file during execution.
         */
        String[] permissFormats = new String[]{"MMM dd, yyyy", "dd MMM yyyy", "yyyy-MM-dd hh:mm:ss.sss", "yyyy-MM-dd hh:mm", "dd/MM/yyyy",
                "dd-MM-yyyy", "MM-dd-yyyy", "ddMMyyyy", "dd-MMM-yyyy"};
        for (int i = 0; i < permissFormats.length; i++) {
            try {
                DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat sdfObj = new SimpleDateFormat(permissFormats[i]);
                sdfObj.setLenient(false);
                sdfObj.parse(dateValue);
                returnVal = df2.format(sdfObj.parse(dateValue));
                Log.e("Date--", "Looks like a valid date for Date Value :" + dateValue + ": For Format:" + permissFormats[i]);
                break;
            } catch (ParseException e) {
                Log.e("Date--", "Parse Exception Occured for Date Value :" + dateValue + ":And Format:" + permissFormats[i]);
            }
        }
        return returnVal;
    }

}
