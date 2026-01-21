package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.exception.BadRequestException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty findFacultyById(Long id) {
        return facultyRepository.findById(id).orElseThrow(() -> new NotFoundException("Факультет с id " + id + " не найден."));
    }

    public Collection<Faculty> findAllFaculties() {
        return facultyRepository.findAll();
    }

    public Collection<Faculty> findFacultyByColor(String color) {
        return facultyRepository.findFacultyByColor(color);
    }

    public Faculty updateFaculty(Faculty faculty) {
        if (!facultyRepository.existsById(faculty.getId())) {
            throw new BadRequestException("Факультет с id: " + faculty.getId() + " не найден, обновление невозможно.");
        }
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id) {
        if (!facultyRepository.existsById(id)) {
            throw new NotFoundException("Факультет с id " + id + " не найден.");
        }
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findFacultyByNameOrColor(String find) {
        return facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(find, find);
    }

    public Collection<Student> findStudentsByFacultyById(Long id) {
        if (!facultyRepository.existsById(id)) {
            throw new NotFoundException("Факультет с id " + id + " не найден.");
        }
        return studentRepository.findByFacultyId(id);
    }
}
