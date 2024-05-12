package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Запрос вещи
 */
@Entity
@NoArgsConstructor
@Table(name = "requests", schema = "public")
@Getter
@Setter
@ToString
public class ItemRequest {

    /**
     * Уникальный идентификатор запроса
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Текст запроса, содержащий описание требуемой вещи
     */
    private String description;

    /**
     * Пользователь, создавший запрос
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    /**
     * Дата и время создания запроса
     */
    private LocalDateTime created;
}
