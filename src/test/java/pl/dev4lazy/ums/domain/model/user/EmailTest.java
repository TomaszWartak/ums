package pl.dev4lazy.ums.domain.model.user;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class EmailTest {

    @DataProvider(name = "validEmails")
    public Object[][] validEmails() {
        return new Object[][] {
                {"test@example.com"},
                {"user.name@domain.co"},
                {"email123@sub.domain.org"}
        };
    }

    @Test(dataProvider = "validEmails")
    public void testValidEmails(String emailStr) {
        Email email = new Email(emailStr);
        assertEquals(email.getValue(), emailStr);
    }

    @DataProvider(name = "invalidEmails")
    public Object[][] invalidEmails() {
        return new Object[][] {
                {null},
                {""},
                {"plainaddress"},
                {"@missingusername.com"},
                {"missingatsign.com"},
                {"missingdomain@.com"},
                {"missingdot@domaincom"},
                {"missing@dotcom."}
        };
    }

    @Test(dataProvider = "invalidEmails", expectedExceptions = IllegalArgumentException.class)
    public void testInvalidEmails(String emailStr) {
        new Email(emailStr);
    }
}
