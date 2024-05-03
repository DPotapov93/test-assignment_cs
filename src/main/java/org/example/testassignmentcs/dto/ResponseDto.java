package org.example.testassignmentcs.dto;

import java.util.List;
import lombok.Data;

@Data
public class ResponseDto {
    private List<UserDto> data;
}
