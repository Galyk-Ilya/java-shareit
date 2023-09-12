package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable(name = "id") Long id,
                              @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public Long deleteUser(@PathVariable(name = "id") Long id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable(name = "id") Long id) {
        return userService.findUserById(id);
    }

    @GetMapping
    public List<UserDto> findUsers() {
        return userService.findUsers();
    }
}