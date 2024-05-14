package org.example.testassignmentcs.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.example.testassignmentcs.dto.UserCreateRequestDto;
import org.example.testassignmentcs.dto.UserDto;
import org.example.testassignmentcs.dto.WrapperDto;
import org.example.testassignmentcs.exception.EntityNotFoundException;
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

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final int ADULT_AGE = 18;
    private static final String EMAIL_EXCEPTION_MESSAGE
            = "This email is already registered";

    private static final String AGE_EXCEPTION_MESSAGE
            = "User's age must be at least: ";

    private static final String DATES_EXCEPTION_MESSAGE
            = "'from' date should be before 'to' date";

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserMapper userMapper = Mockito.mock(UserMapper.class);
    private final Patcher patcher = Mockito.mock(Patcher.class);

    private final UserServiceImpl userService = new UserServiceImpl(
            ADULT_AGE,
            userRepository,
            userMapper,
            patcher
    );

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

        Long id = 1L;
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
        assertEquals(AGE_EXCEPTION_MESSAGE + ADULT_AGE, exception.getMessage());
    }

    @Test
    @DisplayName("Verify patchUpdate() method works")
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
    @DisplayName("patchUpdate() method donn`t work(non-existing id)")
    void patchUpdate_InValidId_ReturnsException() {
        assertThrows(EntityNotFoundException.class,
                () -> userService.patchUpdate(anyLong(), new UserDto()));
    }

    @Test
    @DisplayName("Verify putUpdate() method works")
    public void putUpdate_ValidRequestDto_Ok() {
        Long id = 1L;
        UserCreateRequestDto requestDto = new UserCreateRequestDto()
                .setEmail("user1@example.com")
                .setFirstName("John")
                .setLastName("Smith")
                .setBirthDate(LocalDate.of(1990, 1, 1));

        when(userRepository.existsById(id)).thenReturn(true);
        when(userMapper.toModel(requestDto)).thenReturn(new User());

        assertDoesNotThrow(() -> userService.putUpdate(id, requestDto));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("putUpdate() method donn`t work(non-existing id)")
    void putUpdate_InValidId_ReturnsException() {
        assertThrows(EntityNotFoundException.class,
                () -> userService.putUpdate(anyLong(), new UserCreateRequestDto()));
    }

    @Test
    @DisplayName("Verify findAllUsersAgeBetween() method works")
    void findAllUsersAgeBetween_ValidDates_ReturnsWrapperDto() {
        LocalDate fromDate = LocalDate.of(1999, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 1, 1);
        User john = new User()
                .setId(1L)
                .setEmail("john@example.com")
                .setFirstName("John")
                .setLastName("Smith")
                .setBirthDate(LocalDate.of(1999, 1,1));
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
                .setBirthDate(LocalDate.of(1999, 1,1));
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
        assertEquals(
                expected.getData().get(0).getBirthDate(),
                actual.getData().get(0).getBirthDate()
        );
        assertEquals(
                expected.getData().get(1).getFirstName(),
                actual.getData().get(1).getFirstName()
        );
    }

    @Test
    @DisplayName("findAllUsersAgeBetween() method donn`t work('from' is less than 'to')")
    void findAllUsersAgeBetween_InValidDateFrom_ReturnsException() {
        LocalDate fromDate = LocalDate.of(2000, 1, 1);
        LocalDate toDate = LocalDate.of(1999, 1, 1);
        when(userRepository.findAllByBirthDateBetween(fromDate, toDate, PageRequest.of(0, 10)))
                .thenThrow(new IllegalArgumentException(DATES_EXCEPTION_MESSAGE));
        assertThrows(IllegalArgumentException.class,
                () -> userService.findAllUsersAgeBetween(
                        fromDate,
                        toDate,
                        PageRequest.of(0, 10)));
    }
}
