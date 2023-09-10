package ru.practicum.shareit.item.comment.mapper;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, LocalDateTime time) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(time)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}