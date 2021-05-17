package cz.cvut.fel.tmr.service;

import cz.cvut.fel.tmr.dto.trackedtime.TrackedTimeDto;
import cz.cvut.fel.tmr.environment.Generator;
import cz.cvut.fel.tmr.exception.AlreadyExistsException;
import cz.cvut.fel.tmr.model.Task;
import cz.cvut.fel.tmr.model.User;
import org.junit.jupiter.api.Assertions;
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

import java.time.LocalDateTime;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class TrackedTimeServiceTest {
    private String rawToken;

    private Long userId;

    @PersistenceContext
    EntityManager em;

    @Autowired
    private TrackedTimeService sut;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        User user = Generator.generateUser();
        em.persist(user);
        userId = user.getId();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        rawToken = Generator.generateRawAuthenticationToken(userDetails);
    }

    @Test
    public void addTrackedTimeWhileTrackedThrowsException(){
        TrackedTimeDto trackedTimeDto = Generator.generateTrackedTimeDto();
        trackedTimeDto.setUserId(userId);
        Task task = Generator.generateTask();
        em.persist(task);
        trackedTimeDto.setTaskId(task.getId());

        //adding tracked time
        sut.addTrackedTime(trackedTimeDto);

        //generating second trackedTime with the same user
        TrackedTimeDto trackedTimeDto2 = Generator.generateTrackedTimeDto();
        trackedTimeDto2.setUserId(userId);
        trackedTimeDto2.setTaskId(task.getId());

        assertThrows(AlreadyExistsException.class, () -> sut.addTrackedTime(trackedTimeDto2));
    }

    @Test
    public void addTrackedTimeAfterTrackedEndedDoesntThrowsException(){
        TrackedTimeDto trackedTimeDto = Generator.generateTrackedTimeDto();
        trackedTimeDto.setUserId(userId);
        Task task = Generator.generateTask();
        em.persist(task);
        trackedTimeDto.setTaskId(task.getId());

        //adding tracked time
        Long id = sut.addTrackedTime(trackedTimeDto);

        //setting end time to the first tracked time
        trackedTimeDto.setEndTime(LocalDateTime.now());
        sut.update(id, trackedTimeDto);

        //generating second trackedTime with the same user
        TrackedTimeDto trackedTimeDto2 = Generator.generateTrackedTimeDto();
        trackedTimeDto2.setUserId(userId);
        trackedTimeDto2.setTaskId(task.getId());

        Assertions.assertDoesNotThrow(() -> sut.addTrackedTime(trackedTimeDto2));
    }

    @Test
    public void getTaskTrackedTimeBriefReturnsSumOfStoppedTrackedTimesForOneUser(){
        TrackedTimeDto trackedTimeDto = Generator.generateTrackedTimeDto();
        trackedTimeDto.setUserId(userId);
        Task task = Generator.generateTask();
        em.persist(task);
        trackedTimeDto.setTaskId(task.getId());

        //adding tracked time
        Long id = sut.addTrackedTime(trackedTimeDto);

        //returns 0 when there are no stopped tracked times
        assertEquals(0, sut.getTaskTrackedTimeBrief(task.getId()).size());

        //setting end time to the first tracked time
        trackedTimeDto.setEndTime(LocalDateTime.now());
        sut.update(id, trackedTimeDto);
        assertEquals(1, sut.getTaskTrackedTimeBrief(task.getId()).size());

        //generating second trackedTime with the same user
        TrackedTimeDto trackedTimeDto2 = Generator.generateTrackedTimeDto();
        trackedTimeDto2.setUserId(userId);
        trackedTimeDto2.setTaskId(task.getId());

        //adding second tracked time
        Long id2 = sut.addTrackedTime(trackedTimeDto2);

        //setting end time to the second tracked time
        trackedTimeDto2.setEndTime(LocalDateTime.now());
        sut.update(id2, trackedTimeDto2);

        //task still returns 1 tracked time
        assertEquals(1, sut.getTaskTrackedTimeBrief(task.getId()).size());
    }
}
