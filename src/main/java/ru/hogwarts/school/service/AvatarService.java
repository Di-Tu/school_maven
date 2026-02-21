package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    private static final int BUFFER_SIZE = 1024;

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        logger.info("Was invoked method for upload avatar");
        Student student = studentRepository.findById(studentId).orElseThrow(() -> {
            logger.error("Student with id {} was not found for avatar upload", studentId);
            return new StudentNotFoundException("Студент с id " + studentId + " не найден");
        });

        Path filePath = Path.of(avatarsDir, studentId + "_" + avatarFile.getOriginalFilename());
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = avatarFile.getInputStream(); OutputStream os = Files.newOutputStream(filePath, CREATE_NEW); BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE); BufferedOutputStream bos = new BufferedOutputStream(os, BUFFER_SIZE);) {
            bis.transferTo(bos);
        }

        Avatar avatar = findAvatarByStudentId(studentId).orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(generateDataForDB(filePath));

        Avatar savedAvatar = avatarRepository.save(avatar);
        return savedAvatar;
    }

    public Optional<Avatar> findAvatarByStudentId(Long studentId) {
        logger.info("Was invoked method for find avatar by student id");
        return avatarRepository.findByStudentId(studentId);
    }

    public byte[] getAvatarFromFile(Long studentId) throws IOException {
        logger.info("Was invoked method for getting avatar from file");
        Avatar avatar = findAvatarByStudentId(studentId).orElseThrow(() -> {
            logger.error("Avatar for student with id {} not found", studentId);
            return new StudentNotFoundException("Аватар для студента с id " + studentId + " не найден");
        });
        Path filePath = Path.of(avatar.getFilePath());
        return Files.readAllBytes(filePath);
    }

    public byte[] getAvatarFromDb(Long studentId) {
        logger.info("Was invoked method for getting avatar from db");
        Avatar avatar = findAvatarByStudentId(studentId).orElseThrow(() -> {
            logger.error("Avatar for student with id {} not found", studentId);
            return new StudentNotFoundException("Аватар для студента с id " + studentId + " не найден");
        });
        return avatar.getData();
    }

    public Avatar getAvatarInfo(Long studentId) {
        logger.info("Was invoked method for getting avatar info");
        return findAvatarByStudentId(studentId).orElseThrow(() -> {
            logger.error("Avatar for student with id {} not found", studentId);
            return new StudentNotFoundException("Аватар для студента с id " + studentId + " не найден");
        });
    }

    private byte[] generateDataForDB(Path filePath) throws IOException {
        logger.info("Was invoked method for generating data for db");
        BufferedImage image = ImageIO.read(filePath.toFile());
        if (image == null) {
            logger.warn("Image file at {} is corrupted or not an image", filePath);
            logger.error("Unable to read image");
            throw new IOException("Не удалось прочитать изображение");
        }

        int height = image.getHeight() / (image.getWidth() / 100);
        BufferedImage preview = new BufferedImage(100, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = preview.createGraphics();
        graphics.drawImage(image, 0, 0, 100, height, null);
        graphics.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(preview, getExtensions(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    private String getExtensions(String fileName) {
        logger.info("Was invoked method for getting extensions");
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public Collection<Avatar> getAllAvatars(int page, int size) {
        logger.info("Was invoked method for getting all avatars");
        logger.debug("Pagination params: page={}, size={}", page, size);
        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size);
        return avatarRepository.findAll(pageRequest).getContent();
    }
}
