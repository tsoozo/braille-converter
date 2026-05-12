package mn.braille.repository;

import mn.braille.entity.AppUser;
import mn.braille.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository repository;

    @Test
    void shouldFindByUsernameWhenExists() {
        repository.save(AppUser.builder()
                .username("testuser")
                .password("$2a$10$hashed")
                .role(UserRole.ROLE_USER)
                .build());

        Optional<AppUser> found = repository.findByUsername("testuser");

        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(UserRole.ROLE_USER);
    }

    @Test
    void shouldReturnEmptyWhenUsernameNotFound() {
        Optional<AppUser> found = repository.findByUsername("nobody");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldEnforceUniqueUsername() {
        repository.save(AppUser.builder()
                .username("dup")
                .password("pass")
                .role(UserRole.ROLE_USER)
                .build());

        AppUser duplicate = AppUser.builder()
                .username("dup")
                .password("other")
                .role(UserRole.ROLE_USER)
                .build();

        org.junit.jupiter.api.Assertions.assertThrows(
            Exception.class, () -> repository.saveAndFlush(duplicate)
        );
    }

    @Test
    void shouldPersistTimestampsOnCreate() {
        AppUser saved = repository.save(AppUser.builder()
                .username("admin")
                .password("$2a$10$hashed")
                .role(UserRole.ROLE_ADMIN)
                .build());

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldStoreRoleAsString() {
        AppUser user = repository.save(AppUser.builder()
                .username("roletest")
                .password("pass")
                .role(UserRole.ROLE_ADMIN)
                .build());

        AppUser found = repository.findById(user.getId()).get();
        assertThat(found.getRole()).isEqualTo(UserRole.ROLE_ADMIN);
    }
}
