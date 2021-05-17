package cz.cvut.fel.tmr.service;

import cz.cvut.fel.tmr.dto.user.UserDto;
import cz.cvut.fel.tmr.environment.Generator;
import cz.cvut.fel.tmr.exception.AlreadyExistsException;
import cz.cvut.fel.tmr.exception.NotFoundException;
import cz.cvut.fel.tmr.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceTest {

    private String rawToken;

    private Long userId;

    @PersistenceContext
    EntityManager em;

    @Autowired
    private UserService sut;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        User user = Generator.generateUser();
        user.setName("Jmeno");
        user.setUsername("Username");
        em.persist(user);
        userId = user.getId();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        rawToken = Generator.generateRawAuthenticationToken(userDetails);
    }

    @Test
    public void persistUserAddsToDatabase(){
        //creating second user with same username
        User user = Generator.generateUser();
        user.setUsername("Username1");
        UserDto userDto = new UserDto(user);
        sut.persist(userDto);

        UserDto us = new UserDto(sut.findByUsername("Username1"));
        assertEquals(us.getName(), user.getName());

    }

    @Test
    public void addUserWithExistingNameThrows(){
        //creating second user with same username
        User user = Generator.generateUser();
        user.setUsername("Username");
        UserDto userDto = new UserDto(user);

        assertThrows(AlreadyExistsException.class, ()->sut.persist(userDto));
    }

    @Test
    public void findByUsernameThrowsNotExistingUserException(){
        assertThrows(NotFoundException.class,
                () -> sut.findByUsername("not exit"));
    }
}