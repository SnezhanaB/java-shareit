package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.sql.Timestamp;
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
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, Timestamp now);

    /**
     * By Booker FUTURE
     */
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId, Timestamp now);

    /**
     * By Booker CURRENT
     */
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByIdAsc(Integer bookerId, Timestamp now,
                                                                         Timestamp alsoNow);
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
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer bookerId, Timestamp now);

    /**
     * By Owner FUTURE
     */
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Integer bookerId, Timestamp now);

    /**
     * By Owner CURRENT
     */
    List<Booking> findAllByItemOwnerIdAndStartAfterAndEndBeforeOrderByStartDesc(
            Integer bookerId,
            Timestamp now,
            Timestamp alsoNow
    );

    /**
     * Find last booking for item with id
     */
    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
            Integer itemId,
            BookingStatus status,
            Timestamp now
    );

    /**
     * Find next booking for item with id
     */
    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Integer itemId,
            BookingStatus status,
            Timestamp now
    );

    /**
     * Check for user can add comment (if any found)
     */
    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
            Integer bookerId,
            Integer itemId,
            BookingStatus status,
            Timestamp now
    );
}
