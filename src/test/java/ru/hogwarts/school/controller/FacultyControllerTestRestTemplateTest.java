package ru.hogwarts.school.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ru.hogwarts.school.repository.FacultyRepository facultyRepository;

    @AfterEach
    public void cleanup() {
        facultyRepository.deleteAll();
    }

    private String baseUrl;
    private Faculty testFaculty;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/faculty";
        testFaculty = new Faculty("FacTest1", "ColTest1");
    }

    @Test
    void createFaculty_shouldReturnCreatedFaculty() {
        ResponseEntity<Faculty> response = restTemplate.postForEntity(baseUrl, testFaculty, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("FacTest1");
        assertThat(response.getBody().getColor()).isEqualTo("ColTest1");
        assertThat(response.getBody().getId()).isPositive();
    }

    @Test
    void getFacultyById_shouldReturnFaculty() {
        Faculty created = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(baseUrl + "/" + created.getId(), Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(created.getId());
        assertThat(response.getBody().getName()).isEqualTo("FacTest1");
    }

    @Test
    void getFacultyById_whenFacultyNotFound_shouldReturn404() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/999999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Факультет с id 999999 не найден");
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() {
        Faculty created = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);
        created.setName("FacTest2");
        created.setColor("ColTest2");

        ResponseEntity<Faculty> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, new HttpEntity<>(created), Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("FacTest2");
        assertThat(response.getBody().getColor()).isEqualTo("ColTest2");
    }

    @Test
    void deleteFaculty_shouldDeleteSuccessfully() {
        Faculty created = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        restTemplate.delete(baseUrl + "/" + created.getId());

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/" + created.getId(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void searchFacultyByNameOrColor_shouldReturnMatchingFaculties() {
        Faculty faculty1 = new Faculty("FacTest3", "ColTest3");
        Faculty faculty2 = new Faculty("FacTest4", "ColTest4");

        restTemplate.postForObject(baseUrl, faculty1, Faculty.class);
        restTemplate.postForObject(baseUrl, faculty2, Faculty.class);

        ResponseEntity<Collection<Faculty>> response = restTemplate.exchange(baseUrl + "/search/ColTest3", HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Faculty>>() {
        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().iterator().next().getName()).isEqualTo("FacTest3");
    }

    @Test
    void findFacultyByColor_shouldReturnFacultiesWithColor() {
        Faculty faculty1 = new Faculty("FacTest5", "ColTest5");
        Faculty faculty2 = new Faculty("FacTest6", "ColTest6");
        Faculty faculty3 = new Faculty("FacTest5An", "ColTest5");

        restTemplate.postForObject(baseUrl, faculty1, Faculty.class);
        restTemplate.postForObject(baseUrl, faculty2, Faculty.class);
        restTemplate.postForObject(baseUrl, faculty3, Faculty.class);

        ResponseEntity<Collection<Faculty>> response = restTemplate.exchange(baseUrl + "/search/color/ColTest5", HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Faculty>>() {
        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getAllFaculties_shouldReturnAllFaculties() {
        restTemplate.postForObject(baseUrl, new Faculty("FacTest3", "ColTest3"), Faculty.class);
        restTemplate.postForObject(baseUrl, new Faculty("FacTest4", "ColTest4"), Faculty.class);

        ResponseEntity<Collection<Faculty>> response = restTemplate.exchange(baseUrl, HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Faculty>>() {
        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }
}