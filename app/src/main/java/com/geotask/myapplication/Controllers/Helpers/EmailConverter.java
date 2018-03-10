package com.geotask.myapplication.Controllers.Helpers;

public class EmailConverter {
    public static String convertEmailForElasticSearch(String email) {
        String convertedEmail = "";
        for (int i = 0; i < email.length(); i++) {
            int character = (int) email.charAt(i);
            convertedEmail += Integer.toString(character) + "c";
        }
        return convertedEmail;
    }

    public static String revertEmailFromElasticSearch(String convertedEmail) {
        String email = "";
        for (String character : convertedEmail.split("c")) {
            int intCharacter = Integer.valueOf(character);
            email += (char) intCharacter;
        }
        return email;
    }
}