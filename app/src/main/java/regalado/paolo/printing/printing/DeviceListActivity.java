package regalado.paolo.printing.printing;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

import regalado.paolo.printing.R;

/**
 * Created by Jesli Albert Bautista on 12/04/2018 10:24 AM.
 * jesli.bautista@safesat.com.ph
 * Satellite GPS (GPS Tracking and Asset Management System, Corp)
 **/

public class DeviceListActivity extends Activity {
    protected static final String TAG = "BluetoothPrinter";
    private static BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.printer_device_list);

        setResult(Activity.RESULT_CANCELED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.printer_device_name);

        ListView mPairedListView = findViewById(R.id.paired_devices);
        mPairedListView.setAdapter(mPairedDevicesArrayAdapter);
        mPairedListView.setOnItemClickListener(mDeviceClickListener);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();

        if (mPairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice mDevice : mPairedDevices) {
                mPairedDevicesArrayAdapter.add(mDevice.getName() + "\n" + mDevice.getAddress());
            }
        } else {
            String mNoDevices = "None Paired";//getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(mNoDevices);
            BluetoothPrinterUtil.showEnableBluetooth(this, "Pair to a bluetooth printer");
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelBluetoothDiscovery();
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onPause() {
        super.onPause();
        cancelBluetoothDiscovery();
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Bundle mBundle = new Bundle();
        Intent mBackIntent = new Intent();
        mBackIntent.putExtras(mBundle);
        setResult(Activity.RESULT_CANCELED, mBackIntent);
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @TargetApi(Build.VERSION_CODES.ECLAIR)
        public void onItemClick(AdapterView<?> mAdapterView, View mView, int mPosition, long mLong) {
            cancelBluetoothDiscovery();
            String mDeviceInfo = ((TextView) mView).getText().toString();
            try {
                String mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);
                Log.v(TAG, "Device_Address " + mDeviceAddress);

                Bundle mBundle = new Bundle();
                mBundle.putString("DeviceAddress", mDeviceAddress);
                Intent mBackIntent = new Intent();
                mBackIntent.putExtras(mBundle);
                setResult(Activity.RESULT_OK, mBackIntent);
                finish();
            } catch (Exception e) {
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancelBluetoothDiscovery() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }
}