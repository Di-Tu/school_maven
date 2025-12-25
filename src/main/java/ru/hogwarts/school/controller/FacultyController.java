package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable("id") Long id) {
        Faculty faculty = facultyService.findFacultyById(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> getAllFaculty() {
        Collection<Faculty> faculties = facultyService.findAllFaculties();
        if (faculties.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(faculties);
    }

    @GetMapping(params = "color")
    public ResponseEntity<Collection<Faculty>> getFacultyByColor(@RequestParam String color) {
        Collection<Faculty> faculties = facultyService.findFacultyByColor(color);
        if (faculties.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(faculties);
    }

    @PostMapping
    public Faculty addFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PutMapping
    public ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty faculty) {
        if (facultyService.findFacultyById(faculty.getId()) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(facultyService.updateFaculty(faculty));
    }

    @DeleteMapping("{id}")
    public void deleteFaculty(@PathVariable("id") Long id) {
        facultyService.deleteFaculty(id);
    }
}
