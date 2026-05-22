package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de PasswordValidator")
class PasswordValidatorTest {

    private PasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
    }

    // -------------------------------------------------------------------------
    // Tests classiques
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Mot de passe null → invalide")
    void nullPasswordIsInvalid() {
        assertFalse(validator.isValid(null));
    }

    @Test
    @DisplayName("Mot de passe valide : Password1!")
    void validPassword() {
        assertTrue(validator.isValid("Password1!"));
    }

    @Test
    @DisplayName("Mot de passe valide : Admin2024@")
    void validPasswordAdmin() {
        assertTrue(validator.isValid("Admin2024@"));
    }

    @Test
    @DisplayName("Mot de passe trop court → invalide")
    void tooShortPasswordIsInvalid() {
        assertFalse(validator.isValid("Sh0rt!"));
    }

    @Test
    @DisplayName("Sans majuscule → invalide")
    void noUppercaseIsInvalid() {
        assertFalse(validator.isValid("password1!"));
    }

    @Test
    @DisplayName("Sans minuscule → invalide")
    void noLowercaseIsInvalid() {
        assertFalse(validator.isValid("PASSWORD1!"));
    }

    @Test
    @DisplayName("Sans chiffre → invalide")
    void noDigitIsInvalid() {
        assertFalse(validator.isValid("Password!"));
    }

    @Test
    @DisplayName("Sans caractère spécial → invalide")
    void noSpecialCharIsInvalid() {
        assertFalse(validator.isValid("Password1"));
    }

    // -------------------------------------------------------------------------
    // Tests paramétrés — @CsvSource (password, expectedValid)
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "''{0}'' → valide={1}")
    @DisplayName("Validation via @CsvSource")
    @CsvSource({
            "Password1!,  true",
            "Admin2024@,  true",
            "short1!,     false",
            "PASSWORD1!,  false",
            "password1!,  false",
            "Password!,   false",
            "Password1,   false"
    })
    void csvSourceValidation(String password, boolean expected) {
        assertEquals(expected, validator.isValid(password));
    }

    // -------------------------------------------------------------------------
    // Tests paramétrés — @CsvSource (password, expectedMessage)
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "''{0}'' → ''{1}''")
    @DisplayName("Messages d'erreur via @CsvSource")
    @CsvSource({
            "short1!,    Password must contain at least 8 characters",
            "PASSWORD1!, Password must contain at least one lowercase letter",
            "password1!, Password must contain at least one uppercase letter",
            "Password!,  Password must contain at least one digit",
            "Password1,  Password must contain at least one special character",
            "Password1!, Password is valid"
    })
    void csvSourceErrorMessages(String password, String expectedMessage) {
        assertEquals(expectedMessage, validator.getErrorMessage(password));
    }

    // -------------------------------------------------------------------------
    // Test — @ValueSource : tous ces mots de passe doivent être invalides
    // -------------------------------------------------------------------------

    @ParameterizedTest(name = "''{0}'' doit être invalide")
    @DisplayName("Mots de passe invalides via @ValueSource")
    @ValueSource(strings = {
            "short1!",
            "PASSWORD1!",
            "password1!",
            "Password!",
            "Password1"
    })
    void valueSourceInvalidPasswords(String password) {
        assertFalse(validator.isValid(password),
                "Le mot de passe '" + password + "' devrait être invalide");
    }

    // -------------------------------------------------------------------------
    // Test — @MethodSource : cas avec null + valides + invalides
    // -------------------------------------------------------------------------

    static Stream<org.junit.jupiter.params.provider.Arguments> passwordProvider() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(null,         false, "Password must not be null"),
                org.junit.jupiter.params.provider.Arguments.of("short1!",    false, "Password must contain at least 8 characters"),
                org.junit.jupiter.params.provider.Arguments.of("PASSWORD1!", false, "Password must contain at least one lowercase letter"),
                org.junit.jupiter.params.provider.Arguments.of("password1!", false, "Password must contain at least one uppercase letter"),
                org.junit.jupiter.params.provider.Arguments.of("Password!",  false, "Password must contain at least one digit"),
                org.junit.jupiter.params.provider.Arguments.of("Password1",  false, "Password must contain at least one special character"),
                org.junit.jupiter.params.provider.Arguments.of("Password1!", true,  "Password is valid"),
                org.junit.jupiter.params.provider.Arguments.of("Admin2024@", true,  "Password is valid")
        );
    }

    @ParameterizedTest(name = "''{0}'' → valide={1}, message=''{2}''")
    @DisplayName("Validation complète via @MethodSource")
    @MethodSource("passwordProvider")
    void methodSourceFullValidation(String password, boolean expectedValid, String expectedMessage) {
        assertEquals(expectedValid, validator.isValid(password));
        assertEquals(expectedMessage, validator.getErrorMessage(password));
    }
}