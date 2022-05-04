package com.example.xposeddev;

import android.app.Application;
import android.os.Bundle;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookActivityOnCreate implements IXposedHookLoadPackage {
    public static Field getClassField(ClassLoader classLoader, String className, String fieldName){
        try{
            Class clazz = classLoader.loadClass(className);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getClassFieldObject(ClassLoader classLoader,String className,Object obj,String fieldName){
        try{
            Class clazz = classLoader.loadClass(className);
            Field field = clazz.getDeclaredField(fieldName);
            Object result = null;
            result=field.get(obj);
            return result;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object invokeStaticMethod(String className,String methodName,Class[] pareType,Object[] pareValues){
        try{
            Class clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName,pareType);
            return method.invoke(null,pareValues);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getFieldObject(String className,Object obj,String fieldName){
        try{
            Class clazz = Class.forName(className);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ClassLoader getClassLoader() {
        ClassLoader classLoader=null;
        Object currentActivityThread = invokeStaticMethod("android.app.ActivityThread","currentActivityThread",
                new Class[]{},new Object[]{});
        Object mBoundApplication = getFieldObject("android.app.ActivityThread",currentActivityThread,"mBoundApplication");
        Application mInitialApplication = (Application) getFieldObject("android.app.ActivityThread",
                currentActivityThread,"mInitialApplication");
        Object loadedApkInfo = getFieldObject("android.app.ActivityThread$AppBindData",
                mBoundApplication,"info");
        Application mApplication = (Application) getFieldObject("android.app.LoadedApk",loadedApkInfo,"mApplication");
        classLoader = mApplication.getClassLoader();
        return classLoader;
    }

    public void GetClassLoaderClassList(ClassLoader classLoader){
        XposedBridge.log("start deal with classloader:"+classLoader);
        Object pathList = XposedHelpers.getObjectField(classLoader,"pathList");

        Object[] dexElements = (Object[]) XposedHelpers.getObjectField(pathList,"dexElements");
        for(Object dexElement:dexElements){
            Object dexFile = XposedHelpers.getObjectField(dexElement,"dexFile");
            Object mCookie = XposedHelpers.getObjectField(dexFile,"mCookie");
            Class clazz = XposedHelpers.findClass("dalvik.system.DexFile",classLoader);
            String[] classNameList = (String[]) XposedHelpers.callStaticMethod(clazz,"getClassNameList",mCookie);
            for(String className:classNameList){
                //XposedBridge.log(dexFile+"---"+className);
            }
        }
        XposedBridge.log("end deal with class loader: "+classLoader);
    }

    public void GetClassLoaderNativeLibPathList(ClassLoader classLoader){
        Object pathList = XposedHelpers.getObjectField(classLoader,"pathList");
        Object[] nativeLibraryPathElements = (Object[]) XposedHelpers.getObjectField(pathList,"nativeLibraryPathElements");
        for(Object nativeLibraryPathElement:nativeLibraryPathElements){
            File path = (File) XposedHelpers.getObjectField(nativeLibraryPathElement,"path");
            XposedBridge.log("path-->"+path);
        }
    }


    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.qtz.game.jltx"))
            return;

        ClassLoader classLoader = lpparam.classLoader;

        GetClassLoaderClassList(classLoader);
        ClassLoader parent = classLoader.getParent();
        while(parent!=null){
            XposedBridge.log("parent->"+parent);
            if(parent.toString().contains("BootClassLoader")){
                XposedBridge.log("find BootClassLoader");
            }
            else{
                GetClassLoaderClassList(parent);
            }
            parent=parent.getParent();
        }

        XposedBridge.log("hook "+lpparam.packageName);
        // com.qtz.game.main.Logo
        Class clazz = XposedHelpers.findClass("com.qtz.game.main.Logo",lpparam.classLoader);
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method:methods){
            XposedBridge.log("com.qtz.game.Logo->"+method);
        }


        XposedHelpers.findAndHookMethod("com.qtz.game.main.Logo", lpparam.classLoader, "onCreate",
                Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("com.qtz.game.main.Logo->onCreate before hook");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log("com.qtz.game.main.Logo->onCreate after hook");
                ClassLoader classLoader = getClassLoader();
                GetClassLoaderClassList(classLoader);

                GetClassLoaderNativeLibPathList(classLoader);
            }
        });
    }
}