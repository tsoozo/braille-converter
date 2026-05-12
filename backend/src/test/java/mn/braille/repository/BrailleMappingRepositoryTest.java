package mn.braille.repository;

import mn.braille.entity.BrailleMapping;
import mn.braille.entity.CharacterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BrailleMappingRepositoryTest {

    @Autowired
    private BrailleMappingRepository repository;

    @BeforeEach
    void setUp() {
        repository.saveAll(List.of(
            BrailleMapping.builder().cyrillic("а").braille("⠁").type(CharacterType.LETTER).build(),
            BrailleMapping.builder().cyrillic("б").braille("⠃").type(CharacterType.LETTER).build(),
            BrailleMapping.builder().cyrillic("1").braille("⠁").type(CharacterType.NUMBER).build(),
            BrailleMapping.builder().cyrillic(".").braille("⠲").type(CharacterType.PUNCTUATION).build()
        ));
    }

    @Test
    void shouldFindByCyrillicWhenExists() {
        Optional<BrailleMapping> result = repository.findByCyrillic("а");

        assertThat(result).isPresent();
        assertThat(result.get().getBraille()).isEqualTo("⠁");
        assertThat(result.get().getType()).isEqualTo(CharacterType.LETTER);
    }

    @Test
    void shouldReturnEmptyWhenCyrillicNotFound() {
        Optional<BrailleMapping> result = repository.findByCyrillic("X");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAllByType() {
        List<BrailleMapping> letters = repository.findByType(CharacterType.LETTER);

        assertThat(letters).hasSize(2);
        assertThat(letters).extracting(BrailleMapping::getCyrillic)
                .containsExactlyInAnyOrder("а", "б");
    }

    @Test
    void shouldPersistCreatedAt() {
        BrailleMapping saved = repository.findByCyrillic("а").get();

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldEnforceUniqueCyrillic() {
        BrailleMapping duplicate = BrailleMapping.builder()
                .cyrillic("а")
                .braille("⠁")
                .type(CharacterType.LETTER)
                .build();

        org.junit.jupiter.api.Assertions.assertThrows(
            Exception.class, () -> {
                repository.saveAndFlush(duplicate);
            }
        );
    }
}
