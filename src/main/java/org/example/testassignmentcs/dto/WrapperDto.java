package org.example.testassignmentcs.dto;

import java.util.List;
import lombok.Data;

@Data
public class WrapperDto<T> {
    private List<T> data;
}
