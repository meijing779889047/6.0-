package com.mj.permissionmanager.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.mj.permissionmanager.impl.PermissionCallbacks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C) 2017,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：PermissionManager
 * 类描述：对应用权限进行检测
 * 创建人：Administrator
 * 创建时间：2017/3/13 14:04
 * 修改人：Administrator
 * 修改时间：2017/3/13 14:04
 * 修改备注：
 * Version:  1.0.0
 */
public class EasyPermission {

    private static String  TAG="EasyPermission";
    //应用设置姐界面请求吗
    public static final int SETTINGS_REQ_CODE = 16061;
    /**
     * 检测权限是否打开
     * @param context   上下文
     * @param params    权限
     * @return
     */
    public   static boolean hasPersissions(Context  context,String ...params){
        //sdk是否在23以上  23以下默认授予
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return   true;
        }
        for(String param:params){
            //该权限是否已被授予
            boolean  hasPermission=(ContextCompat.checkSelfPermission(context,param)== PackageManager.PERMISSION_GRANTED);
            if(!hasPermission){
                Log.i(TAG,"该权限没有被授予："+param);
                return  false;
            }
        }
        Log.i(TAG,"所需权限已被授予");
        return   true;
    }

    /**
     * 请求权限授予
     * @param object           上下文  activity   fragment
     * @param rationale        理由：用于解释为什么需要此权限
     * @param requestCode      请求吗
     * @param params           请求权限
     */
    public  static void requestPermissions(Object  object,String rationale,int requestCode,String... params){
        requestPermissions(object,rationale,android.R.string.ok,android.R.string.cancel,requestCode,params);

    }

    /**
     * 请求权限
     * @param object           上下文  activity   fragment
     * @param rationale        理由：用于解释为什么需要此权限
     * @param positiveButton   确定
     * @param negativeButton   取消
     * @param requestCode      请求吗
     * @param params           请求权限
     */
    private static void requestPermissions(final Object object, String rationale, @StringRes int positiveButton, @StringRes int negativeButton, final int requestCode, final String[] params) {
         //1.判断传入的上下文是否是activity或fragment
         checkCallingObjectSuitablity(object);
         //2.是否向用户解释为什么需要此权限
        boolean shouldShowRationale = false;
        for(String param:params){
             shouldShowRationale= shouldShowRationale || shouldShowRequestPermissionRationale(object, param);
        }
        //3.获取activity，显示提示框
        if(shouldShowRationale){
            Activity activity=getActivity(object);
            if(null==activity){
                return;
            }
            AlertDialog  dialog=new AlertDialog.Builder(activity)
                                                .setMessage(rationale)
                                                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         executePermissionsRequest(object, params, requestCode);
                                                     }
                                                 })
                                                 .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int which) {
                                                         if (object instanceof PermissionCallbacks) {
                                                             ((PermissionCallbacks) object).onPermissionsDenied(requestCode, Arrays.asList(params));
                                                         }
                                                     }
                                                 })
                                                  .create();
            dialog.show();

        }else{
            //权限申请
            executePermissionsRequest(object, params, requestCode);
        }

    }


    /**
     * 检测否定的权限列表中的权限是否已经被授予权限，若没有授予，则弹出对话框让其去应用设置中去打开权限
     * @param object
     * @param rationale
     * @param positiveButton
     * @param negativeButton
     * @param negativeButtonOnClickListener
     * @param deniedPerms
     * @return
     */
    public   static   boolean checkDeniedPermissionsNerverAskAgain(final Object  object, String rationale,
                                                                   @StringRes int positiveButton,
                                                                   @StringRes int negativeButton,
                                                                   @Nullable DialogInterface.OnClickListener negativeButtonOnClickListener,
                                                                   List<String> deniedPerms){
        boolean shouldShowRationale;
        for(String perm : deniedPerms) {
        shouldShowRationale = shouldShowRequestPermissionRationale(object, perm);
        if (!shouldShowRationale) {
            final Activity activity = getActivity(object);
            if (null == activity) {
                return true;
            }
            AlertDialog dialog = new   AlertDialog.Builder(activity)
                    .setMessage(rationale)
                    .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
                            startAppSettingsScreen(object, intent);
                        }
                    })
                    .setNegativeButton(negativeButton, negativeButtonOnClickListener)
                    .create();
            dialog.show();
            return true;
        }
    }
       return false;
    }

    /**
     * 权限申请结果
     * @param requestCode    结果码
     * @param permissions    权限
     * @param grantResults
     * @param object         上下文 activitiy/fragment
     */
    public   static  void onRequestPermissionsResult(int  requestCode,String[] permissions,int[]  grantResults,Object object){
        //1.判断传入的上下文是否是activity或fragment
        checkCallingObjectSuitablity(object);
        //2.遍历权限申请结果，获取那些是搜权成功，那些是搜权失败
        //权限授予成功列表集合
        List<String>  granted=new ArrayList<>();
        //权限授予失败列表集合
        List<String>   dentied=new ArrayList<>();

        for (int i = 0; i <permissions.length ; i++) {
            String permm=permissions[i];
            if(grantResults[i]==PackageManager.PERMISSION_GRANTED){//成功授予此权限
                 granted.add(permm);
            }else{
                dentied.add(permm);
            }
        }
        //3.结果回调
        if(null!=granted&&granted.size()>0){
            if(object  instanceof   PermissionCallbacks){
                ((PermissionCallbacks) object).onPermissionsGranted(requestCode,granted);
            }
        }

        if(null!=dentied&&dentied.size()>0){
            if(object  instanceof   PermissionCallbacks){
                ((PermissionCallbacks) object).onPermissionsDenied(requestCode,dentied);
            }
        }
        //所申请的权限都已搜权成功
        if(granted.size()>0&&dentied.size()==0){
            if (object instanceof PermissionCallbacks) {
                ((PermissionCallbacks) object).onPermissionsAllGranted();
            }
        }
    }

    /**
     * 判断当前权限的处理是否处于fragment或者activity中
     * @param object
     */
    private static void checkCallingObjectSuitablity(Object object) {
        //1.判断传入的上下文是否是activity或fragment
        boolean   isActivity=object  instanceof Activity;
        boolean   isSupportFragment=object instanceof Fragment;
        boolean   isAppFragment=object  instanceof android.app.Fragment;
        //sdk是否在23以上
        boolean    isMinSdkM=Build.VERSION.SDK_INT>=Build.VERSION_CODES.M;
        //当前权限的处理是否处于fragment或者activity中
        if(!(isActivity||isSupportFragment||(isAppFragment&&isMinSdkM))){
            if (isAppFragment) {
                throw new IllegalArgumentException(
                        "Target SDK needs to be greater than 23 if caller is android.app.Fragment");
            } else {
                throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
            }
        }
    }

    /**
     * 解释为什么需要这个权限
     * 1.shouldShowRequestPermissionRationale()
       如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
       如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
       注意具体解释原因的这个dialog需要自己实现, 系统没有提供
     * @param object
     * @param param
     * @return
     */
    @TargetApi(23)
    private static boolean shouldShowRequestPermissionRationale(Object object, String param) {
        if(object instanceof  Activity){
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object,param);
        }else  if(object instanceof  Fragment){
            return ((Fragment) object).shouldShowRequestPermissionRationale(param);
        }else  if(object instanceof android.app.Fragment){
            return ((android.app.Fragment)object).shouldShowRequestPermissionRationale(param);
        }
        return false;
    }

    /**
     * 获取activity或fragment对象
     * @param object
     * @return
     */
    @TargetApi(11)
    private static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        } else {
            return null;
        }
    }

    /**
     * 执行权限申请操作
     * @param object        对象
     * @param params        权限
     * @param requestCode   请求吗
     */
    @TargetApi(23)
    private static void executePermissionsRequest(Object object, String[] params, int requestCode) {
        //1.判断传入的上下文是否是activity或fragment
          checkCallingObjectSuitablity(object);
        //2.权限申请操作
        if(object  instanceof   Activity){
             ActivityCompat.requestPermissions((Activity) object,params,requestCode);
        }else if(object  instanceof   Fragment){
            ((Fragment)object).requestPermissions(params,requestCode);
        }else if(object instanceof android.app.Fragment){
            ((android.app.Fragment)object).requestPermissions(params,requestCode);
        }
    }


    @TargetApi(11)
    private static void startAppSettingsScreen(Object object,
                                               Intent intent) {
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        } else if (object instanceof Fragment) {
            ((Fragment) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        }
    }
}
