package com.test_task.rest_service;

import com.test_task.rest_service.exceptions.ErrorMessage;
import com.test_task.rest_service.repositories.UserRepository;
import com.test_task.rest_service.services.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CacheManager cacheManager;

    @Value("${user.age.min}")
    private int minimumAge;

    @Test
    public void createController() throws Exception {
        postUser(createUser());
    }

    @Test
    public void createController_Duplicate() throws Exception {
        postUser(createUser());
        mvc.perform(post("/user")
                        .content(createUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value(ErrorMessage.EMAIL_ALREADY_USED.getMessage()));
    }

    @Test
    public void createController_MissedField() throws Exception {
        JSONObject user = new JSONObject();
        user.put("email", "test@gmail.com");
        user.put("firstName", "firstName1");
        user.put("birthDate", "1991-12-11");
        user.put("phoneNumber", "+380111111111");
        user.put("address", "address");
        mvc.perform(post("/user")
                        .content(user.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Last name is mandatory"));
    }

    @Test
    public void createController_SmallAge() throws Exception {
        JSONObject user = new JSONObject();
        user.put("email", "test1@gmail.com");
        user.put("firstName", "firstName1");
        user.put("lastName", "lastName1");
        user.put("birthDate", LocalDate.now().minusYears(minimumAge - 1).toString());
        user.put("phoneNumber", "+380111111112");
        user.put("address", "Kyiv");
        mvc.perform(post("/user")
                        .content(user.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value(ErrorMessage.SMALL_AGE.getMessage()));
    }

    @Test
    public void getController() throws Exception {
        String context = postUser(createUser()).getResponse().getContentAsString();
        JSONObject user = new JSONObject(context);
        mvc.perform(get("/user/" + user.get("id")).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void getController_NotFound() throws Exception {
        mvc.perform(get("/user/" + 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value(ErrorMessage.USER_NOT_EXIST.getMessage()));
    }

    @Test
    public void getControllerWithRange() throws Exception {
        postUser(createUser()).getResponse().getContentAsString();
        postUser(createUser(1)).getResponse().getContentAsString();
        postUser(createUser(2)).getResponse().getContentAsString();
        postUser(createUser(3)).getResponse().getContentAsString();
        mvc.perform(get(String.format("/user?from=%s&to=%s",
                        LocalDate.of(1991, 12, 9),
                        LocalDate.of(1991, 12, 12)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(2)));
        mvc.perform(get(String.format("/user?from=%s&to=%s",
                        LocalDate.of(1991, 12, 9),
                        LocalDate.of(1991, 12, 14)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(4)));
    }

    @Test
    public void getControllerWithRange_BadRange() throws Exception {
        mvc.perform(get(String.format("/user?from=%s&to=%s",
                        LocalDate.of(1991, 12, 12),
                        LocalDate.of(1991, 12, 9)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value(ErrorMessage.INVALID_DATE.getMessage()));
    }

    @Test
    public void getControllerWithRange_MissedRange() throws Exception {
        mvc.perform(get("/user")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Required parameter 'from' is not present."));
        mvc.perform(get("/user?from=2000-12-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Required parameter 'to' is not present."));
    }

    @Test
    public void getControllerWithRange_InvalidRange() throws Exception {
        mvc.perform(get("/user?from=2000-13-01&to=2000-12-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Date 2000-13-01 is incorrect"));
        mvc.perform(get("/user?from=2000-12-01&to=2000-13-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Date 2000-13-01 is incorrect"));
    }

    @Test
    public void UpdateController() throws Exception {
        String context = postUser(createUser()).getResponse().getContentAsString();
        JSONObject user = new JSONObject(context);
        user.put("email", "test1@gmail.com");
        user.put("firstName", "firstName1");
        user.put("lastName", "lastName1");
        user.put("birthDate", "1991-12-12");
        user.put("phoneNumber", "+380111111112");
        user.put("address", "Kyiv");
        mvc.perform(put("/user/" + user.get("id")).content(user.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(get("/user/" + user.get("id")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.get("email")))
                .andExpect(jsonPath("$.firstName").value(user.get("firstName")))
                .andExpect(jsonPath("$.lastName").value(user.get("lastName")))
                .andExpect(jsonPath("$.birthDate").value(user.get("birthDate")))
                .andExpect(jsonPath("$.phoneNumber").value(user.get("phoneNumber")))
                .andExpect(jsonPath("$.address").value(user.get("address")));
    }

    @Test
    public void UpdateController_WrongId() throws Exception {
        String context = postUser(createUser()).getResponse().getContentAsString();
        JSONObject user = new JSONObject(context);
        user.put("email", "test1@gmail.com");
        user.put("firstName", "firstName1");
        user.put("lastName", "lastName1");
        user.put("birthDate", "1991-12-12");
        user.put("phoneNumber", "+380111111112");
        user.put("address", "Kyiv");
        mvc.perform(put("/user/" + user.get("id") + 1).content(user.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value(ErrorMessage.USER_NOT_EXIST.getMessage()));
    }

    @Test
    public void UpdateController_EmailDuplicate() throws Exception {
        String context = postUser(createUser()).getResponse().getContentAsString();
        postUser(createUser(2));
        JSONObject user = new JSONObject(context);
        user.put("email", "test2@gmail.com");
        user.put("firstName", "firstName1");
        user.put("lastName", "lastName1");
        user.put("birthDate", "1991-12-12");
        user.put("phoneNumber", "+380111111112");
        user.put("address", "Kyiv");
        mvc.perform(put("/user/" + user.get("id")).content(user.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value(ErrorMessage.EMAIL_ALREADY_USED.getMessage()));

    }

    @Test
    public void partialUpdateController() throws Exception {
        String context = postUser(createUser()).getResponse().getContentAsString();
        JSONObject user = new JSONObject(context);
        user.put("email", "test1@gmail.com");
        user.put("firstName", "firstName1");
        user.put("address", "Kyiv");
        mvc.perform(patch("/user/" + user.get("id")).content(user.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mvc.perform(get("/user/" + user.get("id")).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.get("email")))
                .andExpect(jsonPath("$.firstName").value(user.get("firstName")))
                .andExpect(jsonPath("$.lastName").value(user.get("lastName")))
                .andExpect(jsonPath("$.birthDate").value(user.get("birthDate")))
                .andExpect(jsonPath("$.phoneNumber").value(user.get("phoneNumber")))
                .andExpect(jsonPath("$.address").value(user.get("address")));
    }

    @Test
    public void partialUpdateController_WrongId() throws Exception {
        String context = postUser(createUser()).getResponse().getContentAsString();
        JSONObject user = new JSONObject(context);
        user.put("email", "test1@gmail.com");
        user.put("firstName", "firstName1");
        user.put("address", "Kyiv");
        mvc.perform(patch("/user/" + user.get("id") + 1).content(user.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value(ErrorMessage.USER_NOT_EXIST.getMessage()));
    }

    @Test
    public void partialUpdateController_EmailDuplicate() throws Exception {
        String context = postUser(createUser()).getResponse().getContentAsString();
        postUser(createUser(2));
        JSONObject user = new JSONObject(context);
        user.put("email", "test2@gmail.com");
        user.put("firstName", "firstName1");
        user.put("address", "Kyiv");
        mvc.perform(patch("/user/" + user.get("id")).content(user.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value(ErrorMessage.EMAIL_ALREADY_USED.getMessage()));
    }

    @Test
    public void deleteController() throws Exception {
        String context = postUser(createUser()).getResponse().getContentAsString();
        JSONObject user = new JSONObject(context);
        mvc.perform(delete("/user/" + user.get("id")).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
        mvc.perform(delete("/user/" + user.get("id")).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void deleteController_NotFound() throws Exception {
        mvc.perform(delete("/user/" + 1).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void cacheChecking() throws Exception {
        String context = postUser(createUser()).getResponse().getContentAsString();
        JSONObject user = new JSONObject(context);
        mvc.perform(get("/user/" + user.get("id")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        userRepository.deleteById(Long.parseLong(user.get("id").toString()));
        mvc.perform(get("/user/" + user.get("id")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        cacheManager.getCache("user").evict(user.get("id"));
        mvc.perform(get("/user/" + user.get("id")).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private MvcResult postUser(String user) throws Exception {
        return mvc.perform(post("/user")
                        .content(user)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();
    }

    private String createUser() throws JSONException {
        return createUser(0);
    }

    private String createUser(int i) throws JSONException {
        JSONObject user = new JSONObject();
        user.put("email", "test" + i + "@gmail.com");
        user.put("firstName", "firstName");
        user.put("lastName", "lastName");
        user.put("birthDate", "1991-12-1" + i);
        user.put("phoneNumber", "+380111111111");
        user.put("address", "address");
        return user.toString();
    }

    @AfterEach
    private void cleanDatabase() {
        cacheManager.getCache("user").clear();
        userRepository.deleteAll();
    }
}
