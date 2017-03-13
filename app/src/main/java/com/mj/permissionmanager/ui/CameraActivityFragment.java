package com.mj.permissionmanager.ui;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.mj.permissionmanager.R;
import com.mj.permissionmanager.impl.CheckPermissionListener;


/**
 * A placeholder fragment containing a simple view.
 */
public class CameraActivityFragment extends Fragment implements View.OnClickListener {

    private AppCompatButton mBtn;

    public CameraActivityFragment() {
    }

    View mRootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.fragment_main, container, false);
        mBtn = (AppCompatButton) mRootView.findViewById(R.id.btn_camera);
        mBtn.setOnClickListener(this);
        return mRootView;
    }


    @Override
    public void onClick(View v) {
        ((BaseActivity)getActivity()).checkPermission(new CheckPermissionListener() {
            @Override
            public void superPermission() {
                ((MainActivity)getActivity()).Toast(mRootView,"在fragment中相机可以用");
            }
        }, R.string.camera,Manifest.permission.CAMERA);
    }
}
