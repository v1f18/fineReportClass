package org.getOnsClass.visitor;

import org.apache.commons.io.FileUtils;
import org.getOnsClass.util.Annotation;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;


public class AllClassVisitor extends ClassVisitor {
    private byte[] classData;
    private boolean isControllerClass = false;
    private boolean isRequestMappingClass = false;
    private boolean isRestController = false;
    private String className;
    private boolean classNeedAuth = true;
    private boolean methodNeedAuth = true;


    public AllClassVisitor(byte[] data) {
        super(Opcodes.ASM6);
        classData = data;
    }
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }
    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (descriptor.equals(Annotation.controller)) {
            isControllerClass = true;
        } else if (descriptor.equals(Annotation.requestMapping)) {
            isRequestMappingClass = true;
        } else if (descriptor.equals(Annotation.restController)) {
            isRestController = true;
        } else if (descriptor.equals(Annotation.loginStatusChecker)) {
            return new AnnotationVisitor(Opcodes.ASM6) {
                @Override
                public void visit(String name, Object value) {
                    if (name.equals("required") && value instanceof Boolean && value.toString() == "false") {
                        classNeedAuth = false;
                    }
                    super.visit(name, value);
                }
            };
        }
        return super.visitAnnotation(descriptor, visible);
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

        return new AllMethodAdapter(className, name);
    }
    @Override
    public void visitEnd() {
        if (isControllerClass || isRequestMappingClass || isRestController) {
            if (!classNeedAuth || !methodNeedAuth) {
                String fileName = "your save path" + className.substring(className.lastIndexOf("/") + 1) + ".class";
                File file = new File(fileName);
                try {
                    FileUtils.writeByteArrayToFile(file, classData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String fileName = "your save path" + className.substring(className.lastIndexOf("/") + 1) + ".class";
                File file = new File(fileName);
                try {
                    FileUtils.writeByteArrayToFile(file, classData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        }
    }
    public class AllMethodAdapter extends MethodVisitor {
        private String methodName;

        public AllMethodAdapter(String name, String method) {
            super(Opcodes.ASM6);
            className = name;
            methodName = method;
        }
        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (descriptor.equals(Annotation.loginStatusChecker)) {
                return new AnnotationVisitor(Opcodes.ASM6) {
                    @Override
                    public void visit(String name, Object value) {
                        if (name.equals("required") && value instanceof Boolean && value.toString() == "false") {
                            methodNeedAuth = false;
                        }
                        super.visit(name, value);
                    }
                };
            } else if (descriptor.equals(Annotation.templateAuth)) {
                return new AnnotationVisitor(Opcodes.ASM6) {
                    @Override
                    public void visitEnum(String name, String descriptor, String value) {
                        if (name.equals("product") && descriptor.equals("Lcom/fr/decision/webservice/bean/template/TemplateProductType;") && value.equals("FINE_REPORT")) {
                            methodNeedAuth = false;
                        }
                        super.visitEnum(name, descriptor, value);
                    }
                };
            }
            return super.visitAnnotation(descriptor, visible);
        }
    }
}
