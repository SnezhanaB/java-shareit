package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Бронирование
 */
@Entity
@NoArgsConstructor
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@ToString
public class Booking {
    /**
     * Уникальный идентификатор бронирования
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * Дата и время начала бронирования
     */
    @Column(name = "start_time", nullable = false)
    private Timestamp start;
    /**
     * Дата и время конца бронирования
     */
    @Column(name = "end_time", nullable = false)
    private Timestamp end;
    /**
     * Вещь, которую пользователь бронирует
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Item item;
    /**
     * Пользователь, который осуществляет бронирование
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User booker;
    /**
     * Статус бронирования
     */
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

}
