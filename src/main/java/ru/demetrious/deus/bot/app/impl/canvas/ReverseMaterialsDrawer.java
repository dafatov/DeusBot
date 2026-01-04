package ru.demetrious.deus.bot.app.impl.canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.demetrious.deus.bot.domain.Image;
import ru.demetrious.deus.bot.domain.reverse1999.Item;
import ru.demetrious.deus.bot.domain.reverse1999.ItemData;
import ru.demetrious.deus.bot.utils.CanvasUtils;

import static java.awt.Font.PLAIN;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.ceilDiv;
import static java.lang.Math.divideExact;
import static java.lang.Math.multiplyFull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static ru.demetrious.deus.bot.utils.CanvasUtils.drawMaterialIcon;
import static ru.demetrious.deus.bot.utils.ImageUtils.calcWidth;

@Slf4j
public class ReverseMaterialsDrawer {
    private static final Font FONT = new Font("SansSerif", PLAIN, 100);
    private static final int X_GAP = 10;
    private static final int Y_GAP = 10;
    private static final int DEFAULTS_ICON_SIZE = 256;

    @NotNull
    private final Graphics2D graphics2d;
    @NotNull
    private final Params params;
    @NotNull
    private final List<Item> materialList;
    private final int maxRows;

    public ReverseMaterialsDrawer(@NotNull Graphics2D graphics2d,
                                  @NotNull Map<Integer, ItemData> items,
                                  @NotNull Map<Integer, Integer> materials,
                                  int maxRows,
                                  int maxMaterialWidth,
                                  int iconHeight,
                                  Comparator<Item> materialComparator) {
        this.graphics2d = graphics2d;
        this.params = new Params(maxMaterialWidth, iconHeight);
        this.materialList = materials.entrySet().stream()
            .map(entry -> new Item(entry.getKey(),
                ofNullable(items.get(entry.getKey()))
                    .map(ItemData::getOrder)
                    .orElse(MAX_VALUE),
                ofNullable(items.get(entry.getKey()))
                    .map(ItemData::getImage)
                    .map(Image::toBufferedImage)
                    .orElseGet(() -> createWhiteSquare(entry.getKey())),
                entry.getValue()))
            .sorted(materialComparator)
            .toList();
        log.debug("materialListOrders: {}", materialList.stream()
            .collect(toMap(Item::id, Item::order)));
        this.maxRows = maxRows;
    }

    public void draw(int xStart, int yStart) {
        int materialsStartX = X_GAP + xStart;
        int materialsStartY = Y_GAP + yStart;
        int maxColumns = ceilDiv(materialList.size(), maxRows);
        int rowHeight = params.iconHeight + Y_GAP;

        for (int row = 0; row < maxRows; row++) {
            int yPos = materialsStartY + row * rowHeight;
            List<Item> rowMaterials = materialList.stream()
                .skip(multiplyFull(row, maxColumns))
                .limit(maxColumns)
                .toList();

            int xPos = materialsStartX;
            for (Item material : rowMaterials) {
                int materialWidth = calcWidth(material.image(), params.iconHeight);

                drawMaterialIcon(material, xPos, yPos, materialWidth, params.iconHeight, graphics2d);

                xPos += params.maxMaterialWidth + X_GAP;
            }
        }
    }

    // =========================================================================================================================================================
    // = Implementation
    // =========================================================================================================================================================

    private static BufferedImage createWhiteSquare(Integer id) {
        BufferedImage image = new BufferedImage(DEFAULTS_ICON_SIZE, DEFAULTS_ICON_SIZE, TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        try {
            g2d.setFont(FONT);
            g2d.setColor(new Color(1, 1, 1, 0.3f));
            g2d.fillRect(0, 0, DEFAULTS_ICON_SIZE, DEFAULTS_ICON_SIZE);
            CanvasUtils.drawCenteredText(String.valueOf(id), divideExact(DEFAULTS_ICON_SIZE, 2), divideExact(DEFAULTS_ICON_SIZE, 2), DEFAULTS_ICON_SIZE, g2d);
        } finally {
            g2d.dispose();
        }
        return image;
    }

    private record Params(int maxMaterialWidth, int iconHeight) {
    }
}
