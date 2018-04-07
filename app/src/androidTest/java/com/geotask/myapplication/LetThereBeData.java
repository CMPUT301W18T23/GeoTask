package com.geotask.myapplication;

import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class LetThereBeData {

    @Test
    public void data() {
        ElasticsearchController controller = new ElasticsearchController();
        controller.verifySettings();

        try {
            controller.deleteIndex();
            controller.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }

        User michael = new User("Michael", "mtang@ualberta.ca", "5555555");

        Task task;
        String temp;
        for(int i = 0; i< 100; i++){
            for(int j = 0; j< 5; j++){
                temp = "test" + i + "block" + j;
                task = new Task(temp, temp, temp);
                task.setRequesterID(michael.getObjectID());
                try {
                    controller.createNewDocument(task);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
