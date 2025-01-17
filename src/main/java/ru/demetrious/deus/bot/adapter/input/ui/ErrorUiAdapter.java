package ru.demetrious.deus.bot.adapter.input.ui;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.boot.web.error.ErrorAttributeOptions.defaults;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@RequiredArgsConstructor
@RestController
public class ErrorUiAdapter implements ErrorController {
    private final ErrorAttributes errorAttributes;

    @GetMapping(value = "/error", produces = TEXT_HTML_VALUE)
    public ModelAndView getErrorPage(HttpServletRequest request) {
        return new ModelAndView("error", errorAttributes.getErrorAttributes(new ServletWebRequest(request), defaults()));
    }
}
