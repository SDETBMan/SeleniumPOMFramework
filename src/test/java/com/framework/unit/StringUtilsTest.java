package com.framework.unit;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.Random;

public class StringUtilsTest {

    // This mimics a method you might have in a 'RandomUtils' helper class
    public String generateRandomEmail() {
        return "user" + new Random().nextInt(1000) + "@test.com";
    }

    @Test(groups = "unit")
    public void testEmailGenerationFormat() {
        String email = generateRandomEmail();

        System.out.println("Testing email: " + email);

        // Assertions: Fast, logic-only checks
        Assert.assertTrue(email.contains("@"), "Email should contain @ symbol");
        Assert.assertTrue(email.endsWith(".com"), "Email should end with .com");
        Assert.assertTrue(email.startsWith("user"), "Email should start with 'user'");
    }
}
