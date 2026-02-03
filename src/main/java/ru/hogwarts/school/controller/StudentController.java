package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public Student getStudentId(@PathVariable Long id) {
        return studentService.findStudentById(id);
    }

    @GetMapping("/age/{age}")
    public Collection<Student> getStudentsByAge(@PathVariable Integer age) {
        return studentService.findStudentsByAge(age);
    }

    @GetMapping("/age/range")
    public Collection<Student> getStudentsByAgeRange(@RequestParam Integer min, @RequestParam Integer max) {
        return studentService.findStudentsByAgeBetween(min, max);
    }

    @GetMapping
    public Collection<Student> getAllStudents() {
        return studentService.findAllStudents();
    }

    @GetMapping("/faculty/{id}")
    public Faculty getStudentFaculty(@PathVariable Long id) {
        return studentService.findFacultyByIdStudent(id);
    }

    @GetMapping("/count")
    public int countStudents() {
        return studentService.countStudents();
    };

    @GetMapping("/age/average")
    public double averageAgeStudents() {
        return studentService.averageAgeStudents();
    }

    @GetMapping("/last5")
    public Collection<Student> theLast5Students() {
        return studentService.theLast5Students();
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PutMapping
    public Student updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }
}
