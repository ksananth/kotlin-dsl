package myapp.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class AnalyticsTask extends DefaultTask {
    @TaskAction
    public void run() {
        System.out.println("AnalyticsTask " + getPath() + "!");

        try {
            AnalyticsCreator analyticsCreator = new AnalyticsCreator();
            analyticsCreator.create();
            analyticsCreator.getJson(getProject().getProjectDir().getAbsolutePath()+"/buildSrc/src/main/resources/latest/report_1.0.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
