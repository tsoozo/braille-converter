package mn.braille.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConvertResponse {

    private String originalText;
    private String brailleText;
    private int characterCount;
    private long processingTimeMs;
}
