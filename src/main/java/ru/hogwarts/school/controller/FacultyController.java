package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/{id}")
    public Faculty getFacultyId(@PathVariable Long id) {
        return facultyService.findFacultyById(id);
    }

    @GetMapping("/search/{query}")
    public Collection<Faculty> searchFacultyByNameOrColor(@PathVariable String query) {
        return facultyService.findFacultyByNameOrColor(query);
    }

    @GetMapping("/search/color/{color}")
    public Collection<Faculty> findFacultyByColor(@PathVariable String color) {
        return facultyService.findFacultyByColor(color);
    }

    @GetMapping
    public Collection<Faculty> getFacultiesAll() {
        return facultyService.findAllFaculties();
    }

    @GetMapping("/students/{id}")
    public Collection<Student> getStudentsByFaculty(@PathVariable Long id) {
        return facultyService.findStudentsByFacultyById(id);
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PutMapping
    public Faculty updateFaculty(@RequestBody Faculty faculty) {
        return facultyService.updateFaculty(faculty);
    }

    @DeleteMapping("/{id}")
    public void deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
    }
}