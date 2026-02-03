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
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacultyService facultyService;

    private final Faculty testFaculty = new Faculty("FacTest1M", "ColTest1M");

    @Test
    void createFaculty_shouldReturnCreatedFaculty() throws Exception {
        testFaculty.setId(1L);
        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testFaculty))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("FacTest1M")).andExpect(jsonPath("$.color").value("ColTest1M"));
    }

    @Test
    void getFacultyById_shouldReturnFaculty() throws Exception {
        testFaculty.setId(1L);
        when(facultyService.findFacultyById(1L)).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("FacTest1M"));
    }

    @Test
    void getFacultyById_whenFacultyNotFound_shouldReturn404() throws Exception {
        when(facultyService.findFacultyById(999L)).thenThrow(new ru.hogwarts.school.exception.FacultyNotFoundException("Факультет не найден"));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/999")).andExpect(status().isNotFound());
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() throws Exception {
        testFaculty.setId(1L);
        testFaculty.setName("FacTest2M");
        testFaculty.setColor("ColTest2M");
        when(facultyService.updateFaculty(any(Faculty.class))).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testFaculty))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("FacTest2M")).andExpect(jsonPath("$.color").value("ColTest2M"));
    }

    @Test
    void deleteFaculty_shouldReturnSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/1")).andExpect(status().isOk());
    }

    @Test
    void searchFacultyByNameOrColor_shouldReturnMatchingFaculties() throws Exception {
        Faculty faculty1 = new Faculty("FacTest3M", "ColTest3M");
        Faculty faculty2 = new Faculty("FacTest4M", "ColTest4M");
        faculty1.setId(1L);
        faculty2.setId(2L);

        Collection<Faculty> faculties = List.of(faculty1);
        when(facultyService.findFacultyByNameOrColor("ColTest3M")).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/search/ColTest3M")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("FacTest3M")).andExpect(jsonPath("$[0].color").value("ColTest3M"));
    }

    @Test
    void findFacultyByColor_shouldReturnFacultiesWithColor() throws Exception {
        Faculty faculty1 = new Faculty("FacTest5M", "ColTest5M");
        Faculty faculty2 = new Faculty("FacTest5MAn", "ColTest5M");
        faculty1.setId(1L);
        faculty2.setId(2L);

        Collection<Faculty> faculties = List.of(faculty1, faculty2);
        when(facultyService.findFacultyByColor("ColTest5M")).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/search/color/ColTest5M")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllFaculties_shouldReturnAllFaculties() throws Exception {
        Faculty faculty1 = new Faculty("FacTest6M", "ColTest6M");
        Faculty faculty2 = new Faculty("FacTest7M", "ColTest7M");
        faculty1.setId(1L);
        faculty2.setId(2L);

        Collection<Faculty> faculties = List.of(faculty1, faculty2);
        when(facultyService.findAllFaculties()).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getStudentsByFaculty_shouldReturnStudents() throws Exception {
        Student student1 = new Student("StudTest1M", 17);
        Student student2 = new Student("StudTest2M", 18);
        student1.setId(1L);
        student2.setId(2L);

        Collection<Student> students = List.of(student1, student2);
        when(facultyService.findStudentsByFacultyById(1L)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/students/1")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].name").value("StudTest1M")).andExpect(jsonPath("$[1].name").value("StudTest2M"));
    }
}