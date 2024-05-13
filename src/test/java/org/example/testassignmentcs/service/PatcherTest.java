package org.example.testassignmentcs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.example.testassignmentcs.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PatcherTest {
    private static final String PATCH_EXCEPTION_MESSAGE = "Error while applying patch";
    private static UserDto existingUserDto;
    private static UserDto incompleteUserDto;

    @InjectMocks
    private Patcher patcher;

    @BeforeEach
    void setUp() {
        existingUserDto = new UserDto()
                .setId(1L)
                .setEmail("john@example.com")
                .setFirstName("John")
                .setLastName("Smith")
                .setBirthDate(LocalDate.of(1990,1,1))
                .setAddress("Baker Street")
                .setPhoneNumber("0677377711");

        incompleteUserDto = new UserDto()
                .setEmail("dave@example.com")
                .setFirstName("Dave")
                .setLastName("Davis")
                .setBirthDate(LocalDate.of(2000,1,1))
                .setAddress("Spoon Street")
                .setPhoneNumber("0507377711");

    }

    @Test
    @DisplayName("Verify internPatcher() method works when change all fields")
    void internPatcher_PatchAppliedWithAllFields_Ok() {
        patcher.internPatcher(existingUserDto, incompleteUserDto);

        assertNotNull(existingUserDto);
        assertNotNull(incompleteUserDto);
        assertEquals(1L, existingUserDto.getId());
        assertEquals("dave@example.com", existingUserDto.getEmail());
        assertEquals("Dave", existingUserDto.getFirstName());
        assertEquals("Davis", existingUserDto.getLastName());
        assertEquals("Spoon Street", existingUserDto.getAddress());
        assertEquals("0507377711", existingUserDto.getPhoneNumber());
        assertEquals(LocalDate.of(2000,1,1), existingUserDto.getBirthDate());
    }

    @Test
    @DisplayName("Verify internPatcher() method works when change at least on field")
    void internPatch_PatchAppliedWithOneField_Ok() {
        UserDto oneFieldUserDto = new UserDto()
                .setEmail("changed@example.com");

        patcher.internPatcher(existingUserDto, oneFieldUserDto);

        assertNotNull(existingUserDto);
        assertNotNull(oneFieldUserDto);
        assertEquals(1L, existingUserDto.getId());
        assertEquals("changed@example.com", existingUserDto.getEmail());
        assertEquals("John", existingUserDto.getFirstName());
    }

    @Test
    @DisplayName("Verify internPatcher() method don`t work when existingDto is null")
    void internPatcher_ExistingUserDtoIsNull_ReturnsException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> patcher.internPatcher(null, incompleteUserDto));
        assertEquals(PATCH_EXCEPTION_MESSAGE, exception.getMessage());
    }
}
