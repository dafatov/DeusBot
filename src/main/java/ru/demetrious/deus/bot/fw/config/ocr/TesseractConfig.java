package ru.demetrious.deus.bot.fw.config.ocr;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static net.sourceforge.tess4j.ITessAPI.TessPageSegMode.PSM_SINGLE_CHAR;
import static net.sourceforge.tess4j.util.LoadLibs.extractTessResources;

@Configuration
public class TesseractConfig {
    @Bean
    public Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();

        tesseract.setDatapath(extractTessResources("tesseract").getAbsolutePath());
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(PSM_SINGLE_CHAR);
        tesseract.setVariable("tessedit_char_whitelist", "0123456789");
        tesseract.setVariable("classify_bln_numeric_mode", "1");
        tesseract.setVariable("tessedit_char_blacklist", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ| ");
        tesseract.setVariable("user_defined_dpi", "300");

        return tesseract;
    }
}
