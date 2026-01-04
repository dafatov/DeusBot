package ru.demetrious.deus.bot.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import lombok.experimental.UtilityClass;
import ru.demetrious.deus.bot.domain.reverse1999.Item;

import static java.awt.Color.WHITE;
import static java.lang.Math.divideExact;
import static ru.demetrious.deus.bot.utils.SpellUtils.formatWithHint;

@UtilityClass
public class CanvasUtils {
    public static void drawCenteredText(String text, int centerX, int centerY, int maxWidth, Graphics2D g2d) {
        Font originalFont = g2d.getFont();
        Font currentFont = originalFont;
        FontMetrics fontMetrics = g2d.getFontMetrics(currentFont);
        int textWidth = fontMetrics.stringWidth(text);

        if (textWidth > maxWidth) {
            int currentSize = currentFont.getSize();
            int minFontSize = 10;

            while (textWidth > maxWidth && currentSize > minFontSize) {
                currentFont = new Font(currentFont.getName(), currentFont.getStyle(), --currentSize);
                fontMetrics = g2d.getFontMetrics(currentFont);
                textWidth = fontMetrics.stringWidth(text);
            }

            if (textWidth > maxWidth && currentSize == minFontSize) {
                text = "...";
                textWidth = fontMetrics.stringWidth(text);
            }
        }

        g2d.setFont(currentFont);
        g2d.drawString(text, centerX - divideExact(textWidth, 2), centerY - g2d.getFontMetrics(currentFont).getDescent());
        g2d.setFont(originalFont);
    }

    public static void drawMaterialIcon(Item material, int xPos, int yPos, int materialWidth, int iconHeight, Graphics2D g2d) {
        g2d.drawImage(material.image(), xPos, yPos, materialWidth, iconHeight, null);
        g2d.setColor(new Color(0, 0, 0, 0.5f));
        g2d.fillRect(xPos, yPos, materialWidth, iconHeight);
        g2d.setColor(WHITE);
        drawCenteredText(formatWithHint(material.amount()), xPos + divideExact(materialWidth, 2),
            yPos + iconHeight, materialWidth, g2d);
    }
}
