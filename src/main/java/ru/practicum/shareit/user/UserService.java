package ru.practicum.shareit.user;


import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto updateUser(int userId, UserDto user);

    UserDto getUserById(int userId);
    void deleteUserById(int userId);

    ArrayList<UserDto> getAllUsers();
}
