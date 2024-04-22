package ru.practicum.shareit.user;


import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto updateUser(int userId, UserDto user);

    UserDto getUserById(int userId);

    void deleteUserById(int userId);

    List<UserDto> getAllUsers();
}
