package ru.demetrious.deus.bot.utils;

import com.luciad.imageio.webp.WebPWriteParam;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import lombok.experimental.UtilityClass;
import org.opencv.core.Mat;
import ru.demetrious.deus.bot.domain.Image;

import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static javax.imageio.ImageIO.createImageOutputStream;
import static javax.imageio.ImageIO.getImageWritersByMIMEType;
import static javax.imageio.ImageIO.getWriterFormatNames;
import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.scanForPlugins;
import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;
import static org.opencv.core.CvType.CV_8UC3;

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

    public static BufferedImage loadImage(String imagePath) {
        try {
            return read(requireNonNull(ImageUtils.class.getClassLoader().getResourceAsStream(imagePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Image loadImage(byte[] image) throws IOException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(image)) {
            return new Image(read(input));
        }
    }

    public static int calcWidth(BufferedImage bufferedImage, int height) {
        return height * bufferedImage.getWidth() / bufferedImage.getHeight();
    }

    public static int calcHeight(BufferedImage bufferedImage, int width) {
        return width * bufferedImage.getHeight() / bufferedImage.getWidth();
    }

    public static Mat toMat(BufferedImage source) {
        if (isNull(source)) {
            throw new NullPointerException();
        }

        Mat mat = new Mat(source.getHeight(), source.getWidth(), CV_8UC3);

        if (source.getType() != TYPE_3BYTE_BGR) {
            BufferedImage tmp = new BufferedImage(source.getWidth(), source.getHeight(), TYPE_3BYTE_BGR);

            tmp.getGraphics().drawImage(source, 0, 0, null);
            source = tmp;
        }

        mat.put(0, 0, ((DataBufferByte) source.getRaster().getDataBuffer()).getData());
        return mat;
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
