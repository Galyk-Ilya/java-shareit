package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@AllArgsConstructor
public class User {

    private Long id;

    @NotBlank(message = "The name cannot be empty")
    private String name;

    @NotBlank(message = "The email field cannot be empty")
    @Email(message = "Invalid email value entered")
    private String email;
}