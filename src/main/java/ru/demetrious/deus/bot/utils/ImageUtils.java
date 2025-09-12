package ru.demetrious.deus.bot.utils;

import com.luciad.imageio.webp.WebPWriteParam;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import lombok.experimental.UtilityClass;

import static javax.imageio.ImageIO.createImageOutputStream;
import static javax.imageio.ImageIO.getImageWritersByMIMEType;
import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;

@UtilityClass
public class ImageUtils {
    public static void writeWebp(RenderedImage renderedImage, ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        writeWebp(renderedImage, byteArrayOutputStream, false);
    }

    public static void writeWebp(RenderedImage renderedImage, ByteArrayOutputStream byteArrayOutputStream, boolean isHighQuality) throws IOException {
        ImageWriter writer = getImageWritersByMIMEType("image/webp").next();
        WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());

        writeParam.setCompressionMode(MODE_EXPLICIT);
        writeParam.setCompressionType(isHighQuality ? "Lossless" : "Lossy");

        try (ImageOutputStream imageOutputStream = createImageOutputStream(byteArrayOutputStream)) {
            writer.setOutput(imageOutputStream);
            writer.write(null, new IIOImage(renderedImage, null, null), writeParam);
        } finally {
            writer.dispose();
        }
    }
}
