package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {

    private Long id;

    @NotEmpty
    @Size(max = 1024)
    private String text;

    private String authorName;

    private LocalDateTime created;
}