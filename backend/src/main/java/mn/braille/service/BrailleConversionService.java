package mn.braille.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mn.braille.dto.ConvertRequest;
import mn.braille.dto.ConvertResponse;
import mn.braille.dto.ValidateRequest;
import mn.braille.dto.ValidateResponse;
import mn.braille.exception.BrailleException;
import mn.braille.validator.CyrillicValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrailleConversionService {

    private final BrailleMappingService mappingService;
    private final CyrillicValidator cyrillicValidator;

    public ConvertResponse convert(ConvertRequest request) {
        long start = System.currentTimeMillis();
        String text = request.getText();

        List<String> errors = cyrillicValidator.validate(text);
        if (!errors.isEmpty()) {
            throw new BrailleException("Оролт алдаатай: " + errors.get(0), HttpStatus.BAD_REQUEST);
        }

        String braille = convertToBraille(text);
        long elapsed = System.currentTimeMillis() - start;

        log.debug("Converted {} chars in {}ms", text.length(), elapsed);

        return ConvertResponse.builder()
                .originalText(text)
                .brailleText(braille)
                .characterCount(text.length())
                .processingTimeMs(elapsed)
                .build();
    }

    public ValidateResponse validate(ValidateRequest request) {
        List<String> errors = cyrillicValidator.validate(request.getText());
        return ValidateResponse.builder()
                .valid(errors.isEmpty())
                .errors(errors)
                .build();
    }

    private String convertToBraille(String text) {
        Map<String, String> letters = mappingService.getLetters();
        Map<String, String> digits = mappingService.getDigits();
        Map<String, String> punctuation = mappingService.getPunctuation();
        String capitalSign = mappingService.getCapitalSign();
        String numberSign = mappingService.getNumberSign();

        StringBuilder result = new StringBuilder();
        boolean inNumber = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String ch = String.valueOf(c);

            if (Character.isDigit(c)) {
                if (!inNumber) {
                    result.append(numberSign);
                    inNumber = true;
                }
                result.append(digits.getOrDefault(ch, ch));
            } else {
                inNumber = false;
                if (Character.isUpperCase(c)) {
                    String lower = ch.toLowerCase();
                    if (letters.containsKey(lower)) {
                        result.append(capitalSign).append(letters.get(lower));
                        continue;
                    }
                }
                String lower = ch.toLowerCase();
                if (letters.containsKey(lower)) {
                    result.append(letters.get(lower));
                } else if (punctuation.containsKey(ch)) {
                    result.append(punctuation.get(ch));
                } else {
                    result.append(ch);
                }
            }
        }

        return result.toString();
    }
}
