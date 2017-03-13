package com.mj.permissionmanager.impl;

import android.support.v4.app.ActivityCompat;

import java.util.List;

/**
 * Copyright (C) 2017,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：PermissionManager
 * 类描述：权限是否被授予回调
 * 创建人：Administrator
 * 创建时间：2017/3/13 15:23
 * 修改人：Administrator
 * 修改时间：2017/3/13 15:23
 * 修改备注：
 * Version:  1.0.0
 */
public interface PermissionCallbacks extends ActivityCompat.OnRequestPermissionsResultCallback {

    void onPermissionsGranted(int requestCode, List<String> perms);

    void onPermissionsDenied(int requestCode, List<String> perms);

    void onPermissionsAllGranted();
}
