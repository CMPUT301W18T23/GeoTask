package com.geotask.myapplication;
import java.util.UUID;

//Taken from https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
public class RandomStringUtility {

    public static void main(String[] args) {
        System.out.println(generateString());
    }

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return "uuid = " + uuid;
    }
}
