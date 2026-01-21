package ru.hogwarts.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Для ситуации, когда пытаемся создать или обновить факультет с некорректными данными
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FacultyValidationException extends RuntimeException {
    public FacultyValidationException(String message) {
        super(message);
    }
}
