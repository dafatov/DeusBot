package ru.demetrious.deus.bot.adapter.output.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import ru.demetrious.deus.bot.adapter.output.google.dto.FilesDto;
import ru.demetrious.deus.bot.adapter.output.google.dto.ReverseDataDto;
import ru.demetrious.deus.bot.fw.config.feign.FeignConfig;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@FeignClient(
    name = "google",
    url = "${feign.svc.google.url}",
    path = "${feign.svc.google.path}",
    configuration = FeignConfig.class
)
public interface GoogleDriveClient {
    @GetMapping("/drive/v3/files?spaces=appDataFolder")
    FilesDto getFiles();

    @GetMapping("/drive/v3/files/{fileId}?alt=media&source=downloadUrl")
    ReverseDataDto getFile(@PathVariable("fileId") String fileId);

    @PostMapping(value = "/upload/drive/v3/files?uploadType=multipart", consumes = MULTIPART_FORM_DATA_VALUE)
    void uploadFile(@RequestPart("metadata") MultipartFile metadata, @RequestPart("file") MultipartFile file);

    @PatchMapping(value = "/upload/drive/v3/files/{fileId}?uploadType=multipart", consumes = MULTIPART_FORM_DATA_VALUE)
    void updateFile(@PathVariable("fileId") String fileId,
                    @RequestPart("metadata") MultipartFile metadata,
                    @RequestPart("file") MultipartFile file);
}
