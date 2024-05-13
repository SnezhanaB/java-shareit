package ru.practicum.shareit.Item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CommentCreateDto {

    @NotBlank
    @NotNull
    private String text;

}
