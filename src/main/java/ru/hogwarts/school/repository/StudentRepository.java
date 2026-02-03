package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Collection<Student> findStudentsByAge(int age);

    Collection<Student> findByAgeBetween(int min, int max);

    @Query("SELECT s.faculty FROM Student s WHERE s.id = :studentId")
    Faculty findFacultyByStudentId(@Param("studentId") Long studentId);

    Collection<Student> findByFacultyId(Long facultyId);

    @Query("SELECT COUNT(*) FROM Student")
    Integer countStudents();

    @Query("SELECT AVG(age) FROM Student")
    Double averageAgeStudents();

    @Query(value ="SELECT * FROM student ORDER BY id DESC LIMIT 5", nativeQuery = true)
    Collection<Student> theLast5Students();
}
