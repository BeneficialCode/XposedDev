package com.example.xposeddev;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookActiveInvoke implements IXposedHookLoadPackage {
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.qtz.game.jltx"))
            return;

        XposedBridge.log("Active Invoke hook->app packageName: "+lpparam.packageName);
        ClassLoader classLoader = lpparam.classLoader;
        // public static int getDBLevelMeter()
        Class QTZVoice = classLoader.loadClass("com.qtz.voice.QTZVoice");
        int level = (int) XposedHelpers.callStaticMethod(QTZVoice,"getDBLevelMeter");
        XposedBridge.log("Level: "+level);

        /*Object StuObjByXposed = XposedHelpers.newInstance(StuClass, "StuObjByXposed.newInstance", "500");
        String result1 = (String) XposedHelpers.callMethod(StuObjByXposed, "publicfunc", "publicfunc is called by XposedHelpers.callMethod", 125);
        XposedBridge.log("publicfunc XposedHelpers.callMethod result->" + result1);*/
    }
}
