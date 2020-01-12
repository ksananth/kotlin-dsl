package myapp.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

public class HelloTask extends DefaultTask {
    @TaskAction
    public void run() {
        System.out.println("Hello from task " + getPath() + "!");
        try {
            new FunCreator().createFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
