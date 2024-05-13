package org.example.testassignmentcs.service;

import java.lang.reflect.Field;
import org.example.testassignmentcs.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class Patcher {
    private static final String PATCH_EXCEPTION_MESSAGE = "Error while applying patch";

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
        } catch (RuntimeException | IllegalAccessException e) {
            throw new RuntimeException(PATCH_EXCEPTION_MESSAGE);
        }
    }
}
