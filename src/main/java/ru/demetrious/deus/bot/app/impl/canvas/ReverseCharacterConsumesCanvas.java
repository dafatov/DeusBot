package ru.demetrious.deus.bot.app.impl.canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import ru.demetrious.deus.bot.domain.Image;
import ru.demetrious.deus.bot.domain.MessageFile;
import ru.demetrious.deus.bot.domain.reverse1999.CharacterData;
import ru.demetrious.deus.bot.domain.reverse1999.CharacterStats;
import ru.demetrious.deus.bot.domain.reverse1999.Item;
import ru.demetrious.deus.bot.domain.reverse1999.ReverseData;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.awt.Font.PLAIN;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.ceilDiv;
import static java.lang.Math.divideExact;
import static java.lang.Math.max;
import static java.util.Comparator.comparingDouble;
import static lombok.AccessLevel.PRIVATE;
import static ru.demetrious.deus.bot.utils.CanvasUtils.drawCenteredText;
import static ru.demetrious.deus.bot.utils.CanvasUtils.drawMaterialIcon;
import static ru.demetrious.deus.bot.utils.ImageUtils.calcWidth;
import static ru.demetrious.deus.bot.utils.ImageUtils.createWebp;
import static ru.demetrious.deus.bot.utils.ImageUtils.loadImage;
import static ru.demetrious.deus.bot.utils.SpellUtils.DECIMAL_FORMAT;
import static ru.demetrious.deus.bot.utils.reverse.FarmingPlanUtils.calculateFarmingPlan;

@Slf4j
public class ReverseCharacterConsumesCanvas implements Canvas {
    private static final Font FONT = new Font("SansSerif", PLAIN, 26);
    private static final Font MINI_FONT = new Font("SansSerif", PLAIN, 13);
    private static final int X_GAP = 10;
    private static final int Y_GAP = 10;
    private static final int MAX_ROWS = 8;

    @NotNull
    private final CharacterData character;
    @NotNull
    private final ReverseMaterialsDrawer materialDrawer;
    @NotNull
    private final List<Row> rows;
    @NotNull
    private final CharacterStats currentStats;
    @NotNull
    private final CharacterStats targetStats;
    private final Params params;
    private final BufferedImage canvas;
    private final Graphics2D graphics2D;

    public ReverseCharacterConsumesCanvas(@NotNull CharacterData character,
                                          @NotNull Pair<CharacterStats, CharacterStats> statsPair,
                                          @NotNull Map<Integer, Integer> materialMap,
                                          @NotNull ReverseData reverseData) {
        int minAvatarHeight = reverseData.getCharacters().values().stream()
            .map(CharacterData::getAvatar)
            .map(Image::toBufferedImage)
            .map(BufferedImage::getHeight)
            .min(Integer::compareTo)
            .orElseThrow();
        int iconHeight = divideExact(minAvatarHeight - (MAX_ROWS - 1) * Y_GAP, MAX_ROWS);
        int miniIconHeight = divideExact(3 * iconHeight, 4);
        List<Card> cards = createCards(calculateFarmingPlan(materialMap, reverseData), miniIconHeight);
        int maxMaterialWidth = reverseData.getItems().values().stream()
            .map(item -> calcWidth(item.getImage().toBufferedImage(), iconHeight))
            .max(Integer::compareTo)
            .orElseThrow();
        int canvasWidth = getCanvasWidth(character, statsPair, materialMap, maxMaterialWidth, iconHeight, minAvatarHeight, cards);

        this.currentStats = statsPair.getLeft();
        this.targetStats = statsPair.getRight();
        this.character = character;
        this.rows = createRows(canvasWidth, cards);
        this.params = getParams(minAvatarHeight, iconHeight, miniIconHeight, canvasWidth, maxMaterialWidth, rows.size());
        this.canvas = new BufferedImage(params.width, params.height, TYPE_INT_ARGB);
        this.graphics2D = canvas.createGraphics();
        this.materialDrawer = new ReverseMaterialsDrawer(graphics2D, reverseData.getItems(), materialMap, MAX_ROWS - 2,
            maxMaterialWidth, iconHeight, comparingDouble(Item::amount).reversed());
    }

    @Override
    public MessageFile createFile() {
        try {
            drawBackground();
            drawInfo();
            drawDivider();
            drawCards();

            return new MessageFile()
                .setName("reverse-materials.webp")
                .setData(createWebp(canvas));
        } finally {
            graphics2D.dispose();
        }
    }

    public record Level(String name, int cost, int count, List<Item> items) {
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private void drawBackground() {
        graphics2D.setFont(FONT);
        graphics2D.setColor(BLACK);
        graphics2D.fillRect(0, 0, params.width, params.height);
    }

    private void drawInfo() {
        int xStart = drawCharacter();
        int yStart = drawStats(xStart);

        materialDrawer.draw(xStart, yStart);
    }

    private int drawCharacter() {
        BufferedImage avatar = character.getAvatar().toBufferedImage();
        int avatarWidth = calcWidth(avatar, params.minAvatarHeight);
        GradientPaint gradientPaint = new GradientPaint(
            X_GAP, Y_GAP, character.getRarityColor(),
            X_GAP, Y_GAP + divideExact(params.minAvatarHeight, 3), new Color(1, 1, 1, 0)
        );

        graphics2D.setPaint(gradientPaint);
        graphics2D.fillRect(X_GAP, Y_GAP, avatarWidth, params.minAvatarHeight);
        graphics2D.drawImage(avatar, X_GAP, Y_GAP, avatarWidth, params.minAvatarHeight, new Color(1, 1, 1, 0.1f), null);
        return avatarWidth + X_GAP;
    }

    private int drawStats(int xStart) {
        BufferedImage arrowImage = loadImage("reverse1999/arrow-right.png");
        BufferedImage resonanceImage = loadImage("reverse1999/resonance/r.png");
        BufferedImage currentInsight = loadImage(getInsightImagePath(currentStats.insight()));
        BufferedImage targetInsight = loadImage(getInsightImagePath(targetStats.insight()));

        int arrowImageWidth = calcWidth(arrowImage, divideExact(params.iconHeight, 2));
        int resonanceWidth = calcWidth(resonanceImage, params.iconHeight);
        int currentInsightWidth = calcWidth(currentInsight, params.iconHeight);
        int targetInsightWidth = calcWidth(targetInsight, params.iconHeight);

        int currentInsightX = divideExact(params.width + xStart - currentInsightWidth - arrowImageWidth - targetInsightWidth - 2 * X_GAP, 2);
        int arrowX = currentInsightX + currentInsightWidth + X_GAP;
        int targetInsightX = arrowX + arrowImageWidth + X_GAP;

        int resonanceY = 2 * Y_GAP + params.iconHeight;
        int arrowY = divideExact(3 * Y_GAP, 2) + params.iconHeight - divideExact(params.iconHeight, 4);

        graphics2D.drawImage(arrowImage, arrowX, arrowY, arrowImageWidth, divideExact(params.iconHeight, 2), null);
        graphics2D.drawImage(currentInsight, currentInsightX, Y_GAP, currentInsightWidth, params.iconHeight, null);
        graphics2D.drawImage(targetInsight, targetInsightX, Y_GAP, targetInsightWidth, params.iconHeight, null);
        graphics2D.drawImage(resonanceImage, currentInsightX, resonanceY, resonanceWidth, params.iconHeight, null);
        graphics2D.drawImage(resonanceImage, targetInsightX, resonanceY, resonanceWidth, params.iconHeight, null);

        graphics2D.setColor(new Color(0, 0, 0, 0.75f));
        graphics2D.fillRect(currentInsightX, Y_GAP, currentInsightWidth, params.iconHeight);
        graphics2D.fillRect(targetInsightX, Y_GAP, targetInsightWidth, params.iconHeight);
        graphics2D.fillRect(currentInsightX, resonanceY, resonanceWidth, params.iconHeight);
        graphics2D.fillRect(targetInsightX, resonanceY, resonanceWidth, params.iconHeight);

        graphics2D.setColor(WHITE);
        drawCenteredText("%d".formatted(currentStats.level()),
            currentInsightX + divideExact(currentInsightWidth, 2),
            Y_GAP + params.iconHeight, currentInsightWidth, graphics2D);
        drawCenteredText("%d".formatted(targetStats.level()),
            targetInsightX + divideExact(targetInsightWidth, 2),
            Y_GAP + params.iconHeight, targetInsightWidth, graphics2D);
        drawCenteredText("%d".formatted(currentStats.resonance()),
            currentInsightX + divideExact(resonanceWidth, 2),
            resonanceY + params.iconHeight, resonanceWidth, graphics2D);
        drawCenteredText("%d".formatted(targetStats.resonance()),
            targetInsightX + divideExact(resonanceWidth, 2),
            resonanceY + params.iconHeight, resonanceWidth, graphics2D);

        return resonanceY + params.iconHeight;
    }

    private void drawDivider() {
        GradientPaint leftGradient = new GradientPaint(
            0, params.minAvatarHeight + 2 * Y_GAP,
            new Color(0, 0, 0),
            divideExact(params.width, 2), params.minAvatarHeight + 2 * Y_GAP,
            new Color(255, 255, 255)
        );
        GradientPaint rightGradient = new GradientPaint(
            params.width, params.minAvatarHeight + 2 * Y_GAP,
            new Color(0, 0, 0),
            divideExact(params.width, 2), params.minAvatarHeight + 2 * Y_GAP,
            new Color(255, 255, 255)
        );

        graphics2D.setPaint(leftGradient);
        graphics2D.drawLine(0, params.minAvatarHeight + 2 * Y_GAP, divideExact(params.width, 2), params.minAvatarHeight + 2 * Y_GAP);
        graphics2D.setPaint(rightGradient);
        graphics2D.drawLine(params.width, params.minAvatarHeight + 2 * Y_GAP, divideExact(params.width, 2), params.minAvatarHeight + 2 * Y_GAP);
    }

    private void drawCards() {
        int yPos = params.minAvatarHeight + 3 * Y_GAP;
        for (Row row : rows) {
            int freeSpace = params.width - 2 * X_GAP - row.width;

            if (freeSpace > 0 && !row.cards.isEmpty()) {
                int extraPerCard = divideExact(freeSpace, row.cards.size());
                int remaining = freeSpace - (extraPerCard * row.cards.size());

                row.cards.forEach(card -> card.renderWidth = card.width + extraPerCard);

                if (remaining > 0) {
                    row.cards.getLast().renderWidth += remaining;
                }
            } else {
                for (Card card : row.cards) {
                    card.renderWidth = card.width;
                }
            }

            int xPos = X_GAP;
            for (Card card : row.cards) {
                int headerY = yPos + graphics2D.getFontMetrics(FONT).getAscent();
                String cost = getCardCost(card.level);
                String count = getCardCount(card.level);
                int materialsY = yPos + graphics2D.getFontMetrics(FONT).getHeight() - graphics2D.getFontMetrics(FONT).getDescent();
                int totalIconsWidth = card.level.items.stream()
                    .map(Item::image)
                    .mapToInt(f -> calcWidth(f, params.miniIconHeight))
                    .sum();

                graphics2D.setColor(new Color(1, 1, 1, 0.1f));
                graphics2D.fillRect(xPos, yPos, card.renderWidth, params.cardHeight);
                graphics2D.setColor(new Color(1, 1, 1, 1f));

                graphics2D.setFont(FONT);
                drawCenteredText(card.level.name,
                    xPos + divideExact(card.renderWidth, 2),
                    headerY,
                    card.renderWidth, graphics2D);

                graphics2D.setFont(MINI_FONT);
                graphics2D.drawString(count,
                    xPos + X_GAP,
                    headerY - graphics2D.getFontMetrics(FONT).getAscent() - graphics2D.getFontMetrics(MINI_FONT).getAscent() + graphics2D.getFontMetrics(FONT).getHeight());
                graphics2D.drawString(cost,
                    xPos + card.renderWidth - graphics2D.getFontMetrics(MINI_FONT).stringWidth(cost) - X_GAP,
                    headerY - graphics2D.getFontMetrics(FONT).getAscent() - graphics2D.getFontMetrics(MINI_FONT).getAscent() + graphics2D.getFontMetrics(FONT).getHeight());

                int xMaterialPos = xPos + divideExact(card.renderWidth - totalIconsWidth, 2);
                for (Item material : card.level.items) {
                    drawMaterialIcon(material, xMaterialPos, materialsY, calcWidth(material.image(), params.miniIconHeight), params.miniIconHeight, graphics2D);

                    xMaterialPos += calcWidth(material.image(), params.miniIconHeight);
                }

                xPos += card.renderWidth + X_GAP;
            }

            yPos += params.cardHeight + Y_GAP;
        }
    }

    private static List<Row> createRows(int width, List<Card> cards) {
        List<Row> rows = new ArrayList<>(List.of(new Row()));

        cards.sort(Comparator.<Card>comparingInt(card -> card.width).reversed());
        for (Card card : cards) {
            boolean placed = false;

            for (Row row : rows) {
                if (row.canFit(card.width, width)) {
                    row.addCard(card);
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                rows.add(new Row().addCard(card));
            }
        }
        return rows;
    }

    private static int getCanvasWidth(CharacterData character,
                                      Pair<CharacterStats, CharacterStats> statsPair,
                                      Map<Integer, Integer> materialMap,
                                      int maxMaterialWidth,
                                      int iconHeight,
                                      int minAvatarHeight,
                                      List<Card> cards) {
        int countColumns = ceilDiv(materialMap.size(), MAX_ROWS - 2);
        int materialsWidth = (maxMaterialWidth + X_GAP) * countColumns;
        int statsWidth = calculateStatsWidth(iconHeight, statsPair);
        int avatarWidth = calcWidth(character.getAvatar().toBufferedImage(), minAvatarHeight);
        int infoWidth = avatarWidth + max(materialsWidth, statsWidth) + 3 * X_GAP;
        int maxCardWidth = cards.stream()
            .mapToInt(card -> card.width)
            .max()
            .orElseThrow();

        return max(infoWidth, maxCardWidth + 2 * X_GAP);
    }

    private static List<Card> createCards(List<Level> levelList, int miniIconHeight) {
        Graphics2D g2d = new BufferedImage(1, 1, TYPE_INT_ARGB).createGraphics();

        try {
            List<Card> cards = new ArrayList<>();

            for (Level level : levelList) {
                String cost = getCardCost(level);
                String count = getCardCount(level);
                int nameWidth = g2d.getFontMetrics(FONT).stringWidth(level.name);
                int costWidth = g2d.getFontMetrics(MINI_FONT).stringWidth(cost);
                int countWidth = g2d.getFontMetrics(MINI_FONT).stringWidth(count);
                int headerWidth = nameWidth + 2 * max(costWidth, countWidth) + 4 * X_GAP;
                int iconsWidth = level.items.stream()
                    .map(Item::image)
                    .mapToInt(f -> calcWidth(f, miniIconHeight))
                    .sum() + 2 * X_GAP;
                int minWidth = max(iconsWidth, headerWidth);

                cards.add(new Card(level, minWidth));
            }
            return cards;
        } finally {
            g2d.dispose();
        }
    }

    private static Params getParams(int minAvatarHeight, int iconHeight, int miniIconHeight, int width, int maxMaterialWidth, int rowsCount) {
        Graphics2D g2d = new BufferedImage(1, 1, TYPE_INT_ARGB).createGraphics();

        try {
            int cardHeight = miniIconHeight + g2d.getFontMetrics(FONT).getHeight();
            int height = minAvatarHeight + 3 * Y_GAP + (Y_GAP + cardHeight) * rowsCount;

            return new Params(height, width, minAvatarHeight, iconHeight, miniIconHeight, cardHeight, maxMaterialWidth);
        } finally {
            g2d.dispose();
        }
    }

    private static String getInsightImagePath(Integer insight) {
        return switch (insight) {
            case 0 -> "reverse1999/insight/i_0.png";
            case 1 -> "reverse1999/insight/i_1.png";
            case 2 -> "reverse1999/insight/i_2.png";
            case 3 -> "reverse1999/insight/i_3.png";
            default -> throw new IllegalStateException("Unexpected value: " + insight);
        };
    }

    private static int calculateStatsWidth(int iconHeight, @NotNull Pair<CharacterStats, CharacterStats> statsPair) {
        BufferedImage arrowImage = loadImage("reverse1999/arrow-right.png");
        BufferedImage currentInsight = loadImage(getInsightImagePath(statsPair.getLeft().insight()));
        BufferedImage targetInsight = loadImage(getInsightImagePath(statsPair.getRight().insight()));
        int arrowImageWidth = calcWidth(arrowImage, divideExact(iconHeight, 2));
        int currentInsightWidth = calcWidth(currentInsight, iconHeight);
        int targetInsightWidth = calcWidth(targetInsight, iconHeight);

        return currentInsightWidth + arrowImageWidth + targetInsightWidth + 4 * X_GAP;
    }

    private static String getCardCount(Level level) {
        return level.count > 0 ? "x %s".formatted(DECIMAL_FORMAT.format(level.count)) : "";
    }

    private static String getCardCost(Level level) {
        return level.cost > 0 ? "AP %s".formatted(DECIMAL_FORMAT.format(level.cost)) : "";
    }

    @RequiredArgsConstructor(access = PRIVATE)
    private static class Card {
        private final Level level;
        private final int width;

        private int renderWidth;
    }

    @NoArgsConstructor(access = PRIVATE)
    private static class Row {
        private final List<Card> cards = new ArrayList<>();

        private int width = 0;

        private boolean canFit(int cardWidth, int maxWidth) {
            return width + cardWidth + (cards.isEmpty() ? 0 : X_GAP) <= maxWidth - 2 * X_GAP;
        }

        private Row addCard(Card card) {
            if (!cards.isEmpty()) {
                width += X_GAP;
            }
            cards.add(card);
            width += card.width;
            return this;
        }
    }

    private record Params(int height, int width, int minAvatarHeight, int iconHeight, int miniIconHeight, int cardHeight, int maxMaterialWidth) {
    }
}
