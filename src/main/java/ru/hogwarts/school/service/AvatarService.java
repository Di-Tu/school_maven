package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    private static final int BUFFER_SIZE = 1024;

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Студент с id " + studentId + " не найден"));

        // Создаем директорию, если она не существует
        Path filePath = Path.of(avatarsDir, studentId + "_" + avatarFile.getOriginalFilename());
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        // Сохраняем файл на диск
        try (
                InputStream is = avatarFile.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
                BufferedOutputStream bos = new BufferedOutputStream(os, BUFFER_SIZE);
        ) {
            bis.transferTo(bos);
        }

        // Сохраняем информацию в БД
        Avatar avatar = findAvatarByStudentId(studentId).orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(avatarFile.getBytes());

        Avatar savedAvatar = avatarRepository.save(avatar);
        return savedAvatar;
    }

    public Optional<Avatar> findAvatarByStudentId(Long studentId) {
        return avatarRepository.findByStudentId(studentId);
    }

    public byte[] getAvatarFromFile(Long studentId) throws IOException {
        Avatar avatar = findAvatarByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Аватар для студента с id " + studentId + " не найден"));

        Path filePath = Path.of(avatar.getFilePath());
        return Files.readAllBytes(filePath);
    }

    public byte[] getAvatarFromDb(Long studentId) {
        Avatar avatar = findAvatarByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Аватар для студента с id " + studentId + " не найден"));

        return avatar.getData();
    }

    public Avatar getAvatarInfo(Long studentId) {
        return findAvatarByStudentId(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Аватар для студента с id " + studentId + " не найден"));
    }
}
