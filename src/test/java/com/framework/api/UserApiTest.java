package com.framework.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserApiTest {

    @Test(groups = "integration")
    public void testUserApiHealth() {
        // Simple integration check: Is the backend alive?
        // Using a public API for demonstration
        Response response = RestAssured.get("https://jsonplaceholder.typicode.com/users/1");

        System.out.println("API Response: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200, "API should return 200 OK");
        Assert.assertTrue(response.getBody().asString().contains("Leanne Graham"), "Body should contain user data");
    }
}
