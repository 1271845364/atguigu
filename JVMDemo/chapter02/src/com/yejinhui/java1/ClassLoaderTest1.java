package com.yejinhui.java1;

import sun.misc.Launcher;
import sun.security.ec.CurveDB;

import java.net.URL;
import java.security.Provider;

/**
 * @author ye.jinhui
 * @description
 * @program JVMDemo
 * @create 2020/1/31 12:44
 */
public class ClassLoaderTest1 {

    public static void main(String[] args) {
        System.out.println("*******************启动类加载器*******************");
        //获取BootstrapClassLoader能够加载的api路径
        URL[] urLs = Launcher.getBootstrapClassPath().getURLs();
        for (URL urL : urLs) {
            System.out.println(urL.toExternalForm());
        }

        //从上面的路径中随意选择一个类，来看看他的类加载器是什么
        ClassLoader classLoader = Provider.class.getClassLoader();
        System.out.println(classLoader);//null表明类加载器是引导类加载器

        //从上面的路径中随意选择一个类，来看看他的类加载器是什么
        System.out.println("******扩展类加载器*******");
        String extDirs = System.getProperty("java.ext.dirs");
        for(String path:extDirs.split(";")) {
            System.out.println(path);
        }

        //从上面的路径中随意选择一个类，来看看他的类加载器是什么：扩展类加载器
        ClassLoader classLoader1 = CurveDB.class.getClassLoader();
        System.out.println(classLoader1);//sun.misc.Launcher$ExtClassLoader@d7b1517
    }
}
