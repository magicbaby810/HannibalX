package com.hannibal.gradle.utils

import com.android.build.gradle.BaseExtension
import com.hannibal.gradle.HannibalParams
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import java.util.regex.Pattern


public class Util {


    private static Project project;

    public static void setProject(Project project) {
        Util.@project = project
    }

    public static Project getProject() {
        return project
    }

    public static BaseExtension getExtension() {
        return project.extensions.getByType(BaseExtension)
    }

    public static HannibalParams getHannibal() {
        return project.hannibal
    }

    public static void initTargetClasses(Map<String, Object> modifyMatchMaps) {
        targetClasses.clear()
        if (modifyMatchMaps != null) {
            def set = modifyMatchMaps.entrySet();
            for (Map.Entry<String, Object> entry : set) {
                def value = entry.getValue()
                if (value) {
                    int type;
                    if (value instanceof Map) {
                        type = typeString2Int(value.get(Const.KEY_CLASSMATCHTYPE));
                    } else {
                        type = getMatchTypeByValue(entry.getKey());
                    }
                    targetClasses.put(entry.getKey(), type)
                }
            }
        }
    }

//    static boolean adjustFlutterBoost(Map<String, Object> modifyMatchMaps) {
//        if (getHannibal().adjustFlutterBoost) {
//            String className = 'com.idlefish.flutterboost.FlutterBoost'
//
//            String insertClassAbsolutePath = className2Path("com.sk.flutterpatch.FlutterPatch")
//
//            def adapter = [
//                    ['methodName': 'createEngine', 'methodDesc': '()Lio/flutter/embedding/engine/FlutterEngine;', 'adapter': {
//                        ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
//                            MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
//                            MethodVisitor adapter = new MethodLogAdapter(methodVisitor) {
//
//
//                                @Override
//                                void visitMethodInsn(int opcode, String owner, String name1, String desc1, boolean itf) {
//                                    super.visitMethodInsn(opcode, owner, name1, desc1, itf)
//                                    Log.info "============== adjust FlutterBoost success ============== $name1"
//                                    if (name1.equals("startInitialization")) {
//                                        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
//                                        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC,
//                                                insertClassAbsolutePath,
//                                                "hook",
//                                                "(Ljava/lang/Object;)V",
//                                                false)
//
//                                        Log.info "============== adjust FlutterBoost success =============="
//                                    }
//                                }
//
//                                @Override
//                                void visitCode() {
//                                    super.visitCode();
//                                }
//                            }
//                            return adapter
//                    }]
//            ]
//
//            modifyMatchMaps.put(className, adapter)
//        } else {
//            adjustFlutter(modifyMatchMaps)
//        }
//    }

    static boolean adjustFlutter(Map<String, Object> modifyMatchMaps) {

        String insertClassAbsolutePath = className2Path("com.sk.flutterpatch.FlutterPatch")

//      ========================================================================


        String useTinkerClassName = 'com.tencent.tinker.lib.tinker.TinkerInstaller'

        def adapter3 = [
                ['methodName': 'install', 'methodDesc': '(Lcom/tencent/tinker/entry/ApplicationLike;Lcom/tencent/tinker/lib/reporter/LoadReporter;Lcom/tencent/tinker/lib/reporter/PatchReporter;Lcom/tencent/tinker/lib/listener/PatchListener;Ljava/lang/Class;Lcom/tencent/tinker/lib/patch/AbstractPatch;)Lcom/tencent/tinker/lib/tinker/Tinker;', 'adapter': {
                    ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                        MethodVisitor adapter = new MethodLogAdapter(methodVisitor) {

                            @Override
                            void visitCode() {
                                super.visitCode();
                                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                                        insertClassAbsolutePath,
                                        "hookIsUseTinker", "()V", false)
                            }

                        }
                        return adapter
                }]
        ]

        modifyMatchMaps.put(useTinkerClassName, adapter3)

//      ========================================================================


        String useSophixClassName = 'com.taobao.sophix.a.e'

        def adapter2 = [
                ['methodName': 'initialize', 'methodDesc': '()V', 'adapter': {
                    ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                        MethodVisitor adapter = new MethodLogAdapter(methodVisitor) {

                            @Override
                            void visitCode() {
                                super.visitCode();
                                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                                        insertClassAbsolutePath,
                                        "hookIsUseSophix", "()V", false)
                            }

                        }
                        return adapter
                }]
        ]

        modifyMatchMaps.put(useSophixClassName, adapter2)

//      ========================================================================


        String sophixClassName = 'com.taobao.sophix.a.c'

        def adapter1 = [
                ['methodName': 'a', 'methodDesc': '(Ljava/lang/String;)V', 'adapter': {
                    ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                        MethodVisitor adapter = new MethodLogAdapter(methodVisitor) {

                            @Override
                            void visitCode() {
                                super.visitCode();
                                methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                                        insertClassAbsolutePath,
                                        "hookSophix", "(Ljava/lang/Object;)V", false);
                            }
                        }
                        return adapter
                }]
        ]

        modifyMatchMaps.put(sophixClassName, adapter1)

//      ========================================================================

        String[] d = project.android.defaultConfig.ndk.getProperty("abiFilters")
        String str = ""
        for (String dd : d) {
            str += (dd + ",")
        }
        println("abiFilters：" + str)

        String className = 'io.flutter.app.FlutterApplication'

        def adapter = [
                ['methodName': 'onCreate', 'methodDesc': null, 'adapter': {
                    ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions ->
                        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
                        MethodVisitor adapter = new MethodLogAdapter(methodVisitor) {

                            @Override
                            void visitInsn(int opcode) {
                                if (opcode == Opcodes.RETURN) {
                                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
                                    methodVisitor.visitLdcInsn(str)
                                    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                                            insertClassAbsolutePath,
                                            "hook",
                                            "(Ljava/lang/Object;Ljava/lang/Object;)V",
                                            false)

                                    Log.info "============== adjust Flutter success =============="
                                }
                                super.visitInsn(opcode)
                            }
                        }
                        return adapter
                }]
        ]

        modifyMatchMaps.put(className, adapter)
    }

    public static boolean regMatch(String pattern, String target) {
        if (isEmpty(pattern) || isEmpty(target)) {
            return false;
        }
        if (pattern.startsWith(Const.REGEX_STARTER)) {
            pattern = pattern.substring(2);
        }
        return Pattern.matches(pattern, target);
    }

    public static int typeString2Int(String type) {
        if (type == null || Const.VALUE_ALL.equals(type)) {
            return Const.MT_FULL;
        } else if (Const.VALUE_REGEX.equals(type)) {
            return Const.MT_REGEX;
        } else if (Const.VALUE_WILDCARD.equals(type)) {
            return Const.MT_WILDCARD;
        } else {
            return Const.MT_FULL;
        }
    }

    public static int getMatchTypeByValue(String value) {
        if (isEmpty(value)) {
            throw new RuntimeException("Key cannot be null");
        } else if (value.startsWith(Const.REGEX_STARTER)) {
            return Const.MT_REGEX;
        } else if (value.contains("*") || value.contains("|")) {
            return Const.MT_WILDCARD;
        } else {
            return Const.MT_FULL;
        }
    }

    public static boolean isPatternMatch(String pattern, String type, String target) {
        if (isEmpty(pattern) || isEmpty(target)) {
            return false;
        }
        int intType;
        if (isEmpty(type)) {
            intType = getMatchTypeByValue(pattern);
        } else {
            intType = typeString2Int(type);
        }
        switch (intType) {
            case Const.MT_FULL:
                if (target.equals(pattern)) {
                    return true;
                }
                break;
            case Const.MT_REGEX:
                if (regMatch(pattern, target)) {
                    return true;
                }
                break;
            case Const.MT_WILDCARD:
                if (wildcardMatchPro(pattern, target)) {
                    return true;
                }
                break;
        }
        return false;
    }

    public static boolean isEmpty(String text) {
        return text == null || text.trim().length() < 1;
    }

    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    public static boolean wildcardMatchPro(String pattern, String target) {
        if (pattern.contains("|")) {
            String[] patterns = pattern.split(Const.WILDCARD_VLINE);
            String part;
            for (int i = 0; i < patterns.length; i++) {
                part = patterns[i];
                if (isNotEmpty(part)) {
                    if (part.startsWith("!")) {
                        part = part.substring(1);
                        if (wildcardMatch(part, target)) {
                            return false;
                        }
                    }
                }
            }
            for (int i = 0; i < patterns.length; i++) {
                part = patterns[i]
                if (isNotEmpty(part)) {
                    if (!part.startsWith("!")) {
                        if (wildcardMatch(part, target)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } else {
            return wildcardMatch(pattern, target);
        }
    }
    /**
     * 星号匹配
     * @param pattern
     * @param target
     * @return
     * new StringBuilder()
     .append(wildcardMatch("com.**.act.*.github.*Activity", "com.jj.act.jj.github.MainActivity")).append(",") //true
     .append(wildcardMatch("*Activity", "com.jj.act.jj.github.MainActivity")).append(",")//true
     .append(wildcardMatch("*Activity", "com.jj.act.jjActivity")).append(",")//true
     .append(wildcardMatch("*Activity*", "com.jj.act.jjActivity")).append(",")//false
     .append(wildcardMatch(".*Activity", "com.Activity")).append(",")//false
     .append(wildcardMatch("com.**.a*t.*.github.*Activity", "com.jj.act.jj.github.MainActivity")).append(",")//true
     .append(wildcardMatch("com.**.act.*.gi*ub.*Act*vity", "com.jj.MainActivity.act")).append(",")//false
     .append(wildcardMatch("com.**.act.*.gi*ub.*Act*vity", "com.jj.act.jj.github.Mactivity")).append(",")//false
     .toString()
     */
    private static boolean wildcardMatch(String pattern, String target) {
        if (isEmpty(pattern) || isEmpty(target)) {
            return false;
        }
        try {
            String[] split = pattern.split(Const.WILDCARD_STAR);
            //如果以分隔符开头和结尾，第一位会为空字符串，最后一位不会为空字符，所以*Activity和*Activity*的分割结果一样
            if (pattern.endsWith("*")) {//因此需要在结尾拼接一个空字符
                List<String> strings = new LinkedList<>(Arrays.asList(split));
                strings.add("");
                split = new String[strings.size()];
                strings.toArray(split);
            }
            for (int i = 0; i < split.length; i++) {
                String part = split[i];
                if (isEmpty(target)) {
                    return false;
                }
                if (i == 0 && isNotEmpty(part)) {
                    if (!target.startsWith(part)) {
                        return false;
                    }
                }
                if (i == split.length - 1 && isNotEmpty(part)) {
                    if (!target.endsWith(part)) {
                        return false;
                    } else {
                        return true;
                    }
                }
                if (part == null || part.trim().length() < 1) {
                    continue;
                }
                int index = target.indexOf(part);
                if (index < 0) {
                    return false;
                }
                int newStart = index + part.length() + 1;
                if (newStart < target.length()) {
                    target = target.substring(newStart);
                } else {
                    target = "";
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String path2Classname(String entryName) {
        entryName.replace("/", ".").replace(".class", "")
    }

    public static String getNameFromPath(String path) {
        path.substring(path.lastIndexOf(File.separator) + 1)
    }

    public static String className2Path(String classname) {
        return classname.replace('.', '/');
    }

    static Map<String, Integer> targetClasses = [:];

    public static boolean isTargetClassesNotEmpty() {
        targetClasses != null && targetClasses.size() > 0
    }

    public static String shouldModifyClass(String className) {
        if (getHannibal().enable) {
            def set = targetClasses.entrySet();
            for (Map.Entry<String, Integer> entry : set) {
                def mt = entry.getValue();
                String key = entry.getKey()
                switch (mt) {
                    case Const.MT_FULL:
                        if (className.equals(key)) {
                            return key;
                        }
                        break;
                    case Const.MT_REGEX:
                        if (regMatch(key, className)) {
                            return key;
                        }
                        break;
                    case Const.MT_WILDCARD:
                        if (wildcardMatchPro(key, className)) {
                            return key;
                        }
                        break;
                }
            }
            return null;
        } else {
            return null;
        }
    }
}