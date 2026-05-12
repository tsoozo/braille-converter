package mn.braille.repository;

import mn.braille.entity.BrailleMapping;
import mn.braille.entity.CharacterType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrailleMappingRepository extends JpaRepository<BrailleMapping, Long> {

    Optional<BrailleMapping> findByCyrillic(String cyrillic);

    List<BrailleMapping> findByType(CharacterType type);
}
