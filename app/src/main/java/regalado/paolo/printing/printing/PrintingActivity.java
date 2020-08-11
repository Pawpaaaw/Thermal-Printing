package regalado.paolo.printing.printing;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Jesli Albert Bautista on 12/04/2018 10:13 AM.
 * jesli.bautista@safesat.com.ph
 * Satellite GPS (GPS Tracking and Asset Management System, Corp)
 **/

public abstract class PrintingActivity extends Activity implements IPrint {
    public static final String TAG = PrintingActivity.class.getName();
    public static final int ON_PRINT_RESULT = 1444;
    private String mDeviceAddress;
    private OutputStream os;
    private PrintTable helper;
    private Thread mBlutoothConnectThread;
    private int maxLength = 32;

    public PrintingActivity(int maxLength) {
        this.maxLength = maxLength;
    }

    //callback with helper params. helper params should be used

    private void print() {
        Thread t = new Thread() {
            @TargetApi(Build.VERSION_CODES.ECLAIR)
            public void run() {
                try {
                    Thread.sleep(2000);
                    os = BluetoothPrinterUtil.getInstance().getBluetoothSocket().getOutputStream();
                    helper = new Printer(os, maxLength);
                    //Printing code should go here
                    printDetails(helper);
                    Thread.sleep(2000);
                } catch (Exception e) {
                    Log.e(TAG, "Exe ", e);
                } finally {
                    BluetoothPrinterUtil.getInstance().getBluetoothConnectProgressDialog().dismiss();
                    finishPrinting();
                }
            }
        };
        t.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        startPrintingReceipt();
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public void startPrintingReceipt() {
        BluetoothPrinterUtil.getInstance().setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
        if (null != BluetoothPrinterUtil.getInstance().getBluetoothAdapter()) {
            // request to enable BT
            if (!BluetoothPrinterUtil.getInstance().getBluetoothAdapter().isEnabled()) {
                enableBt();
            } else {
                showAvailableBt();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);
        try {
            if (mResultCode == Activity.RESULT_OK) {
                if (mDataIntent != null) {
                    setBtDevice(mDataIntent);
                } else {
                    startPrintingReceipt();
                }
            }

            if (mResultCode == Activity.RESULT_CANCELED) {
                onBackPressed();
            }
        } catch (NullPointerException ex) {
            Toast.makeText(this, "No printer Found.", Toast.LENGTH_SHORT).show();
            BluetoothPrinterUtil.showEnableBluetooth(this, "Pair to a bluetooth printer");
        }

    }

    private void showAvailableBt() {
        //show BT item list
        Intent connectIntent = new Intent(PrintingActivity.this, DeviceListActivity.class);
        startActivityForResult(connectIntent, BluetoothPrinterUtil.REQUEST_CONNECT_DEVICE);
    }

    private void enableBt() {
        Intent enableBtIntent = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent,
                BluetoothPrinterUtil.REQUEST_ENABLE_BT);
    }

    public Runnable getRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    initSocketConnection();
                } catch (IOException eConnectException) {
                    analyzeError(eConnectException);
                } catch (NullPointerException npe) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BluetoothPrinterUtil.showEnableBluetooth(PrintingActivity.this, "Pair to a bluetooth printer");
                        }
                    });
                }
            }
        };
    }

    private void initSocketConnection() throws IOException {
        if (BluetoothPrinterUtil.getInstance().getBluetoothAdapter().checkBluetoothAddress(mDeviceAddress)) {
            ParcelUuid[] parcels = BluetoothPrinterUtil.getInstance().getBluetoothDevice().getUuids();
            UUID applicationUUID = UUID.fromString(parcels[0].toString());

            createSocket(applicationUUID);

            if (BluetoothPrinterUtil.getInstance().getBluetoothAdapter().isDiscovering()) {
                Log.d(TAG, "Cancelling discovery...");
                BluetoothPrinterUtil.getInstance().getBluetoothAdapter().cancelDiscovery();
            }

            checkIfSocketIsValid();
            // signals start of priting
            mHandler.sendEmptyMessage(0);
        } else {
            Log.d(TAG, "Printer not found");
        }
    }


    private void setBtDevice(Intent mDataIntent) {
        Bundle mExtra = mDataIntent.getExtras();
        mDeviceAddress = mExtra.getString("DeviceAddress");

        BluetoothPrinterUtil.getInstance().setBluetoothDevice(BluetoothPrinterUtil.getInstance().getBluetoothAdapter()
                .getRemoteDevice(mDeviceAddress));
        BluetoothPrinterUtil.getInstance().setBluetoothConnectProgressDialog(ProgressDialog.show(this,
                "Please wait...",
                "Printing to ".concat(BluetoothPrinterUtil.getInstance().getBluetoothDevice().getName()).concat(":")
                        .concat(BluetoothPrinterUtil.getInstance().getBluetoothDevice().getAddress()),
                true, true));
        Log.w("Device Addres", mDeviceAddress);
        mBlutoothConnectThread = new Thread(getRunnable());
        mBlutoothConnectThread.start();
    }


    private void analyzeError(IOException eConnectException) {
        if (eConnectException != null) {//can be null
            popUpMessage("Failed printing. Please try again.");
        }
        Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
        closeSocket(BluetoothPrinterUtil.getInstance().getBluetoothSocket());
        BluetoothPrinterUtil.getInstance().getBluetoothConnectProgressDialog().cancel();
        returnErrorResult();
    }

    private void checkIfSocketIsValid() throws IOException {
        if (BluetoothPrinterUtil.getInstance().getBluetoothSocket() != null) {
            if (!BluetoothPrinterUtil.getInstance().getBluetoothSocket().isConnected()) {
                BluetoothPrinterUtil.getInstance().getBluetoothSocket().connect();
            } else {
                Log.d(TAG, "Socket is still connected.");
            }
        } else {
            Log.d(TAG, "Socket is NULL");
        }
    }

    private void createSocket(UUID applicationUUID) {
        try {
            if (Build.VERSION.SDK_INT >= 10) {
                // We're trying to create an insecure socket, which is only
                // supported in API 10 and up. Otherwise, we try a secure socket
                // which is in API 7 and up.
                BluetoothPrinterUtil.getInstance().setBluetoothSocket(BluetoothPrinterUtil.getInstance().getBluetoothDevice()
                        .createInsecureRfcommSocketToServiceRecord(applicationUUID));
            } else {
                BluetoothPrinterUtil.getInstance().setBluetoothSocket(BluetoothPrinterUtil.getInstance().getBluetoothDevice()
                        .createRfcommSocketToServiceRecord(applicationUUID));
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not createRfcommSocketToServiceRecord ".concat(applicationUUID.toString()));
        }
    }

    private void popUpMessage(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PrintingActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            if (nOpenSocket != null) {
                Log.d(TAG, "Closing socket...");
                nOpenSocket.close();
            }
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                //print when message detected the same
                print();
            }
        }
    };


    private String getFormat(int spaceBetween) {
        return "%s%" + spaceBetween + "s";
    }

    private String getFormat(int spaceOnFront, int space) {
        return "%-" + spaceOnFront + "s%" + space + "s";
    }


    private void finishPrinting() {
        closeSocket(BluetoothPrinterUtil.getInstance().getBluetoothSocket());
        BluetoothPrinterUtil.getInstance().setBluetoothDevice(null);
        BluetoothPrinterUtil.getInstance().setBluetoothSocket(null);
        BluetoothPrinterUtil.getInstance().setBluetoothAdapter(null);
        BluetoothPrinterUtil.getInstance().setBluetoothConnectProgressDialog(null);
        finish();
    }

    private void returnErrorResult() {
        finish();
    }
}
