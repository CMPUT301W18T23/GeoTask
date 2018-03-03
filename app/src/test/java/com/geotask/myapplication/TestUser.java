package com.geotask.myapplication;


import com.geotask.myapplication.DataClasses.User;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestUser {

    @Test
    public void testUser(){
        testAddUser();
        testEditUser();
    }


    public void testAddUser(){
        String name = "kehan";
        String email = "kehan1@ualberta.ca";
        String phone = "7808858151";


        User user = new User(name, email, phone);
        assertEquals(name,user.getName());
        assertEquals(phone,user.getPhonenum());
        assertEquals(email, user.getEmail());

    }
    public void testEditUser(){
        String name = "kehan";
        String email = "kehan1@ualberta.ca";
        String phone = "7808858151";


        User user = new User(name, email, phone);

        String newEmail = "kehan@ualberta.ca";
        user.setEmail(newEmail);
        assertEquals(newEmail,user.getEmail());

        String newPhone = "7808858152";
        user.setPhonenum(newPhone);
        assertEquals(newPhone, user.getPhonenum());
    }
}
