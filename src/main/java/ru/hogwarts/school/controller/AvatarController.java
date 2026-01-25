package ru.hogwarts.school.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{studentId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadAvatar(@PathVariable Long studentId,
                                             @RequestParam MultipartFile avatar) {
        try {
            Avatar savedAvatar = avatarService.uploadAvatar(studentId, avatar);
            return ResponseEntity.ok(savedAvatar.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/{studentId}/from-db")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable Long studentId) {
        byte[] data = avatarService.getAvatarFromDb(studentId);
        Avatar avatar = avatarService.getAvatarInfo(studentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getFileSize());

        return ResponseEntity.ok().headers(headers).body(data);
    }

    @GetMapping(value = "/{studentId}/from-file")
    public ResponseEntity<byte[]> getAvatarFromFile(@PathVariable Long studentId) {
        try {
            byte[] data = avatarService.getAvatarFromFile(studentId);
            Avatar avatar = avatarService.getAvatarInfo(studentId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
            headers.setContentLength(avatar.getFileSize());

            return ResponseEntity.ok().headers(headers).body(data);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}