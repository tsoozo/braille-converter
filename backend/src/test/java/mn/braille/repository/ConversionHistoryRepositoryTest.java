package mn.braille.repository;

import mn.braille.entity.ConversionHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ConversionHistoryRepositoryTest {

    @Autowired
    private ConversionHistoryRepository repository;

    @Test
    void shouldSaveAndRetrieveHistory() {
        ConversionHistory history = ConversionHistory.builder()
                .input("Монгол")
                .output("⠠⠍⠕⠝⠛⠕⠇")
                .inputLength(6)
                .outputLength(7)
                .durationMs(12L)
                .timestamp(LocalDateTime.now())
                .userId(null)
                .build();

        ConversionHistory saved = repository.save(history);

        assertThat(saved.getId()).isNotNull();
        Optional<ConversionHistory> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getInput()).isEqualTo("Монгол");
        assertThat(found.get().getOutput()).isEqualTo("⠠⠍⠕⠝⠛⠕⠇");
    }

    @Test
    void shouldAllowNullUserId() {
        ConversionHistory history = ConversionHistory.builder()
                .input("тест")
                .output("⠧⠑⠣⠧")
                .inputLength(4)
                .outputLength(4)
                .durationMs(5L)
                .timestamp(LocalDateTime.now())
                .userId(null)
                .build();

        ConversionHistory saved = repository.saveAndFlush(history);

        assertThat(saved.getUserId()).isNull();
    }

    @Test
    void shouldStoreLongTextInTextField() {
        String longInput = "а".repeat(5000);
        ConversionHistory history = ConversionHistory.builder()
                .input(longInput)
                .output("⠁".repeat(5000))
                .inputLength(5000)
                .outputLength(5000)
                .durationMs(80L)
                .timestamp(LocalDateTime.now())
                .build();

        ConversionHistory saved = repository.saveAndFlush(history);

        assertThat(saved.getInput()).hasSize(5000);
    }
}
