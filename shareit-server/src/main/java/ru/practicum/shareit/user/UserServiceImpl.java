package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final ModelMapper mapper = new ModelMapper();

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = repository.save(mapper.map(userDto, User.class));
        return mapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {

        User updated = repository.getUserById(userId);
        if (!(userDto.getName() == null || userDto.getName().isEmpty())) {
            updated.setName(userDto.getName());
        }
        if (!(userDto.getEmail() == null || userDto.getEmail().equals(updated.getEmail()) || userDto.getEmail().isEmpty())) {
            checkEmailExists(userDto.getEmail());
            updated.setEmail(userDto.getEmail());
        }
        return mapper.map(repository.save(updated), UserDto.class);
    }

    @Override
    public UserDto getUserById(int userId) {
        return mapper.map(repository.getUserById(userId), UserDto.class);
    }

    @Override
    public void deleteUserById(int userId) {
        repository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream().map((u) -> mapper.map(u, UserDto.class)).collect(Collectors.toList());
    }

    private void checkEmailExists(String email) throws AlreadyExistsException {
        User founded = repository.findUserByEmail(email);
        if (founded != null) {
            throw new AlreadyExistsException("Пользователь с email=" + email + " уже существует");
        }
    }
}
