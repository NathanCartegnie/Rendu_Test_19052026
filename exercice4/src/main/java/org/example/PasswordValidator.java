package org.example;

public class PasswordValidator {

    public boolean isValid(String password) {
        if (password == null) return false;
        if (password.length() < 8) return false;
        if (!password.chars().anyMatch(Character::isLowerCase)) return false;
        if (!password.chars().anyMatch(Character::isUpperCase)) return false;
        if (!password.chars().anyMatch(Character::isDigit)) return false;
        if (!password.chars().anyMatch(c -> "!@#$%".indexOf(c) >= 0)) return false;
        return true;
    }

    public String getErrorMessage(String password) {
        if (password == null)
            return "Password must not be null";
        if (password.length() < 8)
            return "Password must contain at least 8 characters";
        if (!password.chars().anyMatch(Character::isLowerCase))
            return "Password must contain at least one lowercase letter";
        if (!password.chars().anyMatch(Character::isUpperCase))
            return "Password must contain at least one uppercase letter";
        if (!password.chars().anyMatch(Character::isDigit))
            return "Password must contain at least one digit";
        if (!password.chars().anyMatch(c -> "!@#$%".indexOf(c) >= 0))
            return "Password must contain at least one special character";
        return "Password is valid";
    }
}