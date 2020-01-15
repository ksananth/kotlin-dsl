package com.dsl.myapplication;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class AnalyticsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().create("analyticsPlugin", AnalyticsTask.class);
    }
}
