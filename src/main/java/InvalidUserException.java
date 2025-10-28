package main.java;
public class InvalidUserException extends Exception {
  public InvalidUserException(String message) {
    super(message);
  }
}