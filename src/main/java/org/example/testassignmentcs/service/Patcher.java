package org.example.testassignmentcs.service;

import org.example.testassignmentcs.dto.UserDto;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class Patcher {
    public void internPatcher(
            UserDto existingUserDto,
            UserDto incompleteUserDto
    ) {

        try {
            Class<?> userDtoClass = UserDto.class;
            Field[] declaredFields = userDtoClass.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                Object value = field.get(incompleteUserDto);
                if (value != null) {
                    field.set(existingUserDto, value);
                }
                field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while applying patch");
        }
    }
}
