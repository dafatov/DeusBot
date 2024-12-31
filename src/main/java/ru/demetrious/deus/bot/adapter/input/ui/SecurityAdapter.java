package ru.demetrious.deus.bot.adapter.input.ui;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@RestController
public class SecurityAdapter {
    @RequestMapping(produces = TEXT_HTML_VALUE)
    public ModelAndView home() {
        return new ModelAndView("success");
    }
}
