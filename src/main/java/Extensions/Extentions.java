package main.java.Extensions;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Extentions {
    public static float ConvertByteToFloat(byte[] b) {
        ByteBuffer buffer = ByteBuffer.wrap(b);
        return buffer.getFloat();
    }

    public static int ConvertByteToInt(byte[] b) {
        int value = 0;
        for(int i = 0; i<b.length; i++){
            int n = (b[i] < 0 ? (int)b[i] + 256 : (int)b[i]) << (8*i);
            value += n;
        }
        return value;
    }

    public static String ConvertByteToString(byte[] b) {
        String string = new String(b);
        return string;
    }

    public static int ConvertStringToInt(String s) {
        int i = Integer.parseInt(s);
        if (i < 0) {
            i += 256;
        }
        return i;
    }
    public static int ConvertByteToI(byte b) {
        int i = (b & 0xFF);
        return i;
    }

    public static String ConvertDateToString(Calendar calendar) {
        String dateConv;
        DateFormat time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        calendar = Calendar.getInstance();
        dateConv = time.format(calendar.getTime());
        return dateConv;
    }

    public static Date GetDate(Calendar calendar) {
        DateFormat time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        calendar = Calendar.getInstance();
        String dateString = time.format(calendar.getTime());
        try {
            Date date = time.parse(dateString);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
