package mn.braille.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ValidateResponse {

    private boolean valid;
    private List<String> errors;
}
