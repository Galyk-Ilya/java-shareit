package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IncorrectDateError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private Pattern emailPattern = Pattern.compile("^.+@.+\\..+$");

    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()
                || !emailPattern.matcher(userDto.getEmail()).matches()) {
            throw new IncorrectDateError("The email field is not filled in correctly.");
        }
        if (userDto.getName() == null || userDto.getName().isEmpty() || userDto.getName().isBlank()) {
            throw new IncorrectDateError("The name field is filled in incorrectly.");
        }
        User userCreated = userRepository.save(userMapper.toUser(userDto));
        return UserMapper.toUserDto(userCreated);
    }

    @Override
    public Long deleteUser(Long id) {
        userRepository.deleteById(id);
        return id;
    }

    @Override
    public List<UserDto> findUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserDto(users);
    }

    @Override
    public UserDto findUserById(Long id) {
        User userFind = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("The user with id = " + id + " does not exist."));
        return UserMapper.toUserDto(userFind);
    }

    @Override
    public UserDto updateUser(Long id, UserDto user) {
        user.setId(id);
        User interimUser = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("The user with id = " + id + " does not exist."));
        UserDto updateUserDto = UserMapper.toUserDto(interimUser);
        User updateUser = userMapper.toUpdateUser(updateUserDto, user);
        userRepository.save(updateUser);
        return UserMapper.toUserDto(updateUser);
    }
}