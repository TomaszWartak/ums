package pl.dev4lazy.ums.mock;

import pl.dev4lazy.ums.domain.repository.AbstractUserRepositoryTest;
import pl.dev4lazy.ums.domain.repository.UserRepository;

public class UserRepositoryMockTest extends AbstractUserRepositoryTest {

    @Override
    protected UserRepository createRepository() {
        return new UserRepositoryMock();
    }
}
