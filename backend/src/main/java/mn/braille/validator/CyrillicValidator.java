package mn.braille.validator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CyrillicValidator {

    private static final String MONGOLIAN_CYRILLIC = "абвгдеёжзийклмноөпрстуүфхцчшщъыьэюя" +
                                                      "АБВГДЕЁЖЗИЙКЛМНОӨПРСТУҮФХЦЧШЩЪЫЬЭЮЯ";
    private static final String ALLOWED_EXTRAS = " .,?!:;-()\"'/" +
                                                  "0123456789\n\r\t";

    public List<String> validate(String text) {
        List<String> errors = new ArrayList<>();
        if (text == null || text.isBlank()) {
            errors.add("Оролт хоосон байж болохгүй");
            return errors;
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (MONGOLIAN_CYRILLIC.indexOf(c) == -1 && ALLOWED_EXTRAS.indexOf(c) == -1) {
                errors.add(String.format("Зөвшөөрөгдөөгүй тэмдэгт '%c' байрлал %d дээр", c, i));
            }
        }
        return errors;
    }

    public boolean isValid(String text) {
        return validate(text).isEmpty();
    }
}
