package pl.dev4lazy.ums.domain.model.user;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class UserIdTest {

    @Test
    public void whenCreatingUserIdWithValidValue_thenSuccess() {
        UserId id = new UserId(5L);
        assertEquals(id.getValue().longValue(), 5L);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "UserId nie może być null")
    public void whenCreatingUserIdWithNull_thenThrowsException() {
        new UserId(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "UserId musi być dodatnie: -1")
    public void whenCreatingUserIdWithNegativeValue_thenThrowsException() {
        new UserId(-1L);
    }
}