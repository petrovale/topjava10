package ru.javawebinar.topjava.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DbPopulator;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

/**
 * Created by user on 30.04.2017.
 */
@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class MealServiceTest {
    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Autowired
    private DbPopulator dbPopulator;

    @Before
    public void setUp() throws Exception {
        dbPopulator.execute();
    }

    @Test
    public void testDelete() throws Exception {
        service.delete(USER_MEAL1_ID, USER_ID);
        MATCHER.assertCollectionEquals(Arrays.asList(USER_MEAL6, USER_MEAL5, USER_MEAL4, USER_MEAL3, USER_MEAL2), service.getAll(USER_ID));
    }

    @Test(expected = NotFoundException.class)
    public void testNotFoundDelete() throws Exception {
        service.delete(USER_MEAL1_ID, 1);
    }

    @Test
    public void testSave() throws Exception {
        Meal created = MealTestData.getCreated();
        service.save(created, USER_ID);
        MATCHER.assertCollectionEquals(Arrays.asList(created, USER_MEAL6, USER_MEAL5, USER_MEAL4, USER_MEAL3, USER_MEAL2, USER_MEAL1), service.getAll(USER_ID));
    }

    @Test
    public void testGet() throws Exception {
        Meal meal = service.get(ADMIN_MEAL_ID,ADMIN_ID);
        MATCHER.assertEquals(ADMIN_MEAL1, meal);
    }

    @Test(expected = NotFoundException.class)
    public void testNotFoundGet() throws Exception {
        service.get(ADMIN_MEAL_ID,1);
    }

    @Test
    public void testUpdate() throws Exception {
        Meal updated = MealTestData.getUpdated();
        service.update(updated, USER_ID);
        MATCHER.assertEquals(updated, service.get(USER_MEAL1_ID, USER_ID));
    }

    @Test(expected = NotFoundException.class)
    public void testNotFoundUpdate() throws Exception {
        service.update(USER_MEAL1, ADMIN_ID);
    }

    @Test
    public void testGetAll() throws Exception {
        MATCHER.assertCollectionEquals(MEALS, service.getAll(USER_ID));
    }

    @Test
    public void getBetweenDates() throws Exception {
        MATCHER.assertCollectionEquals(Arrays.asList(USER_MEAL3, USER_MEAL2, USER_MEAL1),
                service.getBetweenDates(LocalDate.of(2015, Month.MAY, 30), LocalDate.of(2015, Month.MAY, 30), USER_ID));
    }
}