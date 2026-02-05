package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.BadRequestException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student findStudentById(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Студент с id " + id + " не найден."));
    }

    public Collection<Student> findStudentsByAge(int age) {
        return studentRepository.findStudentsByAge(age);
    }

    public Collection<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    public Student updateStudent(Student student) {
        if (!studentRepository.existsById(student.getId())) {
            throw new StudentNotFoundException("Студент с id: " + student.getId() + " не существует, обновление невозможно.");
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Студент с id " + id + " не найден.");
        }
        studentRepository.deleteById(id);
    }

    public Collection<Student> findStudentsByAgeBetween(int min, int max) {
        if (min > max) {
            throw new BadRequestException("Минимальный возраст не может быть больше максимального возраста.");
        }
        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty findFacultyByIdStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Студент с id " + id + " не найден. Введите корректный id студента.");
        }
        Faculty faculty = studentRepository.findFacultyByStudentId(id);
        if (faculty == null) {
            throw new FacultyNotFoundException("У студента с id: " + id + " нет факультета.");
        }
        return faculty;
    }

    public int countStudents() {
        return studentRepository.countStudents();
    }

    public double averageAgeStudents() {
        Double avg = studentRepository.averageAgeStudents();
        return (avg != null) ? avg : 0.0;
    }

    public Collection<Student> theLast5Students() {
        return studentRepository.theLast5Students();
    }
}