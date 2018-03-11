package com.geotask.myapplication;

import com.geotask.myapplication.Controllers.Helpers.EmailConverter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestEmailConverter {

    @Test
    public void testEmailConversion(){
        String email = "kyleg@email.com";
        String target = "107c121c108c101c103c64c101c109c97c105c108c46c99c111c109c";
        String convertedEmail = EmailConverter.convertEmailForElasticSearch(email);
        String revertedEmail = EmailConverter.revertEmailFromElasticSearch(convertedEmail);
        assert(convertedEmail.compareTo(target) == 0);
        assert(email.compareTo(revertedEmail) == 0);
    }
}
