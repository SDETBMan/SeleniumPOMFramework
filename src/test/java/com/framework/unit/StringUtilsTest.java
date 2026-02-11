package com.framework.unit;

import com.framework.utils.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * StringUtilsTest: Unit tests for utility methods.
 * Demonstrates high-speed, logic-focused validation independent of the browser.
 */
public class StringUtilsTest {

    @Test(groups = "unit")
    public void testEmailGenerationFormat() {
        String email = StringUtils.generateRandomEmail();
        System.out.println("[UNIT-TEST] Validating generated email: " + email);

        Assert.assertNotNull(email, "Generated email should not be null");
        Assert.assertTrue(email.contains("@"), "Email missing @ symbol");
        Assert.assertTrue(email.endsWith(".com"), "Email missing .com suffix");
        Assert.assertTrue(email.startsWith("user"), "Email missing required prefix");
    }

    @Test(groups = "unit")
    public void testEmailValidationLogic() {
        System.out.println("[UNIT-TEST] Testing boundary cases for email validation");

        Assert.assertTrue(StringUtils.isValidEmail("test@example.com"));
        Assert.assertFalse(StringUtils.isValidEmail(""));
        Assert.assertFalse(StringUtils.isValidEmail(null));
        Assert.assertFalse(StringUtils.isValidEmail("missing_at_sign.com"));
    }
}
