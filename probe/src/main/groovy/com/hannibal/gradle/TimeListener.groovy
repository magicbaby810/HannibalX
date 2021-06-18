package com.hannibal.gradle

import com.hannibal.gradle.utils.Log
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

import java.util.concurrent.TimeUnit

class TimeListener implements TaskExecutionListener, BuildListener {
    private times = []
    private long startTime
    private long totalTime

    @Override
    void beforeExecute(Task task) {
        Log.learn("beforeExecute")
        startTime = System.nanoTime()
    }

    @Override
    void afterExecute(Task task, TaskState taskState) {
        def ms = TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS)
        times.add([ms, task.path])
        totalTime += ms
        Log.info "${task.path} 花费时间 spend ${ms}ms"
    }

    @Override
    void buildFinished(BuildResult result) {
        println "Task spend time:${totalTime}ms"
        for (time in times) {
            if (time[0] >= 50) {
                printf "%7sms %s\n", time
            }
        }
    }

    @Override
    void buildStarted(Gradle gradle) {
        Log.learn("buildStarted")
    }

    @Override
    void projectsEvaluated(Gradle gradle) {
        Log.learn("projectsEvaluated")
    }

    @Override
    void projectsLoaded(Gradle gradle) {
        Log.learn("projectsLoaded")
    }

    @Override
    void settingsEvaluated(Settings settings) {
        Log.learn("settingsEvaluated")
    }

}