package ru.demetrious.deus.bot.app.impl.canvas;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.demetrious.deus.bot.domain.Image;
import ru.demetrious.deus.bot.domain.Image;
import ru.demetrious.deus.bot.domain.MessageFile;
import ru.demetrious.deus.bot.domain.reverse1999.CharacterData;

import static java.awt.Color.BLACK;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Double.MAX_VALUE;
import static java.lang.Integer.compare;
import static java.lang.Math.abs;
import static java.lang.Math.divideExact;
import static java.util.Arrays.stream;
import static java.util.Comparator.naturalOrder;
import static ru.demetrious.deus.bot.utils.ImageUtils.calcHeight;
import static ru.demetrious.deus.bot.utils.ImageUtils.calcWidth;
import static ru.demetrious.deus.bot.utils.ImageUtils.createWebp;
import static ru.demetrious.deus.bot.utils.ImageUtils.loadImage;

@Slf4j
public class ReverseCharactersCanvas implements Canvas {
    private static final int X_GAP = 10;
    private static final int Y_GAP = 10;

    @NotNull
    private final List<List<CharacterDrawable>> summonPowerList;
    private final Params params;
    private final BufferedImage canvas;
    private final Graphics2D graphics2D;

    public ReverseCharactersCanvas(List<CharacterDrawable> characterList) {
        int minHeight = characterList.stream()
            .map(CharacterData::getAvatar)
            .map(Image::toBufferedImage)
            .map(BufferedImage::getHeight)
            .min(Integer::compareTo)
            .orElseThrow();

        this.summonPowerList = arrangeCharacters(characterList, minHeight);
        this.params = getParams(characterList, minHeight);
        this.canvas = new BufferedImage(params.width, params.height, TYPE_INT_ARGB);
        this.graphics2D = canvas.createGraphics();
    }

    @Override
    public MessageFile createFile() {
        graphics2D.setColor(BLACK);
        graphics2D.fillRect(0, 0, params.width, params.height);

        int xOffset = X_GAP;
        int yOffset = Y_GAP;
        for (List<CharacterDrawable> characterList : summonPowerList) {
            for (CharacterDrawable character : characterList) {
                BufferedImage avatar = character.getAvatar().toBufferedImage();
                BufferedImage nameImage = character.getNameImage().toBufferedImage();
                BufferedImage portraitImage = character.getPortraitImage();
                int avatarWidth = calcWidth(avatar, params.minHeight);
                int nameImageHeight = calcHeight(nameImage, params.minWidth);
                int portraitImageHeight = calcHeight(portraitImage, params.minWidth);
                GradientPaint gradientPaint = new GradientPaint(xOffset, yOffset, character.getRarityColor(), xOffset, yOffset + divideExact(params.minHeight, 3), new Color(1, 1, 1, 0));

                graphics2D.setPaint(gradientPaint);
                graphics2D.fillRect(xOffset, yOffset, avatarWidth, params.minHeight);
                graphics2D.drawImage(avatar, xOffset, yOffset, avatarWidth, params.minHeight, new Color(1, 1, 1, 0.1f), null);
                graphics2D.drawImage(nameImage, xOffset, yOffset + params.minHeight - nameImageHeight, params.minWidth, nameImageHeight, null);
                graphics2D.drawImage(portraitImage, xOffset + avatarWidth - params.minWidth, yOffset + params.minHeight - portraitImageHeight, params.minWidth, portraitImageHeight, null);

                xOffset += avatarWidth + Y_GAP;
            }
            xOffset = X_GAP;
            yOffset += params.minHeight + Y_GAP;
        }

        graphics2D.dispose();
        return new MessageFile()
            .setName("reverse-characters.webp")
            .setData(createWebp(canvas));
    }

    @Getter
    public static class CharacterDrawable extends CharacterData {
        private final BufferedImage portraitImage;

        public CharacterDrawable(CharacterData character, Integer portrait) {
            this.setRarity(character.getRarity());
            this.setAvatar(character.getAvatar());
            this.setNameImage(character.getNameImage());
            this.setName(character.getName());
            this.portraitImage = loadImage(getPortraitImagePath(portrait));
        }
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private Params getParams(List<CharacterDrawable> characterList, int minHeight) {
        int minWidth = characterList.stream()
            .map(CharacterData::getAvatar)
            .map(image -> calcWidth(image.toBufferedImage(), minHeight))
            .min(Integer::compareTo)
            .orElseThrow();
        int height = summonPowerList.size() * (minHeight + Y_GAP) + Y_GAP;
        int maxRows = summonPowerList.stream()
            .mapToInt(List::size)
            .max()
            .orElseThrow();
        int width = summonPowerList.stream()
            .map(list -> list.stream()
                .map(CharacterData::getAvatar)
                .mapToInt(image -> calcWidth(image.toBufferedImage(), minHeight))
                .sum())
            .max(naturalOrder())
            .map(maxWidth -> maxWidth + X_GAP * (1 + maxRows))
            .orElseThrow();

        return new Params(height, width, minHeight, minWidth);
    }

    private record Params(int height, int width, int minHeight, int minWidth) {
    }

    @NotNull
    private static List<List<CharacterDrawable>> arrangeCharacters(List<CharacterDrawable> characterList, int minHeight) {
        List<List<CharacterDrawable>> result = new ArrayList<>();
        double minDiff = MAX_VALUE;
        double targetRatio = 9. / 16.;

        characterList.sort((a, b) -> compare(calcWidth(b.getAvatar().toBufferedImage(), minHeight), calcWidth(a.getAvatar().toBufferedImage(), minHeight)));

        for (int rows = 1; rows <= characterList.size(); rows++) {
            List<List<CharacterDrawable>> rowsList = new ArrayList<>();
            int[] rowsWidth = new int[rows];

            for (int i = 0; i < rows; i++) {
                rowsList.add(new ArrayList<>());
            }

            characterList.forEach(character -> {
                int shortestRow = findShortestRow(rowsWidth);

                rowsList.get(shortestRow).add(character);
                rowsWidth[shortestRow] += character.getAvatar().toBufferedImage().getWidth();
            });

            int maxRowWidth = stream(rowsWidth).max().orElseThrow();
            double ratio = (double) (rows * minHeight) / maxRowWidth;
            double diff = abs(targetRatio - ratio);

            if (diff < minDiff) {
                minDiff = diff;
                result = rowsList;
            }
        }

        return result;
    }

    private static int findShortestRow(int[] rowsWidth) {
        int minIndex = 0;
        for (int i = 1; i < rowsWidth.length; i++) {
            if (rowsWidth[i] < rowsWidth[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    private static String getPortraitImagePath(Integer count) {
        return switch (count) {
            case 1 -> "reverse1999/portrait/c_0.png";
            case 2 -> "reverse1999/portrait/c_1.png";
            case 3 -> "reverse1999/portrait/c_2.png";
            case 4 -> "reverse1999/portrait/c_3.png";
            case 5 -> "reverse1999/portrait/c_4.png";
            case 6 -> "reverse1999/portrait/c_5.png";
            default -> throw new IllegalStateException("Unexpected value: " + count);
        };
    }
}
