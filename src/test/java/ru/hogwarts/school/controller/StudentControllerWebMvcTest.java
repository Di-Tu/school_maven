package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    private final Student testStudent = new Student("StudTest1M", 17);
    private final Faculty testFaculty = new Faculty("FacTest1M", "ColTest1M");

    @Test
    void createStudent_shouldReturnCreatedStudent() throws Exception {
        testStudent.setId(1L);
        when(studentService.createStudent(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(MockMvcRequestBuilders.post("/student").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testStudent))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("StudTest1M")).andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void getStudentById_shouldReturnStudent() throws Exception {
        testStudent.setId(1L);
        when(studentService.findStudentById(1L)).thenReturn(testStudent);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("StudTest1M"));
    }

    @Test
    void getStudentById_whenStudentNotFound_shouldReturn404() throws Exception {
        when(studentService.findStudentById(999L)).thenThrow(new ru.hogwarts.school.exception.StudentNotFoundException("Студент не найден"));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/999")).andExpect(status().isNotFound());
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() throws Exception {
        testStudent.setId(1L);
        testStudent.setName("StudTest2M");
        when(studentService.updateStudent(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(MockMvcRequestBuilders.put("/student").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testStudent))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("StudTest2M"));
    }

    @Test
    void deleteStudent_shouldReturnSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/student/1")).andExpect(status().isOk());
    }

    @Test
    void getStudentsByAge_shouldReturnFilteredStudents() throws Exception {
        Student student1 = new Student("StudTest3M", 18);
        Student student2 = new Student("StudTest4M", 17);
        student1.setId(1L);
        student2.setId(2L);

        Collection<Student> students = List.of(student1);
        when(studentService.findStudentsByAge(18)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/age/18")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("StudTest3M")).andExpect(jsonPath("$[0].age").value(18));
    }

    @Test
    void getStudentsByAgeRange_shouldReturnStudentsInRange() throws Exception {
        Student student1 = new Student("StudTest5M", 17);
        Student student2 = new Student("StudTest6M", 18);
        student1.setId(1L);
        student2.setId(2L);

        Collection<Student> students = List.of(student1, student2);
        when(studentService.findStudentsByAgeBetween(17, 18)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/age/range").param("min", "17").param("max", "18")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("StudTest5M")).andExpect(jsonPath("$[1].name").value("StudTest6M"));
    }

    @Test
    void getAllStudents_shouldReturnAllStudents() throws Exception {
        Student student1 = new Student("StudTest7M", 17);
        Student student2 = new Student("StudTest8M", 18);
        student1.setId(1L);
        student2.setId(2L);

        Collection<Student> students = List.of(student1, student2);
        when(studentService.findAllStudents()).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getStudentFaculty_shouldReturnFaculty() throws Exception {
        testFaculty.setId(1L);
        when(studentService.findFacultyByIdStudent(1L)).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/faculty/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("FacTest1M")).andExpect(jsonPath("$.color").value("ColTest1M"));
    }
}