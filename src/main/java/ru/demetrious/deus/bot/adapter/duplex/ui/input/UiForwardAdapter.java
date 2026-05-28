package ru.demetrious.deus.bot.adapter.duplex.ui.input;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class UiForwardAdapter {
    @GetMapping(value = "/ui/**")
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
