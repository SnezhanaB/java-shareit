package ru.practicum.shareit.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class CommentDto {

    private Integer id;

    private String text;

    private String authorName;

    private Timestamp created;
}
