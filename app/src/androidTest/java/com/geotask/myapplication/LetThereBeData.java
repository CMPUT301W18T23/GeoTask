package com.geotask.myapplication;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class LetThereBeData {

    @Test
    public void data() throws IOException {
        ElasticsearchController controller = new ElasticsearchController();
        controller.verifySettings();
        LocalDataBase dataBase = LocalDataBase.getDatabase(InstrumentationRegistry.getTargetContext());
        dataBase.userDAO().delete();
        dataBase.bidDAO().delete();
        dataBase.taskDAO().delete();

        try {
            controller.deleteIndex();
            controller.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }

        User michael = new User("Michael", "mtang@ualberta.ca", "5555555");
        User kyle = new User("Kyle", "k@k", "55552355");
        User JamesJ = new User("JamesJ", "1@1.1", "2353490423");
        User Kehan = new User("kehan", "kehan1@u", "2348793258");
        Task task;
        controller.createNewDocument(michael);
        controller.createNewDocument(kyle);
        controller.createNewDocument(JamesJ);
        controller.createNewDocument(Kehan);
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
