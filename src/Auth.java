import java.util.Objects;

public class Auth {
  private Users userRegistered;

  public Auth(Users userRegistered) {
    this.userRegistered = Objects.requireNonNull(userRegistered, "Registered user must not be null");
  }

  public void login(String userName, String password) throws InvalidUserException, InvalidPasswordException {
    if (!Objects.equals(userRegistered.getUserName(), userName)) {
      throw new InvalidUserException("Invalid username: " + userName);
    }
    if (!Objects.equals(userRegistered.getPassword(), password)) {
      throw new InvalidPasswordException("Invalid password for user: " + userName);
    }
    System.out.println("Login successful for user: " + userName);
  }
}