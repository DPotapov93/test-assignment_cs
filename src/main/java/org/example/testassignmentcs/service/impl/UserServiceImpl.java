package org.example.testassignmentcs.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.testassignmentcs.dto.ResponseDto;
import org.example.testassignmentcs.dto.UserDto;
import org.example.testassignmentcs.mapper.UserMapper;
import org.example.testassignmentcs.repository.UserRepository;
import org.example.testassignmentcs.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public ResponseDto findAll() {
        List<UserDto> users = userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
        ResponseDto responseDto = new ResponseDto();
        responseDto.setData(users);
        return responseDto;
    }
}
