package com.example.xposeddev;

import java.security.cert.X509Certificate;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookSo implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.qtz.game.jltx"))
            return;


        // It uses the class loader of the caller, which is usually the app, but would be Xposed as soon as you hook it. Please hook Runtime.loadLibrary() instead.
        // loadLibrary0(VMStack.getCallingClassLoader(), libname); Android 9
        XposedHelpers.findAndHookMethod("java.lang.Runtime", lpparam.classLoader,
                "loadLibrary0", ClassLoader.class,String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        ClassLoader fromClass = (ClassLoader) param.args[0];
                        String soName = (String) param.args[1];
                        XposedBridge.log("afterHookedMethod System.loadLibrary0("+soName+")");
                        // 不可hook多次，so可能会加载多次
                        // https://www.jianshu.com/p/93828be3ff58
                        if(soName.contains("game")){
                            // 拷贝过去
                            // cp /data/app/com.example.hellondk-mE2vo3RWkDDrlWW-T8gywA\=\=/lib/arm64/libhook-lib.so /data/app/com.qtz.game.jltx-NJFXwyKPB3COyuej66PaOQ\=\=/lib/arm64/
                            XposedBridge.log("find "+soName);
                            Object[] newArgs = new Object[]{fromClass, "hook-lib"};
                            XposedBridge.invokeOriginalMethod(param.method, param.thisObject, newArgs);
                        }
                    }
                });
    }
}
