package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserTest {

    private JacksonTester<User> json;
    private User user;
    private Validator validator;

    public UserTest(@Autowired JacksonTester<User> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void start() {
        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@test.com");
    }

    @Test
    void userJsonTest() throws Exception {

        JsonContent<User> result = json.write(user);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@test.com");
    }

    @Test
    void userNoNameTest() throws Exception {
        user.setName(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='The name cannot be empty'");
    }

    @Test
    void userNameIsBlankTest() throws Exception {
        user.setName(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='The name cannot be empty'");
    }

    @Test
    void userNameIsEmptyTest() throws Exception {
        user.setName("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='The name cannot be empty'");
    }

    @Test
    void userEmailIsNullTest() throws Exception {
        user.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='The email field cannot be empty'");
    }

    @Test
    void userEmailIsBlank() throws Exception {
        user.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='The email field cannot be empty'");
    }

    @Test
    void userEmailIsWrong() throws Exception {
        user.setEmail("useremail5");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='Invalid email value entered'");
    }
    }


