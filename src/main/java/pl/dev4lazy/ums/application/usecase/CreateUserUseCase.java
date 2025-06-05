package pl.dev4lazy.ums.application.usecase;

public interface CreateUserUseCase {
    Long execute(String firstName, String lastName, String email);
}
