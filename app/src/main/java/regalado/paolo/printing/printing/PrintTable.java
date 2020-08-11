package regalado.paolo.printing.printing;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Created by Jan Paolo Regalado on 7/31/18.
 * jan.regalado@safesat.com.ph
 * Sattelite GPS (GPS Tracking and Asset Management System)
 */
public interface PrintTable {

    public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, (byte) 255, 3};
    //alignment
    public static byte ALIGN_LEFT = 0x00;
    public static byte ALIGN_CENTER = 0x01;
    public static byte ALIGN_RIGHT = 0x02;

    //textsize
    public static byte SMALL = 0x1;
    public static byte NORMAL = 0;
    public static byte LARGE = 0x10;

    public static byte BOLD = 0x8;
    public static byte NEW_LINE = 0x13;

    void writeByte(byte[] byteArray) throws IOException;

    void leftRightFormatNormal(String title, String body) throws IOException;

    void leftRightFormatBold(String title, String body) throws IOException;

    void leftRightFormatLarge(String title, String body) throws IOException;

    void leftRightFormatSmall(String title, String body) throws IOException;

    void printLine(String message, byte fontSize, byte alignment) throws IOException;

    void printNormal(String message, byte alignment) throws IOException;

    void printSmall(String message, byte alignment) throws IOException;

    void printLarge(String message, byte alignment) throws IOException;

    void printBold(String message, byte alignment) throws IOException;

    void printNormalCenter(String message) throws IOException;

    void printNormalRight(String message) throws IOException;

    void printNormalLeft(String message) throws IOException;

    void printBoldCenter(String message) throws IOException;

    void printBoldRight(String message) throws IOException;

    void printBoldLeft(String message) throws IOException;

    void printSmallCenter(String message) throws IOException;

    void printSmallRight(String message) throws IOException;

    void printSmallLeft(String message) throws IOException;

    void printLargeCenter(String message) throws IOException;

    void printLargeRight(String message) throws IOException;

    void printLargeLeft(String message) throws IOException;

    void printPhoto(Bitmap img) throws IOException;
}
