package mn.braille.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mn.braille.exception.BrailleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrailleMappingService {

    @Value("${app.braille.mapping-file}")
    private Resource mappingFile;

    private final ObjectMapper objectMapper;

    @Getter private Map<String, String> letters;
    @Getter private Map<String, String> digits;
    @Getter private Map<String, String> punctuation;
    @Getter private String capitalSign;
    @Getter private String numberSign;

    @PostConstruct
    public void loadMapping() {
        try {
            Map<String, Object> root = objectMapper.readValue(
                mappingFile.getInputStream(),
                new TypeReference<>() {}
            );

            @SuppressWarnings("unchecked")
            Map<String, String> special = (Map<String, String>) root.get("specialSigns");
            capitalSign = special.get("capitalSign");
            numberSign = special.get("numberSign");

            letters = castStringMap(root.get("letters"));
            digits = castStringMap(root.get("digits"));
            punctuation = castStringMap(root.get("punctuation"));

            log.info("Braille mapping loaded: {} letters, {} digits, {} punctuation marks",
                letters.size(), digits.size(), punctuation.size());
        } catch (IOException e) {
            throw new BrailleException("Брайль mapping файл уншихад алдаа гарлаа: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> getFullMapping() {
        return Map.of(
            "letters", letters,
            "digits", digits,
            "punctuation", punctuation,
            "specialSigns", Map.of("capitalSign", capitalSign, "numberSign", numberSign)
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> castStringMap(Object value) {
        return (Map<String, String>) value;
    }
}
