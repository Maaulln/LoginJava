package main.java;
public class Users {
  private String userName;
  private String password;
  private String email;

  public Users(String userName, String password, String email) {
    this.userName = userName;
    this.password = password;
    this.email = email;
  }

  // Overloaded constructor for cases where email is not provided
  public Users(String userName, String password) {
    this(userName, password, null);
  }

  public String getUserName() {
    return userName;
  }

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }
}