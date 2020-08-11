package regalado.paolo.printing.printing;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Jan Paolo Regalado on 7/31/18.
 * jan.regalado@safesat.com.ph
 * Sattelite GPS (GPS Tracking and Asset Management System)
 */
public class Printer implements PrintTable {
    private OutputStream os;
    private int maxLength = 0;

    public Printer(OutputStream os, int maxLength) {
        this.os = os;
        this.maxLength = maxLength;
    }

    //Does the writing per line
    public void writeOutput(String object, byte fontSize, byte alignment) throws IOException {
        byte[] font = {0x1B, 0x21, fontSize};
        os.write(font);

        byte[] align = {0x1B, 0x61, alignment};
        os.write(align);


        os.write(object.getBytes(), 0, object.getBytes().length);
        os.write("\n".getBytes());
    }

    @Override
    public void writeByte(byte[] byteArray) throws IOException {
        String command = new String(byteArray);
        os.write(command.getBytes(), 0, command.getBytes().length);
    }


    public int calculateSpace(String title) {
        return maxLength - title.length();
    }

    private String getFormat(int spaceBetween) {
        return "%s%" + spaceBetween + "s";
    }

    @Override
    //for left and right setup
    public void leftRightFormatNormal(String title, String body) throws IOException {
        int leftTextDefaultLength = maxLength/2;
        if (title.length() > leftTextDefaultLength) {
            String firstLeftText = title.substring(0, leftTextDefaultLength);
            String secondLeftText = title.substring(leftTextDefaultLength, title.length());
            printNormalLeft(firstLeftText);
            int space = calculateSpace(secondLeftText);
            String format = getFormat(space);
            writeOutput(String.format(format, secondLeftText, body), NORMAL, ALIGN_RIGHT);
        } else {
            int space = calculateSpace(title);
            String format = getFormat(space);
            writeOutput(String.format(format, title, body), NORMAL, ALIGN_RIGHT);
        }
    }

    @Override
    //for left and right setup
    public void leftRightFormatBold(String title, String body) throws IOException {
        int space = calculateSpace(title);
        String format = getFormat(space);
        writeOutput(String.format(format, title, body), BOLD, ALIGN_RIGHT);
    }

    @Override
    //for left and right setup
    public void leftRightFormatLarge(String title, String body) throws IOException {
        int space = calculateSpace(title);
        String format = getFormat(space);
        writeOutput(String.format(format, title, body), LARGE, ALIGN_RIGHT);
    }

    @Override
    //for left and right setup
    public void leftRightFormatSmall(String title, String body) throws IOException {
        int space = calculateSpace(title);
        String format = getFormat(space);
        writeOutput(String.format(format, title, body), SMALL, ALIGN_RIGHT);
    }

    @Override
    public void printLine(String message, byte fontSize, byte alignment) throws IOException {
        writeOutput(message, fontSize, alignment);
    }

    @Override
    public void printNormal(String message, byte alignment) throws IOException {
        writeOutput(message, NORMAL, alignment);
    }

    @Override
    public void printSmall(String message, byte alignment) throws IOException {
        writeOutput(message, SMALL, alignment);
    }

    @Override
    public void printLarge(String message, byte alignment) throws IOException {
        writeOutput(message, LARGE, alignment);
    }

    @Override
    public void printBold(String message, byte alignment) throws IOException {
        writeOutput(message, BOLD, alignment);
    }

    @Override
    public void printNormalCenter(String message) throws IOException {
        writeOutput(message, NORMAL, ALIGN_CENTER);
    }

    @Override
    public void printNormalRight(String message) throws IOException {
        writeOutput(message, NORMAL, ALIGN_RIGHT);
    }

    @Override
    public void printNormalLeft(String message) throws IOException {
        writeOutput(message, NORMAL, ALIGN_LEFT);
    }

    @Override
    public void printBoldCenter(String message) throws IOException {
        writeOutput(message, BOLD, ALIGN_CENTER);
    }

    @Override
    public void printBoldRight(String message) throws IOException {
        writeOutput(message, BOLD, ALIGN_RIGHT);
    }

    public void printBoldLeft(String message) throws IOException {
        writeOutput(message, BOLD, ALIGN_LEFT);
    }

    @Override
    public void printSmallCenter(String message) throws IOException {
        writeOutput(message, SMALL, ALIGN_CENTER);
    }

    @Override
    public void printSmallRight(String message) throws IOException {
        writeOutput(message, SMALL, ALIGN_RIGHT);
    }

    @Override
    public void printSmallLeft(String message) throws IOException {
        writeOutput(message, SMALL, ALIGN_LEFT);
    }

    public void printLargeCenter(String message) throws IOException {
        writeOutput(message, LARGE, ALIGN_CENTER);
    }

    public void printLargeRight(String message) throws IOException {
        writeOutput(message, LARGE, ALIGN_RIGHT);
    }

    @Override
    public void printLargeLeft(String message) throws IOException {
        writeOutput(message, LARGE, ALIGN_LEFT);
    }

    @Override
    public void printPhoto(Bitmap img) throws IOException {
        try {
            Bitmap bmp = img;
            if (bmp != null) {
                byte[] command = ImageDecoder.decodeBitmap(bmp);
                os.write(ALIGN_CENTER);
                os.write(command);
            } else {
                Log.e("Print Photo error", "the file isn't exists");
                throw new IOException("Failed Printing Image");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed Printing Image");
        }
    }

}
