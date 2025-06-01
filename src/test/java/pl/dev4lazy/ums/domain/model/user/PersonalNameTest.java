package pl.dev4lazy.ums.domain.model.user;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PersonalNameTest {

    @DataProvider(name = "validNames")
    public Object[][] validNames() {
        return new Object[][] {
                {"Jan", "Kowalski"},
                {"Anna", "Nowak"},
                {"Maria", "Wiśniewska"}
        };
    }

    @Test(dataProvider = "validNames")
    public void testValidPersonalNames(String firstName, String lastName) {
        PersonalName name = new PersonalName(firstName, lastName);
        assertEquals(name.firstName(), firstName);
        assertEquals(name.lastName(), lastName);
    }

    @DataProvider(name = "invalidNames")
    public Object[][] invalidNames() {
        return new Object[][] {
                {null, "Kowalski"},
                {"", "Nowak"},
                {"   ", "Wiśniewska"},
                {"Jan", null},
                {"Anna", ""},
                {"Maria", "   "},
                {null, null},
                {"", ""},
                {"   ", "   "}
        };
    }

    @Test(dataProvider = "invalidNames", expectedExceptions = IllegalArgumentException.class)
    public void testInvalidPersonalNames(String firstName, String lastName) {
        new PersonalName(firstName, lastName);
    }
}
