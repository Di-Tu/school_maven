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
import ru.hogwarts.school.model.Student;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ru.hogwarts.school.repository.StudentRepository studentRepository;

    @AfterEach
    public void cleanup() {
        studentRepository.deleteAll();
    }

    private String baseUrl;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/student";
        testStudent = new Student("StudTest1", 17);
    }

    @Test
    void createStudent_shouldReturnCreatedStudent() {
        ResponseEntity<Student> response = restTemplate.postForEntity(baseUrl, testStudent, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("StudTest1");
        assertThat(response.getBody().getAge()).isEqualTo(17);
        assertThat(response.getBody().getId()).isPositive();
    }

    @Test
    void getStudentById_shouldReturnStudent() {
        Student created = restTemplate.postForObject(baseUrl, testStudent, Student.class);

        ResponseEntity<Student> response = restTemplate.getForEntity(baseUrl + "/" + created.getId(), Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(created.getId());
        assertThat(response.getBody().getName()).isEqualTo("StudTest1");
    }

    @Test
    void getStudentById_whenStudentNotFound_shouldReturn404() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/999999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Студент с id 999999 не найден");
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() {
        Student created = restTemplate.postForObject(baseUrl, testStudent, Student.class);
        created.setName("StudTest2");
        created.setAge(18);

        ResponseEntity<Student> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, new HttpEntity<>(created), Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("StudTest2");
        assertThat(response.getBody().getAge()).isEqualTo(18);
    }

    @Test
    void deleteStudent_shouldDeleteSuccessfully() {
        Student created = restTemplate.postForObject(baseUrl, testStudent, Student.class);

        restTemplate.delete(baseUrl + "/" + created.getId());

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/" + created.getId(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getStudentsByAge_shouldReturnFilteredStudents() {
        Student student1 = new Student("StudTest3", 18);
        Student student2 = new Student("StudTest4", 17);

        restTemplate.postForObject(baseUrl, student1, Student.class);
        restTemplate.postForObject(baseUrl, student2, Student.class);

        ResponseEntity<Collection<Student>> response = restTemplate.exchange(baseUrl + "/age/18", HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Student>>() {
        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().iterator().next().getName()).isEqualTo("StudTest3");
    }

    @Test
    void getStudentsByAgeRange_shouldReturnStudentsInRange() {
        Student student1 = new Student("StudTest5", 17);
        Student student2 = new Student("StudTest6", 16);
        Student student3 = new Student("StudTest7", 18);

        restTemplate.postForObject(baseUrl, student1, Student.class);
        restTemplate.postForObject(baseUrl, student2, Student.class);
        restTemplate.postForObject(baseUrl, student3, Student.class);

        ResponseEntity<Collection<Student>> response = restTemplate.exchange(baseUrl + "/age/range?min=17&max=18", HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Student>>() {
        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getAllStudents_shouldReturnAllStudents() {
        restTemplate.postForObject(baseUrl, new Student("StudTest8", 16), Student.class);
        restTemplate.postForObject(baseUrl, new Student("StudTest9", 19), Student.class);

        ResponseEntity<Collection<Student>> response = restTemplate.exchange(baseUrl, HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Student>>() {
        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
