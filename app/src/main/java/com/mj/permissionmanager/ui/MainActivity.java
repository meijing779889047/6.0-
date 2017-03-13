package com.mj.permissionmanager.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mj.permissionmanager.R;
import com.mj.permissionmanager.impl.CheckPermissionListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    @Bind(R.id.btn_blue)
    AppCompatButton btnBlue;
    @Bind(R.id.btn_camera)
    AppCompatButton btnCamera;
    @Bind(R.id.btn_fragment_bluetooth)
    AppCompatButton btnFragmentBluetooth;
    @Bind(R.id.btn_fragment_camera)
    AppCompatButton btnFragmentCamera;
    @Bind(R.id.container)
    LinearLayout container;
    @Bind(R.id.fragment)
    FrameLayout fl;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private String TAG="MainActivity";

     //sdk>=23时，必须申请这四个权限
    private String[] bluePermiss=new String[]{Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,
                          Manifest.permission.ACCESS_COARSE_LOCATION ,Manifest.permission.ACCESS_FINE_LOCATION};
    private BluetoothManager blueToothManager;
    private BluetoothAdapter bluethoothAdapter;
    private Handler mHandler=new Handler();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);


    }


    public void Toast(View view, String reSting) {
        Snackbar.make(view, reSting, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @OnClick({R.id.btn_blue, R.id.btn_camera, R.id.btn_fragment_bluetooth, R.id.btn_fragment_camera})
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btn_blue:
                //1.判断当前设备是否支持ble设备
                if(!getPackageManager().hasSystemFeature(getPackageManager().FEATURE_BLUETOOTH_LE)){
                    Toast.makeText(MainActivity.this,"当前设备不支持ble，即将推出",Toast.LENGTH_SHORT).show();
                    finish();
                }
                //2.若支持蓝牙设备，判断设备是否打开，若没有打开，请求打开蓝牙设备
                blueToothManager= (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                bluethoothAdapter=blueToothManager.getAdapter();
                if(bluethoothAdapter==null||!bluethoothAdapter.isEnabled()){
                        Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                       startActivityForResult(intent,10001);
                }else{
                    checkPermission(new CheckPermissionListener() {
                        @Override
                        public void superPermission() {
                            Toast(view,"ble蓝牙可用");
                            scanBlueToothDevice(true);
                        }
                    },R.string.bluetooth, bluePermiss);

                }
                break;
            case R.id.btn_camera:
                checkPermission(new CheckPermissionListener() {
                    @Override
                    public void superPermission() {
                        Toast(view,"相机可用");
                    }
                },R.string.camera, Manifest.permission.CAMERA);
                break;
            case R.id.btn_fragment_bluetooth:
                fl.removeAllViews();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new BlueToothActivityFragment()).commit();
                break;
            case R.id.btn_fragment_camera:
                fl.removeAllViews();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new CameraActivityFragment()).commit();
                break;
        }
    }


    /**
     * 扫描蓝牙设备
     */
    private void scanBlueToothDevice(boolean isEnable) {
        if(bluethoothAdapter!=null) {
            if (isEnable) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bluethoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
                        Log.i(TAG,"扫描完成");

                    }
                }, 10000);
                Log.i(TAG,"开始扫描");
                bluethoothAdapter.startLeScan(mLeScanCallback);//开始扫描
            } else {
                Log.i(TAG,"结束扫描");
                bluethoothAdapter.stopLeScan(mLeScanCallback);//停止扫描
            }
        }
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.i(TAG,"设备名："+device.getName()+"设备地址："+device.getAddress());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    };

}
