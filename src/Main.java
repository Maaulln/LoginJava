import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Users user = new Users("testUser", "testPass");
    Auth auth = new Auth(user);

    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter username: ");
    String username = scanner.nextLine();
    System.out.print("Enter password: ");
    String password = scanner.nextLine();

    try {
      auth.login(username, password);
    } catch (InvalidUserException | InvalidPasswordException e) {
      System.out.println("Login failed: " + e.getMessage());
    }

    scanner.close();
  }
}
