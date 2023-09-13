package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public List<UserDto> toUserDto(Iterable<User> users) {
        List<UserDto> ans = new ArrayList<>();
        for (User user : users) {
            ans.add(UserMapper.toUserDto(user));
        }
        return ans;
    }

    public User toUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public User toUpdateUser(UserDto updateUserDto, UserDto user) {
        if (user.getName() != null) {
            updateUserDto.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUserDto.setEmail(user.getEmail());
        }
        User ans = toUser(updateUserDto);
        ans.setId(updateUserDto.getId());
        return ans;
    }
}