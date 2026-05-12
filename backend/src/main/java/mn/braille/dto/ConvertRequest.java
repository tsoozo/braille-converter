package mn.braille.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConvertRequest {

    @NotBlank(message = "Оролт хоосон байж болохгүй")
    @Size(max = 5000, message = "Оролт 5000 тэмдэгтээс хэтрэхгүй байх ёстой")
    private String text;
}
