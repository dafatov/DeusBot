package ru.demetrious.deus.bot.utils;

import com.luciad.imageio.webp.WebPWriteParam;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import lombok.experimental.UtilityClass;

import static javax.imageio.ImageIO.createImageOutputStream;
import static javax.imageio.ImageIO.getImageWritersByMIMEType;
import static javax.imageio.ImageIO.getWriterFormatNames;
import static javax.imageio.ImageIO.scanForPlugins;
import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;

@UtilityClass
public class ImageUtils {
    private static final String WEBP_MIME_TYPE = "image/webp";

    public static byte[] createWebp(RenderedImage image) {
        return createWebp(image, false);
    }

    public static byte[] createWebp(RenderedImage image, boolean highQuality) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            ImageWriter webpWriter = getWebpImageWriter();

            try {
                WebPWriteParam writeParams = createWriteParams(webpWriter, highQuality);

                try (ImageOutputStream imageStream = createImageOutputStream(byteStream)) {
                    webpWriter.setOutput(imageStream);
                    webpWriter.write(null, new IIOImage(image, null, null), writeParams);
                }
                return byteStream.toByteArray();
            } finally {
                webpWriter.dispose();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create WebP image", e);
        }
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private static ImageWriter getWebpImageWriter() throws IOException {
        Iterator<ImageWriter> writers = getImageWritersByMIMEType(WEBP_MIME_TYPE);

        if (!writers.hasNext()) {
            // Не инициализуется с первого раза тип для webp при старте приложения, поэтому нужно пересканировать
            scanForPlugins();
            writers = getImageWritersByMIMEType(WEBP_MIME_TYPE);

            if (!writers.hasNext()) {
                throw new IOException("No WebP writer found. Available formats: %s".formatted(Arrays.toString(getWriterFormatNames())));
            }
        }
        return writers.next();
    }

    private static WebPWriteParam createWriteParams(ImageWriter writer, boolean highQuality) {
        WebPWriteParam params = new WebPWriteParam(writer.getLocale());

        params.setCompressionMode(MODE_EXPLICIT);
        params.setCompressionType(highQuality ? "Lossless" : "Lossy");
        return params;
    }
}
