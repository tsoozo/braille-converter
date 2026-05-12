package mn.braille.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mn.braille.dto.ConvertRequest;
import mn.braille.dto.ConvertResponse;
import mn.braille.dto.ValidateRequest;
import mn.braille.dto.ValidateResponse;
import mn.braille.service.BrailleConversionService;
import mn.braille.service.BrailleMappingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Conversion", description = "Кирилл → Брайль хөрвүүлэх API")
public class ConvertController {

    private final BrailleConversionService conversionService;
    private final BrailleMappingService mappingService;

    @PostMapping("/convert")
    @Operation(summary = "Кирилл текстийг Брайль болгон хөрвүүлнэ")
    public ResponseEntity<ConvertResponse> convert(@Valid @RequestBody ConvertRequest request) {
        return ResponseEntity.ok(conversionService.convert(request));
    }

    @PostMapping("/validate")
    @Operation(summary = "Оролт текстийг шалгана")
    public ResponseEntity<ValidateResponse> validate(@Valid @RequestBody ValidateRequest request) {
        return ResponseEntity.ok(conversionService.validate(request));
    }

    @GetMapping("/mapping")
    @Operation(summary = "Бүх Брайль mapping буцаана")
    public ResponseEntity<Map<String, Object>> getMapping() {
        return ResponseEntity.ok(mappingService.getFullMapping());
    }
}
