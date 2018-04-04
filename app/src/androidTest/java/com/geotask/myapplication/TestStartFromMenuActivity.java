package com.geotask.myapplication;

import android.content.Intent;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.geotask.myapplication.AbstractGeoTaskActivity.setCurrentUser;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertTrue;

//https://developer.android.com/training/testing/espresso/recipes.html
@RunWith(AndroidJUnit4.class)
public class TestStartFromMenuActivity  implements AsyncCallBackManager {
    private GTData data = null;
    private List<? extends GTData> searchResult = null;
    @BeforeClass
    public static void oneTimeSetUp() {
        MasterController.verifySettings();
        MasterController.setTestSettings(TestServerAddress.getTestAddress());
        User user = new User("NewUser", "User@gmail.com", "123456789");
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                = new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        setCurrentUser(user);


        try {
            MasterController.deleteIndex();
            MasterController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loginCheck() {
        loginCheck();
        String email = "User@gmail.com";

            SuperBooleanBuilder builder = new SuperBooleanBuilder();
            builder.put("email", email);

            MasterController.AsyncSearch asyncSearch =
                    new MasterController.AsyncSearch(this);
            asyncSearch.execute(new AsyncArgumentWrapper(builder, User.class));

            List<User> result;
            try {
                result = (List<User>) asyncSearch.get();

                /*
                    Setting the globals
                 */
                setCurrentUser(result.get(0));

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

    }
    @Rule
    public ActivityTestRule<MenuActivity> menuActivityRule = new ActivityTestRule<>(MenuActivity.class);

    @Test
    public void testOnClickItemInListShouldOpenTaskDetail() {
        assertTrue(menuActivityRule.getActivity() != null);

        onData(instanceOf(Task.class))
                .atPosition(0)
                .perform(click());
        //onView(withId(R.id.editTaskButton)).check(matches(withText("EditTask")));
    }

    @Test
    public void testAddNewTaskFromMenuShouldAddTaskToList() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());

        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_requester));

        onView(withId(R.id.fab))
                .perform(click());
    }

    @Test
    public void testDeleteTaskFromListShouldRemoveIt() {

    }

    @Test
    public void testChangeToRequesterModeShouldSeeOnlyYourRequestedTasks() {

    }

    @Test
    public void testChangeToProviderModeShouldSeeOnlyYourProvicedTasks() {

    }

    @Test
    public void testChangeToAllShouldShowAllTask() {

    }


    @Test
    public void testLogOut() {

    }

    @Test
    public void testLogInThenLogOutThenLogInThenBackButtonShouldNotGoBackToFirstLogin() {

    }


    @Override
    public void onPostExecute(GTData data) {
        this.data = data;
    }

    @Override
    public void onPostExecute(List<? extends GTData> dataList) {
        this.searchResult = dataList;
    }
}
