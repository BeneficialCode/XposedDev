package com.example.xposeddev;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import android.content.Context;
import android.util.AttributeSet;

import java.lang.reflect.Field;


public class HookTest implements IXposedHookLoadPackage {
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Loaded app: " + lpparam.packageName);
        if (!lpparam.packageName.equals("com.qtz.game.jltx"))
            return;
        // hook构造函数
        // PathClassLoader
        ClassLoader classLoader = lpparam.classLoader;
        // private QTZVoice()
        Class QTZVoiceClass = classLoader.loadClass("com.qtz.voice.QTZVoice");
        XposedHelpers.findAndHookConstructor(QTZVoiceClass, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("private QTZVoice() before hook method");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("private QTZVoice() after hook method");
            }
        });
        // 修改属性
        Field languageField = QTZVoiceClass.getDeclaredField("language");
        languageField.setAccessible(true);
        languageField.set(null,"Chinese");

        String language = (String) languageField.get(null);
        XposedBridge.log("Reflection languageField->"+language);

        XposedHelpers.setStaticObjectField(QTZVoiceClass,"language","English");

        String languageName = (String) XposedHelpers.getStaticObjectField(QTZVoiceClass,"language");
        XposedBridge.log("XposedHelpers: languageField->"+languageName);
        // public VideoView(Context context)
        /*Class VideoViewClass = classLoader.loadClass("com.qtz.game.utils.video.VideoViewClass");
        XposedHelpers.findAndHookConstructor(VideoViewClass, Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("public VideoView(Context context) before hook method");
                java.lang.Object[] args = param.args;
                Context context = (Context)args[0];
                XposedBridge.log("PackageResourcePath "+context.getPackageResourcePath());
            }
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });*/
        // public VideoView(Context context, AttributeSet attributeSet)
        /*XposedHelpers.findAndHookConstructor(VideoViewClass, Context.class,AttributeSet.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("public VideoView(Context context, AttributeSet attributeSet) before hook method");
                java.lang.Object[] args = param.args;
                Context context = (Context)args[0];
                AttributeSet attributeSet = (AttributeSet)args[1];
                XposedBridge.log("Package Resource Path: "+context.getPackageResourcePath());
                XposedBridge.log("Class Attribute: "+attributeSet.getClassAttribute());
            }
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                java.lang.Object[] args = param.args;
            }
        });*/
        // public VideoView(Context context, AttributeSet attributeSet, int i)
        /*XposedHelpers.findAndHookConstructor(VideoViewClass, Context.class,AttributeSet.class,int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("public VideoView(Context context, AttributeSet attributeSet, int i) before hook method");
                java.lang.Object[] args = param.args;
                Context context = (Context)args[0];
                AttributeSet attributeSet = (AttributeSet)args[1];
                int i = (int)args[2];
                XposedBridge.log("Package Resource Path: "+context.getPackageResourcePath());
                XposedBridge.log("Class Attribute: "+attributeSet.getClassAttribute());
            }
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                java.lang.Object[] args = param.args;
            }
        });*/
    }
}
