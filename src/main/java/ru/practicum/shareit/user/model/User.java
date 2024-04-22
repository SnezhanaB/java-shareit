package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * Пользователь
 */
@Entity
@NoArgsConstructor
@Table(name = "users", schema = "public")
@Getter
@Setter
@ToString
public class User {
    /**
     * Уникальный идентификатор пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * Имя или логин пользователя
     */
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * Адрес электронной почты
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;
}
