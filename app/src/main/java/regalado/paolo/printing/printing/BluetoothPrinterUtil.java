package regalado.paolo.printing.printing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by Jesli Albert Bautista on 12/04/2018 10:17 AM.
 * jesli.bautista@safesat.com.ph
 * Satellite GPS (GPS Tracking and Asset Management System, Corp)
 **/

public class BluetoothPrinterUtil {
    private static BluetoothPrinterUtil INSTANCE;
    private BluetoothPrinterUtil() {}
    public static BluetoothPrinterUtil getInstance(){
        if(null == INSTANCE){
            INSTANCE = new BluetoothPrinterUtil();
        }
        return INSTANCE;
    }

    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    private BluetoothDevice mBluetoothDevice;

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.mBluetoothAdapter = bluetoothAdapter;
    }

    public ProgressDialog getBluetoothConnectProgressDialog() {
        return mBluetoothConnectProgressDialog;
    }

    public void setBluetoothConnectProgressDialog(ProgressDialog bluetoothConnectProgressDialog) {
        this.mBluetoothConnectProgressDialog = bluetoothConnectProgressDialog;
    }

    public BluetoothSocket getBluetoothSocket() {
        return mBluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.mBluetoothSocket = bluetoothSocket;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.mBluetoothDevice = bluetoothDevice;
    }

    public static void showEnableBluetooth(final Activity activity, String title) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage("Do you want to check bluetooth devices?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
            }
        });

        alertDialogBuilder.setNegativeButton("No", null);
        alertDialogBuilder.show();

    }

    public static boolean isBluetoothEnabled(Activity activity){
        boolean isBluetoothEnabled = false;
        BluetoothPrinterUtil.getInstance().setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
        if (null != BluetoothPrinterUtil.getInstance().getBluetoothAdapter()) {
            if (!BluetoothPrinterUtil.getInstance().getBluetoothAdapter().isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent,
                        BluetoothPrinterUtil.REQUEST_ENABLE_BT);
            } else {
                isBluetoothEnabled = true;
            }
        }
        return isBluetoothEnabled;
    }
}
