package org.getOnsClass;

import org.apache.commons.io.IOUtils;
import org.getOnsClass.visitor.AllClassVisitor;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {
    public static int numClass = 0;
    public static void main(String[] args) throws IOException {
        String jarDirPath = "D:\\javaTocms\\fineReportClass\\src\\main\\resources\\scanJar";
        File dirPath = new File(jarDirPath);
        for (File file : dirPath.listFiles()) {
            System.out.println("[+] Scanning Jar , Path : "+file.getAbsolutePath());
            if (file.isFile()){
                JarFile jarFile = new JarFile(file);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()){
                    JarEntry jarEntry = entries.nextElement();
                    if (!jarEntry.isDirectory() && jarEntry.getName().endsWith(".class")){
                        InputStream inputStream = jarFile.getInputStream(jarEntry);
                        byte[] byteArray = IOUtils.toByteArray(inputStream);
                        getControllerClass(byteArray);
                    }
                }
            }
        }
        System.out.println("[+] not need auth class num : "+numClass);


    }

    public static void getControllerClass(byte[] data){
        ClassReader classReader = new ClassReader(data);
        AllClassVisitor allClassVisitor = new AllClassVisitor(data);
        classReader.accept(allClassVisitor,ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

    }
}