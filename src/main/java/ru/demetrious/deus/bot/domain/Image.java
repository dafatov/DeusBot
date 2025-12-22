package ru.demetrious.deus.bot.domain;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import lombok.Data;

import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.write;

@Data
public class Image implements Serializable {
    private byte[] data;

    public Image(BufferedImage image) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            write(image, "png", byteArrayOutputStream);
            this.data = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage toBufferedImage() {
        try (ByteArrayInputStream input = new ByteArrayInputStream(data)) {
            return read(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}