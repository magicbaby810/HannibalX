package com.hannibal.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.hannibal.gradle.utils.DataHelper
import com.hannibal.gradle.utils.Log
import com.hannibal.gradle.utils.ModifyFiles
import com.hannibal.gradle.utils.Util
import org.gradle.api.Plugin
import org.gradle.api.Project

class HannibalPluginImpl implements Plugin<Project> {
    @Override
    void apply(Project project) {
        Log.info":applied Hannibal success!"
        project.extensions.create('hannibal', HannibalParams)
        Util.setProject(project)
        try {
            if (Class.forName("com.android.build.gradle.BaseExtension")) {
                BaseExtension android = project.extensions.getByType(BaseExtension)
                if (android instanceof LibraryExtension) {
                    DataHelper.ext.projectType = DataHelper.TYPE_LIB;
                } else if (android instanceof AppExtension) {
                    DataHelper.ext.projectType = DataHelper.TYPE_APP;
                } else {
                    DataHelper.ext.projectType = -1
                }

                if (DataHelper.ext.projectType != -1) {
                    registerTransform(android)
                }
            }
        } catch (Exception e) {
            DataHelper.ext.projectType = -1
        }
        initDir(project);
        project.afterEvaluate {
            Log.setQuiet(!project.hannibal.printLog);
            Log.setShowHelp(project.hannibal.showHelp);
            Log.logHelp();
            Map<String, Map<String, Object>> taskMap = project.hannibal.modifyTasks;
            if (taskMap != null && taskMap.size() > 0) {
                generateTasks(project, taskMap);
            }
            if (project.hannibal.watchTimeConsume) {
                Log.info "watchTimeConsume enabled"
                project.gradle.addListener(new TimeListener())
            } else {
                Log.info "watchTimeConsume disabled"
            }
        }
    }

    def static registerTransform(BaseExtension android) {
        InjectTransform transform = new InjectTransform()
        android.registerTransform(transform)
    }

    static void initDir(Project project) {
        if (!project.buildDir.exists()) {
            project.buildDir.mkdirs()
        }
        File hannibalDir = new File(project.buildDir, "Hannibal")
        if (!hannibalDir.exists()) {
            hannibalDir.mkdir()
        }
        File tempDir = new File(hannibalDir, "temp")
        if (!tempDir.exists()) {
            tempDir.mkdir()
        }
        DataHelper.ext.hannibalDir = hannibalDir
        DataHelper.ext.hannibalTempDir = tempDir
    }

    def static generateTasks(Project project, Map<String, Map<String, Object>> taskMap) {
        project.task("hannibalModifyFiles").doLast {
            ModifyFiles.modify(taskMap)
        }
    }
}
