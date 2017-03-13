package com.mj.permissionmanager.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mj.permissionmanager.R;
import com.mj.permissionmanager.impl.CheckPermissionListener;


/**
 * A placeholder fragment containing a simple view.
 */
public class BlueToothActivityFragment extends Fragment implements View.OnClickListener {

    private AppCompatButton mBtn;
    //sdk>=23时，必须申请这四个权限
    private String[] bluePermiss=new String[]{Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION ,Manifest.permission.ACCESS_FINE_LOCATION};
    private BluetoothManager blueToothManager;
    private BluetoothAdapter bluethoothAdapter;
    private Handler mHandler=new Handler();

    public BlueToothActivityFragment() {
    }

    View mRootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.fragment_bluetooth, container, false);
        mBtn = (AppCompatButton) mRootView.findViewById(R.id.btn_bluetooth);
        mBtn.setOnClickListener(this);
        return mRootView;
    }


    @Override
    public void onClick(final View v) {
        //1.判断当前设备是否支持ble设备
        if(!getActivity().getPackageManager().hasSystemFeature(getActivity().getPackageManager().FEATURE_BLUETOOTH_LE)){
            Toast.makeText(getActivity(),"当前设备不支持ble，即将推出",Toast.LENGTH_SHORT).show();

        }
        //2.若支持蓝牙设备，判断设备是否打开，若没有打开，请求打开蓝牙设备
        blueToothManager= (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        bluethoothAdapter=blueToothManager.getAdapter();
        if(bluethoothAdapter==null||!bluethoothAdapter.isEnabled()){
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,10001);
        }else{
            ((BaseActivity)getActivity()).checkPermission(new CheckPermissionListener() {
                @Override
                public void superPermission() {
                    Toast(v,"ble蓝牙可用");
                }
            },R.string.bluetooth, bluePermiss);

        }
    }


    public void Toast(View view, String reSting) {
        Snackbar.make(view, reSting, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
