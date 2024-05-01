package ru.practicum.shareit.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class CommentCreateDto {
    private Integer id;

    @NotBlank
    private String text;

    private String authorName;

    private Timestamp created;
}
