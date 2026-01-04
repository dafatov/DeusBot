package ru.demetrious.deus.bot.app.impl.canvas;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import ru.demetrious.deus.bot.domain.MessageFile;
import ru.demetrious.deus.bot.domain.reverse1999.Item;
import ru.demetrious.deus.bot.domain.reverse1999.ItemData;

import static com.google.common.collect.Sets.symmetricDifference;
import static java.awt.Color.BLACK;
import static java.awt.Font.PLAIN;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.ceilDiv;
import static java.lang.Math.divideExact;
import static java.util.Comparator.comparingInt;
import static ru.demetrious.deus.bot.utils.ImageUtils.calcWidth;
import static ru.demetrious.deus.bot.utils.ImageUtils.createWebp;
import static ru.demetrious.deus.bot.utils.ImageUtils.loadImage;

public class ReverseMaterialsDifferenceCanvas implements Canvas {
    private static final Font FONT = new Font("SansSerif", PLAIN, 26);
    private static final int X_GAP = 10;
    private static final int Y_GAP = 10;
    private static final int MAX_COLS = 6;
    private static final int ICON_HEIGHT = 100;
    private static final BufferedImage ARROW_IMAGE = loadImage("reverse1999/arrow-right.png");
    private static final Comparator<Item> MATERIAL_COMPARATOR = comparingInt(Item::order);

    @NotNull
    private final ReverseMaterialsDrawer beforeMaterialDrawer;
    @NotNull
    private final ReverseMaterialsDrawer afterMaterialDrawer;
    private final Params params;
    private final BufferedImage canvas;
    private final Graphics2D graphics2D;

    public ReverseMaterialsDifferenceCanvas(@NotNull Map<Integer, Integer> beforeMaterialMap,
                                            @NotNull Map<Integer, Integer> afterMaterialMap,
                                            Map<Integer, @NotNull ItemData> items) {
        if (!symmetricDifference(beforeMaterialMap.keySet(), afterMaterialMap.keySet()).isEmpty()) {
            throw new IllegalArgumentException("beforeMaterialMap and afterMaterialMap must contain the same keys");
        }

        int maxMaterialWidth = items.values().stream()
            .map(item -> calcWidth(item.getImage().toBufferedImage(), ICON_HEIGHT))
            .max(Integer::compareTo)
            .orElse(ICON_HEIGHT);

        this.params = getParams(maxMaterialWidth, beforeMaterialMap.size());
        this.canvas = new BufferedImage(params.width, params.height, TYPE_INT_ARGB);
        this.graphics2D = canvas.createGraphics();
        this.beforeMaterialDrawer = new ReverseMaterialsDrawer(graphics2D, items, beforeMaterialMap, ceilDiv(beforeMaterialMap.size(), MAX_COLS),
            maxMaterialWidth, ICON_HEIGHT, MATERIAL_COMPARATOR);
        this.afterMaterialDrawer = new ReverseMaterialsDrawer(graphics2D, items, afterMaterialMap, ceilDiv(beforeMaterialMap.size(), MAX_COLS),
            maxMaterialWidth, ICON_HEIGHT, MATERIAL_COMPARATOR);
    }

    @Override
    public MessageFile createFile() {
        try {
            graphics2D.setFont(FONT);
            graphics2D.setColor(BLACK);
            graphics2D.fillRect(0, 0, params.width, params.height);
            graphics2D.drawImage(ARROW_IMAGE,
                params.materialsWidth + X_GAP,
                divideExact(params.height - divideExact(ICON_HEIGHT, 2), 2),
                params.arrowImageWidth,
                divideExact(ICON_HEIGHT, 2),
                null);

            beforeMaterialDrawer.draw(0, 0);
            afterMaterialDrawer.draw(params.materialsWidth + X_GAP + params.arrowImageWidth, 0);

            return new MessageFile()
                .setName("reverse-materials-difference.webp")
                .setData(createWebp(canvas));
        } finally {
            graphics2D.dispose();
        }
    }

    private static Params getParams(int maxMaterialWidth, int count) {
        int materialsWidth = (maxMaterialWidth + X_GAP) * ceilDiv(count, ceilDiv(count, MAX_COLS));
        int arrowImageWidth = calcWidth(ARROW_IMAGE, divideExact(ICON_HEIGHT, 2));
        int width = 2 * (materialsWidth + X_GAP) + arrowImageWidth;
        int height = ICON_HEIGHT * ceilDiv(count, MAX_COLS) + Y_GAP * (ceilDiv(count, MAX_COLS) + 1);

        return new Params(height, width, materialsWidth, arrowImageWidth);
    }

    private record Params(int height, int width, int materialsWidth, int arrowImageWidth) {
    }
}
