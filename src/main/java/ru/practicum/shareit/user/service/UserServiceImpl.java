package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.userRepository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        log.info("User with id: {} has been created", user.getId());
        return userRepository.save(user);
    }

    public User getUserById(long id) {
        return getUserIfExistOrThrow(id);
    }

    public List<User> getAllUsers() {
        log.info("User info received");
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        User oldUser = getUserById(user.getId());
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            oldUser.setName(user.getName());
        }
        log.info("User with id: {} updated", user.getId());
        return userRepository.save(oldUser);
    }

    public void deleteUser(long id) {
        getUserIfExistOrThrow(id);
        userRepository.deleteById(id);
        log.info("User with id: {} deleted", id);
    }

    @Override
    public User getUserIfExistOrThrow(long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("User with id: " + id + " not found");
        });
=======
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DublicateException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public User createUser(User user) {
        checkEmail(user);
        return userStorage.createUser(user);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User updateUser(User user) {
        checkEmail(user);
        return userStorage.updateUser(user);
    }

    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

    private void checkEmail(User user) {
        if (userStorage.getAllUsers().stream()
                .anyMatch(
                        stored -> stored.getEmail().equalsIgnoreCase(user.getEmail())
                                && stored.getId() != user.getId()
                )
        ) {
            throw new DublicateException("Пользователь с таким адресом Эл. почты " +
                    user.getEmail() + " уже существует!");
        }
    }
}