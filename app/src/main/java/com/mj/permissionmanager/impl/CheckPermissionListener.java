package com.mj.permissionmanager.impl;

/**
 * Copyright (C) 2017,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：PermissionManager
 * 类描述：接口回调，当所需的权限都被授予时回调
 * 创建人：Administrator
 * 创建时间：2017/3/13 16:12
 * 修改人：Administrator
 * 修改时间：2017/3/13 16:12
 * 修改备注：
 * Version:  1.0.0
 */
public interface CheckPermissionListener {
    //权限通过回调
    void superPermission();
}
