package com.mj.permissionmanager.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mj.permissionmanager.R;
import com.mj.permissionmanager.impl.CheckPermissionListener;
import com.mj.permissionmanager.impl.PermissionCallbacks;
import com.mj.permissionmanager.util.EasyPermission;

import java.util.List;

/**
 * Copyright (C) 2017,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：PermissionManager
 * 类描述:基类
 * 创建人：Administrator
 * 创建时间：2017/3/13 15:54
 * 修改人：Administrator
 * 修改时间：2017/3/13 15:54
 * 修改备注：
 * Version:  1.0.0
 */
public class BaseActivity extends AppCompatActivity   implements PermissionCallbacks {

    private String TAG = "BaseActivity";

    private CheckPermissionListener listener;
    //请求吗
    protected static final int RC_PERM = 123;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 检测所需权限
     *
     * @param listener  接口回调
     * @param resString
     * @param params    所需权限
     */
    public void checkPermission(CheckPermissionListener listener, int resString, String... params) {
        this.listener = listener;
        if (EasyPermission.hasPersissions(this, params)) {
            if (listener != null) {
                this.listener.superPermission();
            }
        } else {
            EasyPermission.requestPermissions(this, getString(resString), RC_PERM, params);
        }
    }


    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /******************权限回调接口********************************************************************/
    /**
     * 同意了某些权限列表，但可能不是全部
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        for (int i = 0; i < perms.size(); i++) {
            Log.i(TAG, "已经被授予的权限：" + perms.get(i) + "--->requestCode:" + requestCode);
        }
    }

    /**
     * 否定的权限列表
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        EasyPermission.checkDeniedPermissionsNerverAskAgain(this,
                getString(R.string.perm_tip),
                R.string.setting, R.string.cancel, null, perms);
        for (int i = 0; i < perms.size(); i++) {
            Log.i(TAG, "未被授予的权限：" + perms.get(i) + "--->requestCode:" + requestCode);
        }
    }

    /**
     * 所有的权限都同意了
     */
    @Override
    public void onPermissionsAllGranted() {
        if (listener != null) {
            listener.superPermission();
        }
        Log.i(TAG, "所需的权限都已被授予");
    }
}