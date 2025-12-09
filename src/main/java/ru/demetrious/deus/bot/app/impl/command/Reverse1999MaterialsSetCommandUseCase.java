package ru.demetrious.deus.bot.app.impl.command;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.tuple.Pair;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.character.GetReverseDataOutbound;
import ru.demetrious.deus.bot.app.api.command.GetAttachmentOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.Reverse1999MaterialsSetCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.impl.command.test.MaterialRecognizer;
import ru.demetrious.deus.bot.domain.AttachmentOption;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.MessageFile;
import ru.demetrious.deus.bot.domain.OptionData;

import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;
import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.function.Failable.asFunction;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.MORPH_ELLIPSE;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.morphologyEx;
import static org.opencv.imgproc.Imgproc.putText;
import static org.opencv.imgproc.Imgproc.rectangle;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_MATERIALS_SET;
import static ru.demetrious.deus.bot.domain.OptionData.Type.ATTACHMENT;

@RequiredArgsConstructor
@Slf4j
@Component
public class Reverse1999MaterialsSetCommandUseCase implements Reverse1999MaterialsSetCommandInbound {
    private static final String INVENTORY_OPTION = "inventory";

    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final GetReverseDataOutbound getReverseDataOutbound;
    private final GetAttachmentOptionOutbound getAttachmentOptionOutbound;
    private final Tesseract tesseract;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(REVERSE1999_MATERIALS_SET)
            .setDescription("Позволяет считать инвентаря и сохранить его материалы")
            .setOptions(List.of(
                new OptionData()
                    .setType(ATTACHMENT)
                    .setName(INVENTORY_OPTION)
                    .setDescription("Скриншот инвентаря или его части")
                    .setRequired(true)
            ));
    }

    @Override
    public void execute() {
        Mat src = getAttachmentOptionOutbound.getAttachmentOption(INVENTORY_OPTION)
            .map(AttachmentOption::getUrl)
            .map(URI::create)
            .map(asFunction(URI::toURL))
            .map(asFunction(ImageIO::read))
            .map(Reverse1999MaterialsSetCommandUseCase::toMat)
            .orElseThrow();
        List<Rect> slots = getSlots(src);

        AtomicInteger atomicInteger = new AtomicInteger();
        MaterialRecognizer materialRecognizer = new MaterialRecognizer(getReverseDataOutbound.getReverseData());
        Map<Integer, Rect> collect = slots.stream()
            .map(roi -> Pair.of(roi, src.submat(roi)))
            .map(f -> Pair.of(materialRecognizer.collectSlot(f, atomicInteger.incrementAndGet()), f.getKey()))
            .collect(toMap(Pair::getLeft, Pair::getRight));
        List<MaterialRecognizer.RecognitionResult> recognitionResultList = materialRecognizer.performGlobalRecognition().stream()
            .map(asFunction(f -> {
                if (f.getTemplateId() < 0) {
                    return f;
                }

                Mat submated = src.submat(f.getRect());
                Rect roi1 = new Rect(0, submated.height() - (int) (submated.height() * 0.25), submated.width(), (int) (submated.height() * 0.2));
                Mat roi = new Mat(submated, roi1);
                String doneOCR = getDoneOCR(roi);

                try {
                    f.setCount(Integer.parseInt(doneOCR));
                } catch (NumberFormatException ignored) {
                }
                return f;
            }))
            .map(f -> new MaterialRecognizer.RecognitionResult(f.getTemplateId(), f.getCount(), collect.get(f.getSlotId()), f.getSlotId()))
            .toList();

        Mat debug = src.clone();
        for (MaterialRecognizer.RecognitionResult r : recognitionResultList) {
            Rect rr = r.key();

            putText(debug, String.valueOf(r.slotId()), new Point(rr.x + 5, rr.y + 25), Imgproc.FONT_HERSHEY_SIMPLEX, 1., new Scalar(255, 255, 255));

            if (r.id() < 0) {
                continue;
            }

            rectangle(debug, new Point(rr.x, rr.y), new Point(rr.x + rr.width, rr.y + rr.height),
                new Scalar(0, 255, 0), 2);
            putText(debug, "%d:%d".formatted(r.id(), r.quantity()), new Point(rr.x, rr.y - 5), Imgproc.FONT_HERSHEY_SIMPLEX, 1., new Scalar(255, 255, 255));
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(toBufferedImage(debug), "jpg", byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            notifyOutbound.notify(new MessageData()
                .setContent(abbreviate(slots.toString(), 2000))
                .setFiles(List.of(new MessageFile()
                    .setName("reverse-materials.jpg")
                    .setData(byteArray)
                ))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDoneOCR(Mat roi) throws TesseractException {
// Шаг 2: Конвертация в HSV для лучшей детекции белого (опционально, но рекомендуется)
        Mat hsv = new Mat();
        Imgproc.cvtColor(roi, hsv, Imgproc.COLOR_BGR2HSV);

        // Шаг 3: Создание маски для белого (низкая насыщенность, высокая яркость)
        // Диапазон: H=0-180 (any), S=0-30, V=220-255
        Scalar lowerWhite = new Scalar(0, 0, 220);
        Scalar upperWhite = new Scalar(180, 30, 255);
        Mat mask = new Mat();
        Core.inRange(hsv, lowerWhite, upperWhite, mask);

        // Дополнительно: Расширение маски (dilation) перед шагом 4
        // Создаем структурный элемент (kernel) для dilation, например, ellipse 3x3
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.dilate(mask, mask, kernel); // Расширяем маску (увеличиваем белые области)

        // Шаг 4: Создание чёрного изображения и применение маски
        Mat result = Mat.zeros(roi.size(), roi.type()); // Чёрный фон
        roi.copyTo(result, mask); // Копируем только белые пиксели

        Core.bitwise_not(result, result);
        return tesseract.doOCR(toBufferedImage(result)).trim();
    }


    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private static List<Rect> getSlots(Mat src) {
        // 1. Gray + Blur для подготовки
        Mat gray = new Mat();
        cvtColor(src, gray, COLOR_BGR2GRAY);
        GaussianBlur(gray, gray, new Size(9, 9), 0);

        // 2. Canny для выявления краёв рамок (лучше для градиентов, чем HSV)
        Mat edges = new Mat();
        Canny(gray, edges, 40, 80);

        // 3. Морфология для соединения линий
        Mat kernel = getStructuringElement(MORPH_ELLIPSE, new Size(3, 3));
        dilate(edges, edges, kernel, new Point(-1, -1), 7); // Утолщаем края
        morphologyEx(edges, edges, MORPH_CLOSE, kernel, new Point(-1, -1), 7); // Закрываем разрывы

        // 4. findContours на краях
        List<MatOfPoint> contours = new ArrayList<>();
        findContours(edges, contours, new Mat(), RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

        List<Rect> slots = new ArrayList<>();
        for (MatOfPoint c : contours) {
            Rect r = boundingRect(c);
            double aspect = (double) r.width / r.height;

            if (aspect < 0.7 || aspect > 1.3) {
                continue;
            }

            slots.add(r);
        }
        return slots;
    }

    public static Mat toMat(BufferedImage img) {
        if (img == null) return new Mat();

        // Приводим к удобному типу
        if (img.getType() != TYPE_3BYTE_BGR) {
            BufferedImage temp = new BufferedImage(img.getWidth(), img.getHeight(), TYPE_3BYTE_BGR);
            temp.getGraphics().drawImage(img, 0, 0, null);
            img = temp;
        }

        byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }

    private static BufferedImage toBufferedImage(Mat mat) {
        if (mat == null || mat.empty()) return null;

        int type = mat.channels() == 1 ? TYPE_BYTE_GRAY : TYPE_3BYTE_BGR;

        BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
        mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());

        return image;
    }
}
