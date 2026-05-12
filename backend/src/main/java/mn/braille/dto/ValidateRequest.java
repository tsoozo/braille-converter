package mn.braille.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidateRequest {

    @NotBlank(message = "Оролт хоосон байж болохгүй")
    private String text;
}
