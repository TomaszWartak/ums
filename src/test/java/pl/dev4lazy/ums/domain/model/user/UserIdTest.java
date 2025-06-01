package pl.dev4lazy.ums.domain.model.user;

import org.testng.annotations.Test;
import pl.dev4lazy.ums.utils.Messages;

import static org.testng.Assert.*;

public class UserIdTest {

    @Test
    public void whenCreatingUserIdWithValidValue_thenSuccess() {
        UserId id = new UserId(5L);
        assertEquals(id.getValue().longValue(), 5L);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = Messages.USER_ID_NULL )
    public void whenCreatingUserIdWithNull_thenThrowsException() {
        new UserId(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = Messages.USER_ID_NEGATIVE )
    public void whenCreatingUserIdWithNegativeValue_thenThrowsException() {
        new UserId(-1L);
    }
}