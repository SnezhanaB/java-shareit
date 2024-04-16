package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserInMemoryServiceImpl implements UserService {

    private int currentId = 1;

    private final HashMap<Integer, User> users = new HashMap<>();

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public UserDto createUser(UserDto user) throws AlreadyExistsException {
        checkEmailExists(user.getEmail());
        user.setId(currentId++);
        users.put(user.getId(), mapper.map(user, User.class));
        return user;
    }

    @Override
    public UserDto updateUser(int userId, UserDto user) throws NotFoundException, AlreadyExistsException {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        User updated = users.get(userId);
        if (!(user.getName() == null || user.getName().isEmpty())) {
            updated.setName(user.getName());
        }
        if (!(user.getEmail() == null || user.getEmail().equals(updated.getEmail()) || user.getEmail().isEmpty())) {
            checkEmailExists(user.getEmail());
            updated.setEmail(user.getEmail());
        }
        users.put(userId, updated);
        return mapper.map(updated, UserDto.class);
    }

    @Override
    public UserDto getUserById(int userId) throws NotFoundException {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        return mapper.map(users.get(userId), UserDto.class);
    }

    @Override
    public void deleteUserById(int userId) throws NotFoundException {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

        users.remove(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream().map((u) -> mapper.map(u, UserDto.class)).collect(Collectors.toList());
    }

    private void checkEmailExists(String email) throws AlreadyExistsException {
        if (users.values().stream().anyMatch((u) -> u.getEmail().equals(email))) {
            throw new AlreadyExistsException("Пользователь с email=" + email + " уже существует");
        }
    }
}
