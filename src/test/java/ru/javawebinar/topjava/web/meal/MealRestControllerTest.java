package ru.javawebinar.topjava.web.meal;

import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.View;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JacksonObjectMapper;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.time.Month;
import java.util.Arrays;

import static java.time.LocalDateTime.of;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.MealTestData.MATCHER;
import static ru.javawebinar.topjava.TestUtil.userHttpBasic;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.model.BaseEntity.START_SEQ;

public class MealRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MealRestController.REST_URL + '/';

    @Autowired
    private MealService service;

    @Test
    public void testGet() throws Exception {
        mockMvc.perform(get(REST_URL + ADMIN_MEAL_ID)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER.contentMatcher(ADMIN_MEAL1));
    }

    @Test
    public void testGetUnauth() throws Exception {
        mockMvc.perform(get(REST_URL + MEAL1_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetNotFound() throws Exception {
        mockMvc.perform(get(REST_URL + ADMIN_MEAL_ID)
                .with(userHttpBasic(USER)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testDeleteNotFound() throws Exception {
        mockMvc.perform(delete(REST_URL + ADMIN_MEAL_ID)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL + MEAL1_ID)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk());
        MATCHER.assertCollectionEquals(Arrays.asList(MEAL6, MEAL5, MEAL4, MEAL3, MEAL2), service.getAll(START_SEQ));
    }

    @Test
    public void testUpdate() throws Exception {
        Meal updated = getUpdated();

        mockMvc.perform(put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated))
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk());

        assertEquals(updated, service.get(MEAL1_ID, START_SEQ));
    }

    @Test
    public void testInvalidUpdate() throws Exception {
        Meal updatedInvalid = new Meal(MEAL1_ID, null, null, 300);

        mockMvc.perform(put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedInvalid))
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void testCreateDuplicateDateTime() throws Exception {
        Meal created = new Meal(null, of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500);
        mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created))
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void testCreate() throws Exception {
        Meal created = getCreated();
        ResultActions action = mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created))
                .with(userHttpBasic(ADMIN)));

        Meal returned = MATCHER.fromJsonAction(action);
        created.setId(returned.getId());

        MATCHER.assertEquals(created, returned);
        MATCHER.assertCollectionEquals(Arrays.asList(ADMIN_MEAL2, created, ADMIN_MEAL1), service.getAll(ADMIN_ID));
    }

    @Test
    public void testInvalidCreate() throws Exception {
        Meal created = new Meal(null, null, "fthfh", 300);
        mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(created))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MATCHER_WITH_EXCEED.contentListMatcher(MealsUtil.getWithExceeded(MEALS, USER.getCaloriesPerDay())));
    }

    @Test
    public void testFilter() throws Exception {
        mockMvc.perform(get(REST_URL + "filter")
                .param("startDate", "2015-05-30").param("startTime", "07:00")
                .param("endDate", "2015-05-31").param("endTime", "11:00")
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MATCHER_WITH_EXCEED.contentListMatcher(
                        MealsUtil.createWithExceed(MEAL4, true),
                        MealsUtil.createWithExceed(MEAL1, false)));
    }

    @Test
    public void testFilterAll() throws Exception {
        mockMvc.perform(get(REST_URL + "filter?startDate=&endTime=")
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MATCHER_WITH_EXCEED.contentListMatcher(
                        MealsUtil.getWithExceeded(Arrays.asList(MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1), USER.getCaloriesPerDay())));
    }
}
