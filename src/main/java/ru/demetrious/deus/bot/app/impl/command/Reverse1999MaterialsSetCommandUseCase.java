package ru.demetrious.deus.bot.app.impl.command;

import com.google.common.collect.MapDifference;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.apache.commons.lang3.DoubleRange;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.app.api.character.GetReverseDataOutbound;
import ru.demetrious.deus.bot.app.api.command.GetAttachmentOptionOutbound;
import ru.demetrious.deus.bot.app.api.command.Reverse1999MaterialsSetCommandInbound;
import ru.demetrious.deus.bot.app.api.interaction.SlashCommandInteractionInbound;
import ru.demetrious.deus.bot.app.api.message.NotifyOutbound;
import ru.demetrious.deus.bot.app.impl.canvas.ReverseMaterialsDifferenceCanvas;
import ru.demetrious.deus.bot.app.impl.command.test.MaterialRecognizer;
import ru.demetrious.deus.bot.domain.AttachmentOption;
import ru.demetrious.deus.bot.domain.ButtonComponent;
import ru.demetrious.deus.bot.domain.CommandData;
import ru.demetrious.deus.bot.domain.MessageComponent;
import ru.demetrious.deus.bot.domain.MessageData;
import ru.demetrious.deus.bot.domain.OptionData;
import ru.demetrious.deus.bot.utils.ImageUtils;

import static com.google.common.collect.Maps.difference;
import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;
import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
import static java.util.UUID.randomUUID;
import static java.util.stream.IntStream.rangeClosed;
import static org.apache.commons.lang3.function.Failable.asFunction;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.MORPH_ELLIPSE;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.morphologyEx;
import static ru.demetrious.deus.bot.app.api.button.GetCustomIdOutbound.DATA_DIVIDER;
import static ru.demetrious.deus.bot.domain.ButtonComponent.StyleEnum.DANGER;
import static ru.demetrious.deus.bot.domain.ButtonComponent.StyleEnum.SECONDARY;
import static ru.demetrious.deus.bot.domain.ButtonComponent.StyleEnum.SUCCESS;
import static ru.demetrious.deus.bot.domain.CommandData.Name.REVERSE1999_MATERIALS_SET;
import static ru.demetrious.deus.bot.domain.OptionData.Type.ATTACHMENT;

@RequiredArgsConstructor
@Slf4j
@Component
public class Reverse1999MaterialsSetCommandUseCase implements Reverse1999MaterialsSetCommandInbound {
    private static final Map<UUID, Map<Integer, Integer>> STORAGE = new HashMap<>();
    private static final int INVENTORY_OPTIONS_COUNT = 5;
    private static final String INVENTORY_OPTION_FORMAT = "attachment-%d";
    private static final DoubleRange SQUARE_RANGE = DoubleRange.of(0.7, 1.3);

    private final NotifyOutbound<SlashCommandInteractionInbound> notifyOutbound;
    private final GetReverseDataOutbound getReverseDataOutbound;
    private final GetAttachmentOptionOutbound getAttachmentOptionOutbound;
    private final Tesseract tesseract;

    @Override
    public CommandData getData() {
        return new CommandData()
            .setName(REVERSE1999_MATERIALS_SET)
            .setDescription("Позволяет считать инвентаря и сохранить его материалы")
            .setOptions(rangeClosed(1, INVENTORY_OPTIONS_COUNT)
                .mapToObj(index -> new OptionData()
                    .setType(ATTACHMENT)
                    .setName(INVENTORY_OPTION_FORMAT.formatted(index))
                    .setDescription("Скриншот #%d инвентаря или его части".formatted(index))
                    .setRequired(index == 1))
                .toList());
    }

    @Override
    public void execute() {
        MaterialRecognizer materialRecognizer = new MaterialRecognizer(tesseract, getReverseDataOutbound.getReverseData().getItems());

        rangeClosed(1, INVENTORY_OPTIONS_COUNT)
            .mapToObj(INVENTORY_OPTION_FORMAT::formatted)
            .map(getAttachmentOptionOutbound::getAttachmentOption)
            .flatMap(Optional::stream)
            .map(AttachmentOption::getUrl)
            .map(URI::create)
            .map(asFunction(URI::toURL))
            .map(asFunction(ImageIO::read))
            .map(ImageUtils::toMat)
            .map(Reverse1999MaterialsSetCommandUseCase::getSlots)
            .flatMap(Collection::stream)
            .forEach(materialRecognizer::collectSlot);

        Map<Integer, Integer> recognized = materialRecognizer.recognize();
        Map<Integer, Integer> existed = new HashMap<>(Map.of());
        MapDifference<Integer, Integer> difference = difference(recognized, existed);
        UUID key = randomUUID();

        difference.entriesOnlyOnLeft().keySet().forEach(k -> existed.putIfAbsent(k, 0));
        difference.entriesOnlyOnRight().keySet().forEach(k -> recognized.putIfAbsent(k, 0));

        STORAGE.put(key, recognized);
        notifyOutbound.notify(new MessageData()
            .setContent("Вы можете, либо согласиться с приложенными изменениями, либо внести коррективы в них, либо отменить")
            .setFiles(List.of(new ReverseMaterialsDifferenceCanvas(existed, recognized, getReverseDataOutbound.getReverseData().getItems()).createFile()))
            .setComponents(List.of(new MessageComponent().setButtons(List.of(
                new ButtonComponent()
                    .setId("%s%s%s".formatted("save", DATA_DIVIDER, key))
                    .setStyle(SUCCESS)
                    .setLabel("Сохранить"),
                new ButtonComponent()
                    .setId("%s%s%s".formatted("edit", DATA_DIVIDER, key))
                    .setStyle(SECONDARY)
                    .setLabel("Изменить"),
                new ButtonComponent()
                    .setId("%s%s%s".formatted("cancel", DATA_DIVIDER, key))
                    .setStyle(DANGER)
                    .setLabel("Отменить")
            )))));
    }


    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private static List<Mat> getSlots(Mat source) {
        Mat gray = new Mat();
        cvtColor(source, gray, COLOR_BGR2GRAY);
        GaussianBlur(gray, gray, new Size(9, 9), 0);

        Mat edges = new Mat();
        Canny(gray, edges, 40, 80);

        Mat kernel = getStructuringElement(MORPH_ELLIPSE, new Size(3, 3));
        dilate(edges, edges, kernel, new Point(-1, -1), 7);
        morphologyEx(edges, edges, MORPH_CLOSE, kernel, new Point(-1, -1), 7);

        List<MatOfPoint> contours = new ArrayList<>();
        findContours(edges, contours, new Mat(), RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);


        return contours.stream()
            .map(Imgproc::boundingRect)
            .filter(rect -> SQUARE_RANGE.contains((double) rect.width / rect.height))
            .map(source::submat)
            .toList();
    }

    public static BufferedImage toBufferedImage(Mat mat) {
        if (mat == null || mat.empty()) return null;

        int type = mat.channels() == 1 ? TYPE_BYTE_GRAY : TYPE_3BYTE_BGR;

        BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
        mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());

        return image;
    }
}
