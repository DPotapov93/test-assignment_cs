package org.example.testassignmentcs.service.impl;

import org.example.testassignmentcs.dto.UserCreateRequestDto;
import org.example.testassignmentcs.dto.UserDto;
import org.example.testassignmentcs.dto.WrapperDto;
import org.example.testassignmentcs.exception.EntityNotFoundException;
import org.example.testassignmentcs.exception.IllegalArgumentException;
import org.example.testassignmentcs.exception.RegistrationException;
import org.example.testassignmentcs.mapper.UserMapper;
import org.example.testassignmentcs.model.User;
import org.example.testassignmentcs.repository.UserRepository;
import org.example.testassignmentcs.service.Patcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final String EMAIL_EXCEPTION_MESSAGE
            = "This email is already registered";

    private static final String AGE_EXCEPTION_MESSAGE
            = "User must be at least 18 years old";

    private static final String DATES_EXCEPTION_MESSAGE
            = "'from' date should be before 'to' date";

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserMapper userMapper = Mockito.mock(UserMapper.class);
    private final Patcher patcher = Mockito.mock(Patcher.class);

    private final UserServiceImpl userService = new UserServiceImpl(18, userRepository, userMapper, patcher);

    @Test
    void test() {
        userService.findAll();
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidUserCreateRequestDto_ReturnsLong() {
        UserCreateRequestDto requestDto = new UserCreateRequestDto()
                .setEmail("user1@example.com")
                .setFirstName("John")
                .setLastName("Smith")
                .setBirthDate(LocalDate.of(1990, 1, 1))
                .setAddress("Baker Street")
                .setPhoneNumber("0679876543");
        Long id =1L;
        User user = new User()
                .setId(id)
                .setEmail(requestDto.getEmail())
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setBirthDate(requestDto.getBirthDate())
                .setAddress(requestDto.getAddress())
                .setPhoneNumber(requestDto.getPhoneNumber());

        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        Long expect = user.getId();

        Long result = userService.save(requestDto);
        assertNotNull(result);
        assertEquals(expect, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("save() method don`t work(already existing email)")
    void save_UserCreateDtoWithExistingEmail_ReturnsException() {
        UserCreateRequestDto requestDto = new UserCreateRequestDto()
                .setEmail("user1@example.com");

        when(userRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.of(new User()));

        RegistrationException exception
                = assertThrows(RegistrationException.class, () -> userService.save(requestDto));
        assertEquals(EMAIL_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("save() method don`t work(age is less then 18)")
    void save_UserCreateDtoWithInvalidAge_ReturnsException() {
        UserCreateRequestDto requestDto = new UserCreateRequestDto()
                .setBirthDate(LocalDate.of(2015, 1, 1));
        RegistrationException exception
                = assertThrows(RegistrationException.class, () -> userService.save(requestDto));
        assertEquals(AGE_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    void patchUpdate_ValidUserIdAndValidUserDto_Ok() {
        Long id = 1L;
        User userFromDb = new User()
                .setId(id)
                .setEmail("user1@example.com")
                .setFirstName("John")
                .setEmail("Smith")
                .setBirthDate(LocalDate.of(1990, 1, 1));

        when(userRepository.findById(id)).thenReturn(Optional.of(userFromDb));

        UserDto existUserDto = new UserDto()
                .setId(userFromDb.getId())
                .setEmail(userFromDb.getEmail())
                .setFirstName(userFromDb.getFirstName())
                .setLastName(userFromDb.getLastName())
                .setBirthDate(userFromDb.getBirthDate());

        when(userMapper.toDto(userFromDb)).thenReturn(existUserDto);

        UserDto userDto = new UserDto()
                .setId(existUserDto.getId())
                .setEmail(existUserDto.getEmail())
                .setFirstName("David")
                .setLastName("Davis")
                .setBirthDate(existUserDto.getBirthDate());

        existUserDto.setFirstName(userDto.getFirstName());
        existUserDto.setLastName(userDto.getLastName());

        User userPatch = new User()
                .setId(userFromDb.getId())
                .setLastName(existUserDto.getFirstName())
                .setLastName(existUserDto.getLastName())
                .setBirthDate(existUserDto.getBirthDate());

        when(userMapper.toModelFromUserDto(existUserDto)).thenReturn(userPatch);
        when(userRepository.save(userPatch)).thenReturn(userPatch);

        assertDoesNotThrow(() -> userService.patchUpdate(id, userDto));
        verify(userRepository, times(1)).findById(id);
        verify(userMapper, times(1)).toDto(userFromDb);
        verify(userMapper, times(1)).toModelFromUserDto(userDto);
        verify(userRepository, times(1)).save(userPatch);
    }

    @Test
    void patchUpdate_InValidId_ReturnsException() {
        assertThrows(EntityNotFoundException.class,
                () -> userService.patchUpdate(anyLong(), new UserDto()));
    }

    @Test
    public void putUpdate_ValidRequestDto_Ok() {
        Long id = 1L;
        UserCreateRequestDto requestDto = new UserCreateRequestDto(/* заполните поля DTO */)
                .setEmail("user1@example.com")
                .setFirstName("John")
                .setLastName("Smith")
                .setBirthDate(LocalDate.of(1990, 1, 1));

        when(userRepository.existsById(id)).thenReturn(true);
        when(userMapper.toModel(requestDto)).thenReturn(new User(/* заполните данные пользователя */));

        assertDoesNotThrow(() -> userService.putUpdate(id, requestDto));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void putUpdate_InValidId_ReturnsException() {
        assertThrows(EntityNotFoundException.class,
                () -> userService.putUpdate(anyLong(), new UserCreateRequestDto()));
    }

    @Test
    void findAllUsersAgeBetween_ValidDates_ReturnsWrapperDto() {
        LocalDate fromDate = LocalDate.of(1999, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 1, 1);
        User john = new User()
                .setId(1L)
                .setEmail("john@example.com")
                .setFirstName("John")
                .setLastName("Smith")
                .setBirthDate(LocalDate.of(1999, 1 ,1));
        User david = new User()
                .setId(2L)
                .setEmail("david@example.com")
                .setFirstName("David")
                .setLastName("Davis")
                .setBirthDate(LocalDate.of(1999,1,1));

        UserDto johnDto = new UserDto()
                .setId(1L)
                .setEmail("john@example.com")
                .setFirstName("John")
                .setLastName("Smith")
                .setBirthDate(LocalDate.of(1999, 1 ,1));
        UserDto davidDto = new UserDto()
                .setId(2L)
                .setEmail("david@example.com")
                .setFirstName("David")
                .setLastName("Davis")
                .setBirthDate(LocalDate.of(1999,1,1));

        Pageable pageable = PageRequest.of(0, 10);

        WrapperDto<UserDto> expected = new WrapperDto<>();
        expected.setData(List.of(johnDto, davidDto));

        when(userRepository.findAllByBirthDateBetween(fromDate, toDate, pageable))
                .thenReturn(List.of(john, david));
        when(userMapper.toDto(any(User.class))).thenAnswer(invocationOnMock -> {
            User user = invocationOnMock.getArgument(0);
            return new UserDto()
                    .setId(user.getId())
                    .setEmail(user.getEmail())
                    .setLastName(user.getLastName())
                    .setFirstName(user.getFirstName())
                    .setBirthDate(user.getBirthDate());
        });

        WrapperDto<UserDto> actual = userService.findAllUsersAgeBetween(fromDate, toDate, pageable);

        assertNotNull(actual);
        assertEquals(expected, actual);
        assertEquals(expected.getData(), actual.getData());
        assertEquals(expected.getData().get(0), actual.getData().get(0));
        assertEquals(expected.getData().get(1), actual.getData().get(1));
        assertEquals(expected.getData().get(0).getBirthDate(), actual.getData().get(0).getBirthDate());
        assertEquals(expected.getData().get(1).getFirstName(), actual.getData().get(1).getFirstName());
    }

    @Test
    void findAllUsersAgeBetween_InValidDateFrom_ReturnsException() {
        LocalDate fromDate = LocalDate.of(2000, 1, 1);
        LocalDate toDate = LocalDate.of(1999, 1, 1);
        when(userRepository.findAllByBirthDateBetween(fromDate, toDate, PageRequest.of(0, 10)))
                .thenThrow(new IllegalArgumentException(DATES_EXCEPTION_MESSAGE));
        assertThrows(IllegalArgumentException.class, () -> userService.findAllUsersAgeBetween(fromDate, toDate, PageRequest.of(0, 10)));
    }
}