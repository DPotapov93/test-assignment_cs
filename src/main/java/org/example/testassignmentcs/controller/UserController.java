package org.example.testassignmentcs.controller;

import lombok.RequiredArgsConstructor;
import org.example.testassignmentcs.dto.ResponseDto;
import org.example.testassignmentcs.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseDto getAllUsers() {
        return userService.findAll();
    }
}
