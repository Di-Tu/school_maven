package ru.hogwarts.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Для ситуации дублирования уникальных полей
@ResponseStatus(HttpStatus.CONFLICT)
public class FacultyDuplicateException extends RuntimeException {
    public FacultyDuplicateException(String message) {
        super(message);
    }
}
