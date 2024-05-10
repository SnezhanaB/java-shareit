package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
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
    List<Booking> findAllByBookerId(Integer bookerId, Pageable page);

    /**
     * By Booker and status
     */
    List<Booking> findAllByBookerIdAndStatus(Integer bookerId, BookingStatus status, Pageable page);

    /**
     * By Booker PAST
     */
    List<Booking> findAllByBookerIdAndEndBefore(Integer bookerId, LocalDateTime now, Pageable page);

    /**
     * By Booker FUTURE
     */
    List<Booking> findAllByBookerIdAndStartAfter(Integer bookerId, LocalDateTime now, Pageable page);

    /**
     * By Booker CURRENT
     */
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(
            Integer bookerId,
            LocalDateTime now,
            LocalDateTime alsoNow,
            Pageable page
    );

    /**
     * By Owner ALL
     */
    List<Booking> findAllByItemOwnerId(Integer ownerId, Pageable page);

    /**
     * By Owner and status
     */
    List<Booking> findAllByItemOwnerIdAndStatus(Integer bookerId, BookingStatus status, Pageable page);

    /**
     * By Owner PAST
     */
    List<Booking> findAllByItemOwnerIdAndEndBefore(Integer bookerId, LocalDateTime now, Pageable page);

    /**
     * By Owner FUTURE
     */
    List<Booking> findAllByItemOwnerIdAndStartAfter(Integer bookerId, LocalDateTime now, Pageable page);

    /**
     * By Owner CURRENT
     */
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(
            Integer bookerId,
            LocalDateTime now,
            LocalDateTime alsoNow,
            Pageable page
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
