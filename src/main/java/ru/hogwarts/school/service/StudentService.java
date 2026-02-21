package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.BadRequestException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student findStudentById(Long id) {
        logger.info("Was invoked method for find student by id");
        return studentRepository.findById(id).orElseThrow(() -> {
            logger.error("There is no student with id = " + id);
            return new StudentNotFoundException("Студент с id " + id + " не найден.");
        });
    }

    public Collection<Student> findStudentsByAge(int age) {
        logger.info("Was invoked method for find student by age");
        return studentRepository.findStudentsByAge(age);
    }

    public Collection<Student> findAllStudents() {
        logger.info("Was invoked method for find all students");
        return studentRepository.findAll();
    }

    public Student updateStudent(Student student) {
        logger.info("Was invoked method for update student");
        if (!studentRepository.existsById(student.getId())) {
            logger.error("A student with id " + student.getId() + " does not exist.");
            throw new StudentNotFoundException("Студент с id: " + student.getId() + " не существует, обновление невозможно.");
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        logger.info("Was invoked method for delete student");
        if (!studentRepository.existsById(id)) {
            logger.error("A student with id " + id + " does not exist.");
            throw new StudentNotFoundException("Студент с id " + id + " не найден.");
        }
        studentRepository.deleteById(id);
    }

    public Collection<Student> findStudentsByAgeBetween(int min, int max) {
        logger.info("Was invoked method for find student by age between");
        logger.debug("Finding students in age range: min={}, max={}", min, max);
        if (min > max) {
            logger.error("The minimum age cannot be greater than the maximum age.");
            throw new BadRequestException("Минимальный возраст не может быть больше максимального возраста.");
        }
        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty findFacultyByIdStudent(Long id) {
        logger.info("Was invoked method for find faculty by id student");
        if (!studentRepository.existsById(id)) {
            logger.error("A student with id " + id + " does not exist.");
            throw new StudentNotFoundException("Студент с id " + id + " не найден. Введите корректный id студента.");
        }
        Faculty faculty = studentRepository.findFacultyByStudentId(id);
        if (faculty == null) {
            logger.error("The student with id " + id + " does not have a faculty.");
            throw new FacultyNotFoundException("У студента с id: " + id + " нет факультета.");
        }
        return faculty;
    }

    public int countStudents() {
        logger.info("Was invoked method for count students");
        return studentRepository.countStudents();
    }

    public double averageAgeStudents() {
        logger.info("Was invoked method for average age students");
        Double avg = studentRepository.averageAgeStudents();
        if (avg == null) {
            logger.warn("Average age requested, but student list is empty in database");
            return 0.0;
        }
        return avg;
    }

    public Collection<Student> theLast5Students() {
        logger.info("Was invoked method for the last 5 Students");
        return studentRepository.theLast5Students();
    }

    public Map<Faculty, Set<String>> groupingByFaculty() {
        logger.info("Was invoked method for grouping by faculty");
        List<Student> allStudents = studentRepository.findAll();
        Map<Faculty, Set<String>> studentsByFaculties = allStudents.stream()
                .parallel()
                .filter(s -> s.getName().length() > 5)
                .collect(Collectors.groupingBy(Student::getFaculty, Collectors.mapping(Student::getName, Collectors.toSet())));
        return studentsByFaculties;
    }

    public Collection<String> studentsNamesStartingA() {
        logger.info("Was invoked method for studentsNamesStartingA");
        return studentRepository.findAll().stream()
                .map(s -> s.getName().toUpperCase())
                .filter(name -> name.startsWith("A"))
                .sorted()
                .toList();
    }

    public double studentsAgeMiddle() {
        logger.info("Was invoked method for studentsAgeMiddle");
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }
}