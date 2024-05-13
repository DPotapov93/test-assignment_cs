package org.example.testassignmentcs.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.example.testassignmentcs.dto.UserCreateRequestDto;
import org.example.testassignmentcs.dto.UserDto;
import org.example.testassignmentcs.dto.WrapperDto;
import org.example.testassignmentcs.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public WrapperDto<UserDto> getAllUsers() {
        return userService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateRequestDto requestDto) {
        Long userId = userService.save(requestDto);
        String resourceUrl = "/users/" + userId;
        return ResponseEntity.created(URI.create(resourceUrl)).build();
    }

    @PatchMapping("/{id}")
    public void patchUser(@PathVariable Long id,
                          @RequestBody UserDto userDto) {
        userService.patchUpdate(id, userDto);
    }

    @PutMapping("/{id}")
    public void putUser(@PathVariable Long id,
                        @RequestBody @Valid UserCreateRequestDto requestDto) {
        userService.putUpdate(id, requestDto);
    }

    @GetMapping("/search")
    public WrapperDto<UserDto> getAllUsersByBirthDateRange(
            @RequestParam(value = "from") LocalDate fromDate,
            @RequestParam(value = "to") LocalDate toDate,
            Pageable pageable) {
        return userService.findAllUsersAgeBetween(fromDate, toDate, pageable);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
