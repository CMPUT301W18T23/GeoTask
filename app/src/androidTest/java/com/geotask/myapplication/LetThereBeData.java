package com.geotask.myapplication;

import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.User;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LetThereBeData {

    @Test
    public void data() {
        ElasticsearchController controller = new ElasticsearchController();

        User michael = new User("Michael", "mtang", "5555555");
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(michael);
    }
}
