package ru.demetrious.deus.bot.app.impl.canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import ru.demetrious.deus.bot.domain.Character;
import ru.demetrious.deus.bot.domain.MessageFile;
import ru.demetrious.deus.bot.domain.Pull;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.awt.Font.BOLD;
import static java.awt.Font.PLAIN;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.String.valueOf;
import static java.time.Instant.now;
import static java.time.ZoneId.of;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static javax.imageio.ImageIO.write;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class ReversePullTypeCanvas implements Canvas {
    private static final Font FONT = new Font("SansSerif", PLAIN, 16);
    private static final Font FONT_TITLE = new Font("SansSerif", BOLD, 32);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final ZoneId ZONE_ID = of("Europe/Moscow");
    private static final int X_GAP = 10;
    private static final int Y_GAP = 10;

    @NotNull
    private final List<Pull> summonList;
    @NotNull
    private final Map<Integer, Character> characterMap;
    @NotNull
    private final String poolTypeName;
    private final Params params;
    private final BufferedImage canvas;
    private final Graphics2D graphics2D;
    private final FontMetrics fontMetrics;

    public ReversePullTypeCanvas(@NotNull List<Pull> summonList, @NotNull Map<Integer, Character> characterMap, @NotNull Integer poolType) {
        this.summonList = summonList;
        this.characterMap = characterMap;
        this.poolTypeName = getPoolTypeName(poolType);
        this.params = getParams();
        this.canvas = new BufferedImage(params.width, params.height, TYPE_INT_ARGB);
        this.graphics2D = canvas.createGraphics();
        this.fontMetrics = graphics2D.getFontMetrics(FONT);
    }

    @Override
    public MessageFile createFile() {
        summonList.sort(comparing(Pull::getTime));

        graphics2D.setColor(BLACK);
        graphics2D.fillRect(0, 0, params.width, params.height);
        graphics2D.setColor(WHITE);

        graphics2D.setFont(FONT_TITLE);
        graphics2D.drawString(poolTypeName,
            (params.width - graphics2D.getFontMetrics().stringWidth(poolTypeName)) / 2,
            Y_GAP + graphics2D.getFontMetrics().getAscent());
        graphics2D.setFont(FONT);

        int yOffset = params.height - Y_GAP - fontMetrics.getHeight();
        Map<CounterType, Integer> counter = new EnumMap<>(CounterType.class);
        for (Pull pull : summonList) {
            int groupYOffset = yOffset;
            for (Integer summonId : pull.getSummonIdList()) {
                Character character = characterMap.get(summonId);
                int xOffset = 3 * X_GAP;

                stream(CounterType.values()).forEach(counterType -> counter.merge(counterType, 1, Integer::sum));

                graphics2D.setColor(new Color(1, 1, 1, 0.2f));
                graphics2D.drawLine(
                    xOffset,
                    yOffset + fontMetrics.getAscent() + fontMetrics.getDescent(),
                    params.width - X_GAP,
                    yOffset + fontMetrics.getAscent() + fontMetrics.getDescent()
                );
                graphics2D.setColor(WHITE);

                graphics2D.setColor(getColor(character.getRarity()));
                xOffset = drawStringInline(character.getName(), xOffset, yOffset, params.maxNameWidth);
                xOffset = drawStringInline(pull.getTime().atZone(ZONE_ID).format(DATE_TIME_FORMATTER), xOffset, yOffset, params.maxInstantWidth);
                xOffset = switch (character.getRarity()) {
                    case 6 -> drawCounterInline(valueOf(counter.remove(CounterType.STAR_6)), xOffset, yOffset);
                    case 5 -> drawCounterInline(valueOf(counter.remove(CounterType.STAR_5)), xOffset, yOffset);
                    case null, default -> drawCounterInline(EMPTY, xOffset, yOffset);
                };
                drawCounterInline(valueOf(counter.get(CounterType.ALL)), xOffset, yOffset);
                graphics2D.setColor(WHITE);

                yOffset -= fontMetrics.getHeight() + Y_GAP;
            }

            if (!"1".equalsIgnoreCase(pull.getType())) {
                int midYStart = groupYOffset + fontMetrics.getHeight() / 2;
                int midYEnd = yOffset + 3 * fontMetrics.getHeight() / 2 + Y_GAP;

                graphics2D.drawLine(X_GAP, midYStart, 2 * X_GAP, midYStart);
                graphics2D.drawLine(X_GAP, midYStart, X_GAP, midYEnd);
                graphics2D.drawLine(X_GAP, midYEnd, 2 * X_GAP, midYEnd);
            }
        }

        graphics2D.dispose();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            write(canvas, "png", byteArrayOutputStream);
            return new MessageFile()
                .setName("reverse-pulls-type-%s.png".formatted(poolTypeName.replaceAll("\\W", "_")))
                .setData(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private int drawCounterInline(String counter, int xOffset, int yOffset) {
        return drawStringInline(counter, xOffset, yOffset, params.maxSizeWidth, true);
    }

    private int drawStringInline(String string, int xOffset, int yOffset, int maxStringWidth) {
        return drawStringInline(string, xOffset, yOffset, maxStringWidth, false);
    }

    private int drawStringInline(String string, int xOffset, int yOffset, int maxStringWidth, boolean withPadding) {
        graphics2D.drawString(string, withPadding ? (xOffset + maxStringWidth - fontMetrics.stringWidth(string)) : xOffset, yOffset + fontMetrics.getAscent());
        return xOffset + maxStringWidth + X_GAP;
    }

    private Params getParams() {
        BufferedImage test = new BufferedImage(1, 1, TYPE_INT_RGB);
        Graphics2D graphics2D = test.createGraphics();
        FontMetrics fontMetrics = graphics2D.getFontMetrics(FONT);
        graphics2D.dispose();

        int fullSize = summonList.stream()
            .map(Pull::getSummonIdList)
            .mapToInt(List::size)
            .sum();
        int maxNameWidth = characterMap.values().stream()
            .map(Character::getName)
            .map(fontMetrics::stringWidth)
            .max(Integer::compareTo)
            .orElse(0);
        int maxInstantWidth = fontMetrics.stringWidth(now().atZone(ZONE_ID).format(DATE_TIME_FORMATTER));
        int maxSizeWidth = fontMetrics.stringWidth(valueOf(fullSize));
        int titleHeight = graphics2D.getFontMetrics(FONT_TITLE).getAscent();
        int width = 7 * X_GAP + maxNameWidth + maxInstantWidth + 2 * maxSizeWidth;
        int height = fullSize * (Y_GAP + fontMetrics.getHeight()) + 3 * Y_GAP + titleHeight;

        return new Params(height, width, maxInstantWidth, maxNameWidth, maxSizeWidth);
    }

    private record Params(int height, int width, int maxInstantWidth, int maxNameWidth, int maxSizeWidth) {
    }

    private enum CounterType {
        STAR_5,
        STAR_6,
        ALL,
    }

    private static @NotNull Color getColor(@NotNull Integer rarity) {
        return switch (rarity) {
            case 2 -> new Color(69, 93, 68);
            case 3 -> new Color(84, 96, 127);
            case 4 -> new Color(114, 91, 128);
            case 5 -> new Color(183, 156, 92);
            case 6 -> new Color(205, 117, 32);
            default -> throw new IllegalStateException("Unexpected value: " + rarity);
        };
    }

    private static @NotNull String getPoolTypeName(@NotNull Integer poolType) {
        return switch (poolType) {
            case 1 -> "Starter Banner";
            case 2 -> "Regular Banner";
            case 3 -> "Time-limited Banner";
            case 12 -> "Ripples on the Water";
            case 21 -> "Collaboration Banner";
            default -> throw new IllegalStateException("Unexpected value: " + poolType);
        };
    }
}
