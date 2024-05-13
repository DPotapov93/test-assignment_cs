package org.example.testassignmentcs.service;

import org.example.testassignmentcs.dto.UserCreateRequestDto;
import org.example.testassignmentcs.dto.WrapperDto;
import org.example.testassignmentcs.dto.UserDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserService {
    WrapperDto<UserDto> findAll();

    Long save(UserCreateRequestDto requestDto);

    void patchUpdate(Long id, UserDto userDto);

    void putUpdate(Long id, UserCreateRequestDto requestDto);

    WrapperDto<UserDto> findAllUsersAgeBetween(
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    );

    void deleteById(Long id);
}
