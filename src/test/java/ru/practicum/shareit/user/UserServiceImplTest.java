package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.TestingUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl service;

    private final ModelMapper mapper = new ModelMapper();

    @Test
    void createUser() {
        UserDto userDto = TestingUtils.createUserDto();
        User user = mapper.map(userDto, User.class);
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto saved = service.createUser(userDto);
        assertEquals(saved, userDto);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUser() {
        UserDto userDto = TestingUtils.createUserDto(2);
        userDto.setId(1);
        User user = TestingUtils.createUser(1);

        when(userRepository.getUserById(anyInt()))
                .thenReturn(user);
        when(userRepository.findUserByEmail(anyString()))
                .thenReturn(null);
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto saved = service.updateUser(userDto.getId(), userDto);
        assertEquals(saved, userDto);
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findUserByEmail(anyString());

    }

    @Test
    void updateUse_duplicateEmail() {
        UserDto userDto = TestingUtils.createUserDto(2);
        userDto.setId(1);
        User user = TestingUtils.createUser(1);

        when(userRepository.getUserById(anyInt()))
                .thenReturn(user);
        when(userRepository.findUserByEmail(anyString()))
                .thenReturn(user);
        when(userRepository.save(any()))
                .thenReturn(user);

        AlreadyExistsException e = assertThrows(
                AlreadyExistsException.class,
                () -> service.updateUser(userDto.getId(), userDto)
        );
        assertEquals(e.getMessage(), "Пользователь с email=" + userDto.getEmail() + " уже существует");
    }

    @Test
    void updateUse_sameEmail() {
        UserDto userDto = TestingUtils.createUserDto(1);
        userDto.setId(1);
        User user = TestingUtils.createUser(1);

        when(userRepository.getUserById(anyInt()))
                .thenReturn(user);
        when(userRepository.findUserByEmail(anyString()))
                .thenReturn(user);
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto saved = service.updateUser(userDto.getId(), userDto);
        assertEquals(saved, userDto);
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(0)).findUserByEmail(anyString());
    }

    @Test
    void getUserById() {

        service.getUserById(1);

        verify(userRepository, times(1)).getUserById(eq(1));

    }

    @Test
    void deleteUserById() {

        service.deleteUserById(1);

        verify(userRepository, times(1)).deleteById(eq(1));

    }

    @Test
    void getAllUsers() {
        service.getAllUsers();

        verify(userRepository, times(1)).findAll();
    }
}