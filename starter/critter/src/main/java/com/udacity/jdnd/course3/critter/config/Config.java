package com.udacity.jdnd.course3.critter.config;

import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;

public class Config {

    private Config() {
        // non-instantiatable
    }

    public static final String EMPLOYEE_ID = "employeeId";
    public static final String PET_ID = "petId";
    public static final String CUSTOMER_ID = "customerId";
    public static final String OWNER_ID = "ownerId";
    public static final String SCHEDULE_ID = "scheduleId";

    public static final String USER_URL = "/user";
    public static final String EMPLOYEE_URL = "/employee";
    public static final String CUSTOMER_URL = "/customer";
    public static final String OWNER_URL = "/owner";
    public static final String PET_URL = "/pet";
    public static final String SCHEDULE_URL = "/schedule";
    public static final String EMPLOYEE_ID_PATTERN = "{" + EMPLOYEE_ID + "}";
    public static final String PET_ID_PATTERN = "{" + PET_ID + "}";
    public static final String CUSTOMER_ID_PATTERN = "{" + CUSTOMER_ID + "}";
    public static final String OWNER_ID_PATTERN = "{" + OWNER_ID + "}";
    public static final String SCHEDULE_ID_PATTERN = "{" + SCHEDULE_ID + "}";
    public static final String EMPLOYEE_ID_URL = "/" + EMPLOYEE_ID_PATTERN;
    public static final String PET_ID_URL = "/" + PET_ID_PATTERN;
    public static final String CUSTOMER_ID_URL = "/" + CUSTOMER_ID_PATTERN;
    public static final String OWNER_ID_URL = "/" + OWNER_ID_PATTERN;
    public static final String SCHEDULE_ID_URL = "/" + SCHEDULE_ID_PATTERN;

    // Save Customer
    public static final String CUSTOMER_POST_URL = CUSTOMER_URL;
    // Get All Customers
    public static final String CUSTOMER_GET_URL = CUSTOMER_POST_URL;
    // Get Customer by id
    public static final String CUSTOMER_GET_ID_URL = CUSTOMER_GET_URL + CUSTOMER_ID_URL;
    // Get Owner By Pet
    public static final String OWNER_GET_BY_PET_URL = OWNER_URL + PET_URL + PET_ID_URL;
    public static final String CUSTOMER_GET_BY_PET_URL = CUSTOMER_GET_URL + PET_URL + PET_ID_URL;

    // Save Employee
    public static final String EMPLOYEE_POST_URL = EMPLOYEE_URL;
    public static final String EMPLOYEE_GET_URL = EMPLOYEE_URL;
    public static final String EMPLOYEE_GET_BY_ID_URL = EMPLOYEE_URL + EMPLOYEE_ID_URL;
    // Add/Create Employee Schedule
    public static final String EMPLOYEE_PUT_BY_ID_URL = EMPLOYEE_GET_BY_ID_URL;
    // Check Availability
    public static final String EMPLOYEE_AVAILABILITY_URL = EMPLOYEE_GET_URL + "/availability";

    // Create Pet
    public static final String PET_POST_URL = PET_URL;
    // Get All Pets
    public static final String PET_GET_URL = PET_URL;
    // Save Pet
    public static final String PET_PUT_URL = PET_URL + PET_ID_URL;
    // Delete Pet
    public static final String PET_DELETE_URL = PET_PUT_URL;
    // Get Pets By Owner
    public static final String PET_GET_BY_OWNER_URL = OWNER_URL + OWNER_ID_URL;

    public static final String SCHEDULE_POST_URL = SCHEDULE_URL;
    public static final String SCHEDULE_GET_URL = SCHEDULE_URL;
    // Get Schedule
    public static final String GET_SCHEDULE_BY_ID_URL = SCHEDULE_ID_URL;
    // Find Schedule By Pet
    public static final String GET_SCHEDULE_BY_PET_URL = PET_URL + PET_ID_URL;
    // Find Schedule By Employee
    public static final String GET_SCHEDULE_BY_EMPLOYEE_URL = EMPLOYEE_URL + EMPLOYEE_ID_URL;
    // Find Schedule By Owner
    public static final String GET_SCHEDULE_BY_CUSTOMER_URL = CUSTOMER_URL + CUSTOMER_ID_URL;
    public static final String GET_SCHEDULE_BY_OWNER_URL = OWNER_URL + OWNER_ID_URL;



    public static String getUrl(String url, Map<String, Object> query) {
        StringBuilder sb = new StringBuilder(
                url.endsWith("/") ? url.substring(0, url.length() - 1) : url);
        boolean start = true;
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            if (start) {
                sb.append('?');
                start = false;
            } else {
                sb.append('&');
            }
            sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue().toString());
        }
        return sb.toString();
    }

    public static String getUrl(String baseUrl, List<String> parts, Map<String, Object> query, List<Pair<String, Long>> idReplacement) {
        String url = baseUrl + String.join("", parts);
        if (idReplacement != null) {
            for (Pair<String, Long> replacement : idReplacement) {
                if (url.contains(replacement.getFirst())) {
                    url = url.replace(replacement.getFirst(), replacement.getSecond().toString());
                }
            }
        }
        return getUrl(url, query);
    }

    public static String getUrl(String baseUrl, List<String> parts) {
        return getUrl(baseUrl, parts, Map.of(), List.of());
    }
}
