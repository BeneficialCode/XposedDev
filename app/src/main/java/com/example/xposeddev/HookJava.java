package com.example.xposeddev;

import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/*
implements IXposedHookLoadPackage {
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
    	if (!lpparam.packageName.equals("com.qtz.game.jltx"))
            return;

    }
}

 */
public class HookJava implements IXposedHookLoadPackage {
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.qtz.game.jltx"))
            return;

        ClassLoader classLoader = lpparam.classLoader;
        Class WXSharedAPI = classLoader.loadClass("com.qtz.game.jltx.wxapi.WXSharedAPI");

        // Class WXSharedAPIByXposed = XposedHelpers.findClass("com.qtz.game.jltx.wxapi.WXSharedAPI",classLoader);

        // public static void createAPI(Context context)

        XposedHelpers.findAndHookMethod(WXSharedAPI, "createAPI", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("before hook createAPI");
                Object[] args = param.args;
                Context context = (Context) args[0];
                XposedBridge.log("context.getPackageResourcePath()->"+context.getPackageResourcePath());
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log("after hook createAPI");
            }
        });

        // private static String buildTransaction(String str)

        XposedHelpers.findAndHookMethod(WXSharedAPI, "buildTransaction", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("before hook buildTransaction");
                Object[] args = param.args;
                String str = (String) args[0];
                XposedBridge.log(str);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log("after hook buildTransaction");
            }
        });
        // com.qtz.game.utils.sdk
        // public void notifyAndUploadPayInfo(boolean z)
        Class QtzSdk = XposedHelpers.findClass("com.qtz.game.utils.sdk.QtzSdk",classLoader);
        XposedHelpers.findAndHookMethod(QtzSdk, "notifyAndUploadPayInfo", boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("before hook notifyAndUploadPayInfo");
                Object[] args = param.args;
                Boolean isSuccess = (Boolean) args[0];
                XposedBridge.log("alipay isSuccess: "+isSuccess);
                args[0] = true;
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log("after hook notifyAndUploadPayInfo");
                Object[] args = param.args;
                Boolean isSuccess = (Boolean) args[0];
                XposedBridge.log("alipay isSuccess: "+isSuccess);
            }
        });

        // com.qtz.game.utils.sdk;
        // public PayInfo2Upload(String str, String str2, String str3, String str4, String str5, int i, float f)
        Class PayInfo2Upload = XposedHelpers.findClass("com.qtz.game.utils.sdk.PayInfo2Upload",classLoader);
        Class PayInfo2UploadByLoad = classLoader.loadClass("com.qtz.game.utils.sdk.PayInfo2Upload");

        XposedHelpers.findAndHookConstructor(PayInfo2UploadByLoad, String.class, String.class,
                String.class, String.class, String.class, int.class, float.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Object[] args = param.args;
                        String orderId = (String) args[0];
                        String productId = (String) args[1];
                        String productName = (String) args[2];
                        String paymentChannel= (String) args[3];
                        String currencyType = (String) args[4];
                        int productNum = (int) args[5];
                        float cash = (float) args[6];
                        XposedBridge.log( "{orderId=" + orderId + ",productId=" + productId + ",productName=" + productName + ",paymentChannel=" + paymentChannel + ",currencyType=" + currencyType + ",productNum=" + productNum + ",cash=" + cash);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });

        Class Tracking = XposedHelpers.findClass("com.reyun.tracking.sdk.Tracking",classLoader);
        // public static void setOrder(String str, String str2, float f)public static void setOrder(String str, String str2, float f)
        XposedHelpers.findAndHookMethod(Tracking, "setOrder", String.class, String.class,
                float.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Object[] args = param.args;
                        String transactionId = (String) args[0];
                        String currencyType = (String) args[1];
                        float currencyAmount = (float) args[2];
                        XposedBridge.log("transactionId: "+transactionId+",currentType: "+currencyType+",currentAmount: "+currencyAmount);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
    }
}
