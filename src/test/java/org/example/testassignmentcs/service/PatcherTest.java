package org.example.testassignmentcs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.example.testassignmentcs.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PatcherTest {
    private static final String PATCH_EXCEPTION_MESSAGE = "Error while applying patch";
    private static UserDto existingUserDto;
    private static UserDto incompleteUserDto;

    private final Patcher patcher = new Patcher();

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
    @DisplayName("Verify internPatcher() method works when change at least one field")
    void internPatch_PatchAppliedWithOneField_Ok() {
        String changedEmail = "changed@example.com";
        UserDto oneFieldUserDto = new UserDto()
                .setEmail(changedEmail);

        patcher.internPatcher(existingUserDto, oneFieldUserDto);

        assertNotNull(existingUserDto);
        assertNotNull(oneFieldUserDto);
        assertEquals(1L, existingUserDto.getId());
        assertEquals(changedEmail, existingUserDto.getEmail());
        assertEquals("John", existingUserDto.getFirstName());
    }

    @Test
    @DisplayName("Verify internPatcher() method works when all fields are null and"
            + "existingUserDto will not change")
    void internPatch_PatchAppliedWithAllFieldsAreNull_Ok() {
        patcher.internPatcher(existingUserDto, new UserDto());
        assertNotNull(existingUserDto);
        assertEquals(1L, existingUserDto.getId());
        assertEquals("john@example.com", existingUserDto.getEmail());
        assertEquals("John", existingUserDto.getFirstName());
        assertEquals("Smith", existingUserDto.getLastName());
        assertEquals(LocalDate.of(1990,1,1), existingUserDto.getBirthDate());
        assertEquals("Baker Street", existingUserDto.getAddress());
        assertEquals("0677377711", existingUserDto.getPhoneNumber());

    }

    @Test
    @DisplayName("Verify internPatcher() method don`t work when existingDto is null")
    void internPatcher_ExistingUserDtoIsNull_ReturnsException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> patcher.internPatcher(null, incompleteUserDto));
        assertEquals(PATCH_EXCEPTION_MESSAGE, exception.getMessage());
    }
}
