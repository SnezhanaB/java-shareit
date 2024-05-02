package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Booking getBookingById(Integer id);

    /**
     * By Booker ALL
     */
    List<Booking> findAllByBookerIdOrderByStartDesc(Integer bookerId);

    /**
     * By Booker and status
     */
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer bookerId, BookingStatus status);

    /**
     * By Booker PAST
     */
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime now);

    /**
     * By Booker FUTURE
     */
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime now);

    /**
     * By Booker CURRENT
     */
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(
            Integer bookerId,
            LocalDateTime now,
            LocalDateTime alsoNow
    );

    /**
     * By Owner ALL
     */
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Integer ownerId);

    /**
     * By Owner and status
     */
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Integer bookerId, BookingStatus status);

    /**
     * By Owner PAST
     */
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime now);

    /**
     * By Owner FUTURE
     */
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime now);

    /**
     * By Owner CURRENT
     */
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Integer bookerId,
            LocalDateTime now,
            LocalDateTime alsoNow
    );

    /**
     * Find last booking for item with id
     */
    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
            Integer itemId,
            BookingStatus status,
            LocalDateTime now
    );

    /**
     * Find next booking for item with id
     */
    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Integer itemId,
            BookingStatus status,
            LocalDateTime now
    );

    /**
     * Check for user can add comment (if any found)
     */
    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
            Integer bookerId,
            Integer itemId,
            BookingStatus status,
            LocalDateTime now
    );
}
