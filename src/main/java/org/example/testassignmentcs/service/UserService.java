package org.example.testassignmentcs.service;

import java.time.LocalDate;
import org.example.testassignmentcs.dto.UserCreateRequestDto;
import org.example.testassignmentcs.dto.UserDto;
import org.example.testassignmentcs.dto.WrapperDto;
import org.springframework.data.domain.Pageable;

public interface UserService {
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
