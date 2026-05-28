package ru.demetrious.deus.bot.adapter.output.repository.pack;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.demetrious.deus.bot.app.api.game.codenames.DeleteCodeNamesGamePackOutbound;
import ru.demetrious.deus.bot.app.api.game.codenames.GetCodeNamesGamePackWordsOutbound;
import ru.demetrious.deus.bot.app.api.game.codenames.GetCodeNamesGamePacksOutbound;
import ru.demetrious.deus.bot.app.api.game.codenames.SaveCodeNamesGamePacksOutbound;
import ru.demetrious.deus.bot.domain.game.Pack;
import ru.demetrious.deus.bot.domain.game.Word;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Transactional
@RequiredArgsConstructor
@Component
public class PackAdapter implements GetCodeNamesGamePacksOutbound, SaveCodeNamesGamePacksOutbound, DeleteCodeNamesGamePackOutbound, GetCodeNamesGamePackWordsOutbound {
    private final PackRepository packRepository;
    private final WordRepository wordRepository;

    @Override
    public void deletePack(Long id) {
        packRepository.deleteById(id);
    }

    @Override
    public List<Pack> getPacks() {
        return packRepository.findAll();
    }

    @Override
    public void savePack(Pack pack) {
        Map<String, Word> wordMap = wordRepository.findAll().stream()
            .collect(toMap(Word::getText, identity()));
        Set<Word> wordSet = pack.getWords().stream()
            .map(word -> wordMap.getOrDefault(word.getText(), word))
            .collect(toSet());

        packRepository.save(pack.setWords(wordSet));
    }

    @Override
    public Set<String> getWords(Long id) {
        return packRepository.findById(id)
            .map(Pack::getWords)
            .orElseThrow(() -> new IllegalArgumentException("No words found for id " + id)).stream()
            .map(Word::getText)
            .collect(toSet());
    }
}
