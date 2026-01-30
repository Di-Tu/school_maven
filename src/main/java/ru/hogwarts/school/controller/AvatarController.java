package ru.hogwarts.school.controller;

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
    public ResponseEntity<Long> uploadAvatar(@PathVariable Long studentId, @RequestPart("avatar") MultipartFile avatar) throws IOException {
        return ResponseEntity.ok(avatarService.uploadAvatar(studentId, avatar).getId());
    }

    @GetMapping(value = "/{studentId}/from-db")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable Long studentId) {
        Avatar avatar = avatarService.getAvatarInfo(studentId);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(avatar.getMediaType())).contentLength(avatar.getData().length).body(avatar.getData());
    }

    @GetMapping(value = "/{studentId}/from-file")
    public ResponseEntity<byte[]> getAvatarFromFile(@PathVariable Long studentId) throws IOException {
        Avatar avatar = avatarService.getAvatarInfo(studentId);
        byte[] data = avatarService.getAvatarFromFile(studentId);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(avatar.getMediaType())).contentLength(data.length).body(data);
    }
}
