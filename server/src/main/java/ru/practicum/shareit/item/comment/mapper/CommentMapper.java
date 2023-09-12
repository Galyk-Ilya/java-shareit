package ru.practicum.shareit.item.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public Comment toComment(CommentDto commentDto, LocalDateTime time) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(time)
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}