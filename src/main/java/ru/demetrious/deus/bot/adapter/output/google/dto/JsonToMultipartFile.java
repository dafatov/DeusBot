package ru.demetrious.deus.bot.adapter.output.google.dto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SuppressWarnings("ClassCanBeRecord")
public class JsonToMultipartFile implements MultipartFile {
    private final byte[] data;
    private final String filename;

    public JsonToMultipartFile(String jsonString, String filename) {
        this.data = jsonString.getBytes(UTF_8);
        this.filename = filename;
    }

    @Override
    public @NotNull String getName() {
        return filename;
    }

    @Override
    public String getOriginalFilename() {
        return filename;
    }

    @Override
    public String getContentType() {
        return APPLICATION_JSON_VALUE;
    }

    @Override
    public boolean isEmpty() {
        return data.length == 0;
    }

    @Override
    public long getSize() {
        return data.length;
    }

    @Override
    public byte @NotNull [] getBytes() {
        return data;
    }

    @Override
    public @NotNull InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public void transferTo(@NotNull File dest) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}