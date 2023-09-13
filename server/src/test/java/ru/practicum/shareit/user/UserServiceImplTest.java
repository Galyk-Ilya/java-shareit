package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.IncorrectDateError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    @Autowired
    private final UserService userService;

    @Autowired
    private final UserMapper userMapper;

    @MockBean
    private final UserRepository userRepository;

    @Test
    void createUser() {
        UserDto user = UserDto.builder()
                .name("User1")
                .email("user1@test.ru")
                .build();

        User userCreate = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        when(userRepository.save(userMapper.toUser(user)))
                .thenReturn(userCreate);

        UserDto ans = userService.createUser(user);
        assertThat(ans, is(notNullValue()));
        assertThat(ans.getId(), is(1L));
        assertThat(ans.getName(), is("User1"));
        assertThat(ans.getEmail(), is("user1@test.ru"));
    }

    @Test
    void dropExceptionCreateUserIfEmailNull() {
        IncorrectDateError noEmailError;
        noEmailError = Assertions.assertThrows(IncorrectDateError.class,
                () -> userService.createUser(UserDto.builder().name("BadUser").build()));
        assertThat(noEmailError.getMessage(), is("The email field is not filled in correctly."));
    }

    @Test
    void dropExceptionCreateUserIfEmailBad() {
        IncorrectDateError noEmailError;
        noEmailError = Assertions.assertThrows(IncorrectDateError.class,
                () -> userService.createUser(UserDto.builder().name("BadUser").email("bad123").build()));
        assertThat(noEmailError.getMessage(), is("The email field is not filled in correctly."));
    }

    @Test
    void dropExceptionCreateUserIfNameNull() {
        IncorrectDateError noEmailError;
        noEmailError = Assertions.assertThrows(IncorrectDateError.class,
                () -> userService.createUser(UserDto.builder().email("user@test.com").build()));
        assertThat(noEmailError.getMessage(), is("The name field is filled in incorrectly."));
    }

    @Test
    void dropExceptionCreateUserIfNameEmpty() {
        IncorrectDateError nameEmptyError;
        nameEmptyError = Assertions.assertThrows(IncorrectDateError.class,
                () -> userService.createUser(UserDto.builder().email("user@test.com").name("").build()));
        assertThat(nameEmptyError.getMessage(), is("The name field is filled in incorrectly."));
    }

    @Test
    void dropExceptionCreateUserIfNameBlank() {
        IncorrectDateError nameBlankError;
        nameBlankError = Assertions.assertThrows(IncorrectDateError.class,
                () -> userService.createUser(UserDto.builder().email("user@test.com").name(" ").build()));
        assertThat(nameBlankError.getMessage(), is("The name field is filled in incorrectly."));
    }

    @Test
    void findUsersTest() {
        User user = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@test.ru")
                .build();

        when(userRepository.findAll())
                .thenReturn(Arrays.asList(user));

        List<UserDto> ans = userService.findUsers();
        assertThat(ans.size(), is(1));
        assertThat(ans.get(0).getName(), is("User1"));
        assertThat(ans.get(0).getEmail(), is("user1@test.ru"));
    }

    @Test
    void findUserByIdTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(User.builder().id(1L).name("test").email("test@test.com").build()));

        UserDto ans = userService.findUserById(1L);
        assertThat(ans, is(notNullValue()));
        assertThat(ans.getId(), is(1L));
        assertThat(ans.getName(), is("test"));
        assertThat(ans.getEmail(), is("test@test.com"));
    }

    @Test
    void dropExceptionFindUserByIdIfUserNotExist() {
        NotFoundException userNotExistError;

        userNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> userService.findUserById(1L));
        assertThat(userNotExistError.getMessage(), is("The user with id = " + 1L + " does not exist."));
    }

    @Test
    void dropExceptionUpdateUserByIdIfUserNotExist() {
        NotFoundException userNotExistError;

        userNotExistError = Assertions.assertThrows(NotFoundException.class,
                () -> userService.updateUser(1L, UserDto.builder().build()));
        assertThat(userNotExistError.getMessage(), is("The user with id = " + 1L + " does not exist."));
    }

    @Test
    void updateUserByIdTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(User.builder().id(1L).name("user").email("user@email.com").build()));

        when(userRepository.save(User.builder().id(1L).name("userUpdate").email("userUpdate@email.com").build()))
                .thenReturn(User.builder().id(1L).name("userUpdate").email("userUpdate@email.com").build());
        UserDto ans = userService.updateUser(1L, UserDto.builder().id(1L).name("userUpdate").email("userUpdate@email.com").build());
        assertThat(ans, is(notNullValue()));
        assertThat(ans.getEmail(), is("userUpdate@email.com"));
        assertThat(ans.getName(), is("userUpdate"));
        assertThat(ans.getId(), is(1L));
    }

    @Test
    void deleteUserTest() {
        Long ans = userService.deleteUser(1L);
        assertThat(ans, is(1L));
    }
}