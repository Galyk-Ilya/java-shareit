package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersistingAnUser() {
        User user = new User();
        user.setEmail("demo-user@email.com");
        user.setName("demo");

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());
    }

    @Test
    void verifyRepositoryByUser() {
        User user = new User();
        user.setEmail("demo-user@email.com");
        user.setName("demo");

        Assertions.assertNull(user.getId());
        repository.save(user);
        Assertions.assertNotNull(user.getId());
    }
}