package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyDuplicateException;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.FacultyValidationException;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for creating a new faculty");
        return facultyRepository.save(faculty);
    }

    public Faculty findFacultyById(Long id) {
        logger.info("Was invoked method for finding a faculty by id");
        return facultyRepository.findById(id).orElseThrow(() -> {
            logger.error("Faculty with id {} was not found", id);
            return new NotFoundException("Факультет с id " + id + " не найден.");
        });
    }

    public Collection<Faculty> findAllFaculties() {
        logger.info("Was invoked method for finding all faculties");
        return facultyRepository.findAll();
    }

    public Collection<Faculty> findFacultyByColor(String color) {
        logger.info("Was invoked method for finding all faculties by color");
        return facultyRepository.findFacultyByColor(color);
    }

    public Faculty updateFaculty(Faculty faculty) {
        logger.info("Was invoked method for updating faculty");
        if (!facultyRepository.existsById(faculty.getId())) {
            logger.error("Faculty with id {} not found.", faculty.getId());
            throw new FacultyNotFoundException("Факультет с id: " + faculty.getId() + " не найден, обновление невозможно.");
        }
        if (faculty.getName() == null || faculty.getName().trim().isEmpty()) {
            logger.error("Faculty name cannot be empty.");
            throw new FacultyValidationException("Название факультета не может быть пустым");
        }
        Optional<Faculty> existing = facultyRepository.findAll().stream().filter(f -> f.getName().equalsIgnoreCase(faculty.getName()) && f.getId() != faculty.getId()).findFirst();
        if (existing.isPresent()) {
            logger.error("Faculty with name {} already exists.", faculty.getName());
            throw new FacultyDuplicateException("Факультет с названием '" + faculty.getName() + "' уже существует");
        }
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id) {
        logger.info("Was invoked method for deleting faculty by id");
        if (!facultyRepository.existsById(id)) {
            logger.error("Faculty with id {} not found.", id);
            throw new FacultyNotFoundException("Факультет с id " + id + " не найден.");
        }
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findFacultyByNameOrColor(String find) {
        logger.info("Was invoked method for finding all faculties by name or color");
        logger.debug("Search parameter: '{}'", find);
        return facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(find, find);
    }

    public Collection<Student> findStudentsByFacultyById(Long id) {
        logger.info("Was invoked method for finding all students by faculty by id");
        if (!facultyRepository.existsById(id)) {
            logger.error("Faculty with id {} not found.", id);
            throw new NotFoundException("Факультет с id " + id + " не найден.");
        }
        return studentRepository.findByFacultyId(id);
    }
}
