package com.example.xposeddev;

import dalvik.system.DexClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

// public DexClassLoader(String dexPath, String optimizedDirectory,
//            String librarySearchPath, ClassLoader parent)
public class HookDexClassLoader implements IXposedHookLoadPackage {
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.qtz.game.jltx"))
            return;
        XposedHelpers.findAndHookConstructor(DexClassLoader.class,
                String.class, String.class, String.class, ClassLoader.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Object[] args = param.args;
                        String dexPath = (String) args[0];
                        String optimizedDirectory = (String) args[1];
                        String librarySearchPath = (String)args[2];
                        XposedBridge.log("DexClassLoader before hook: "+dexPath+"---"+optimizedDirectory+"---"+librarySearchPath);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        DexClassLoader classLoader = (DexClassLoader) param.thisObject;
                        XposedBridge.log("DexClassLoader after hook: "+classLoader);
                    }
                });
    }
}
