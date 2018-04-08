package com.geotask.myapplication;


import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

@RunWith(AndroidJUnit4.class)
public class Demo {

    @Rule
    public ActivityTestRule<MenuActivity> demoMenu = new ActivityTestRule<>(MenuActivity.class, true, false);

    @Test
    public void soFresh() {
        String test = "soFresh";
        User user = new User("KehanWang", "kehan1@ualberta.ca", "7808858151");
        user.setObjectID(test);
        Task task = new Task (test,"Cmput301", "TestStory1 bugs");

        Context context = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(context, MenuActivity.class);
        demoMenu.getActivity().setCurrentUser(user);
        demoMenu.getActivity().setCurrentTask(task);
        demoMenu.launchActivity(intent);

        for (int i = 0; i <= 1000; i++) {
            onData(anything()).inAdapterView(withId(R.id.taskListView)).atPosition(0).perform(click());
            pressBack();
        }
    }
}
