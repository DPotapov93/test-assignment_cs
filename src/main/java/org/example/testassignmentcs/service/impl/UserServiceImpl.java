package org.example.testassignmentcs.service.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import org.example.testassignmentcs.dto.UserCreateRequestDto;
import org.example.testassignmentcs.dto.UserDto;
import org.example.testassignmentcs.dto.WrapperDto;
import org.example.testassignmentcs.exception.EntityNotFoundException;
import org.example.testassignmentcs.exception.RegistrationException;
import org.example.testassignmentcs.mapper.UserMapper;
import org.example.testassignmentcs.model.User;
import org.example.testassignmentcs.repository.UserRepository;
import org.example.testassignmentcs.service.Patcher;
import org.example.testassignmentcs.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private static final String AGE_EXCEPTION_MESSAGE
            = "User's age must be at least: ";
    private static final String EMAIL_EXCEPTION_MESSAGE
            = "This email is already registered";
    private static final String FIND_BY_ID_EXCEPTION_MESSAGE
            = "Can`t find user by id: ";
    private static final String DATES_EXCEPTION_MESSAGE
            = "'from' date should be before 'to' date";
    private final int adultAge;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Patcher patcher;

    public UserServiceImpl(
            @Value("${api.adult_age}")
            int adultAge,
            UserRepository userRepository,
            UserMapper userMapper,
            Patcher patcher
    ) {
        this.adultAge = adultAge;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.patcher = patcher;
    }

    @Override
    public Long save(UserCreateRequestDto requestDto) {
        userRepository.findByEmail(requestDto.getEmail())
                .ifPresent(user -> {
                    throw new RegistrationException(EMAIL_EXCEPTION_MESSAGE);
                });
        checkUserAge(requestDto.getBirthDate());
        User saved = userRepository.save(userMapper.toModel(requestDto));
        return saved.getId();
    }

    @Override
    public void patchUpdate(Long id, UserDto userDto) {
        UserDto existUserDto = userRepository.findById(id)
                        .map(userMapper::toDto)
                        .orElseThrow(
                                () -> new EntityNotFoundException(FIND_BY_ID_EXCEPTION_MESSAGE + id)
                        );
        patcher.internPatcher(existUserDto, userDto);
        checkUserAge(userDto.getBirthDate());
        User userPatch = userMapper.toModelFromUserDto(existUserDto);
        userPatch.setId(id);
        userRepository.save(userPatch);
    }

    @Override
    public void putUpdate(Long id, UserCreateRequestDto requestDto) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(FIND_BY_ID_EXCEPTION_MESSAGE + id);
        }
        checkUserAge(requestDto.getBirthDate());
        User userPut = userMapper.toModel(requestDto);
        userPut.setId(id);
        userRepository.save(userPut);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public WrapperDto<UserDto> findAllUsersAgeBetween(
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    ) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(DATES_EXCEPTION_MESSAGE);
        }
        List<UserDto> users = userRepository.findAllByBirthDateBetween(fromDate, toDate, pageable)
                .stream()
                .map(userMapper::toDto)
                .toList();
        WrapperDto<UserDto> responseDto = new WrapperDto<>();
        responseDto.setData(users);
        return responseDto;
    }

    private void checkUserAge(LocalDate birthDate) {
        Period period = Period.between(birthDate, LocalDate.now());
        int userAge = period.getYears();
        if (userAge < adultAge) {
            throw new RegistrationException(AGE_EXCEPTION_MESSAGE + adultAge);
        }
    }
}
