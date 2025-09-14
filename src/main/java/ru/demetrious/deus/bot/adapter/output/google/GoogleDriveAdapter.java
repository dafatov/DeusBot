package ru.demetrious.deus.bot.adapter.output.google;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.demetrious.deus.bot.adapter.output.google.dto.JsonToMultipartFile;
import ru.demetrious.deus.bot.adapter.output.google.mapper.ReverseDataMapper;
import ru.demetrious.deus.bot.app.api.pull.FindPullsDataOutbound;
import ru.demetrious.deus.bot.app.api.pull.UpdatePullsDataOutbound;
import ru.demetrious.deus.bot.domain.PullsData;

import static java.util.Objects.isNull;
import static ru.demetrious.deus.bot.utils.JacksonUtils.writeValueAsString;

@Slf4j
@RequiredArgsConstructor
@Component
public class GoogleDriveAdapter implements FindPullsDataOutbound, UpdatePullsDataOutbound {
    private static final ThreadLocal<String> FILE_ID = new ThreadLocal<>();
    private static final String FILE_NAME = "reverse1999-pulls-data.json";

    private final ReverseDataMapper reverseDataMapper;
    private final GoogleDriveClient googleDriveClient;

    @Override
    public Optional<PullsData> findPullsData() {
        return googleDriveClient.getFiles().files().stream()
            .filter(file -> StringUtils.equals(FILE_NAME, file.name()))
            .findFirst()
            .map(file -> {
                FILE_ID.set(file.id());
                return googleDriveClient.getFile(file.id());
            })
            .map(reverseDataMapper::map);
    }

    @Override
    public void updatePullsData(PullsData pullsData) {
        JsonToMultipartFile file = new JsonToMultipartFile(writeValueAsString(pullsData), FILE_NAME);
        String id = FILE_ID.get();

        FILE_ID.remove();
        if (isNull(id)) {
            googleDriveClient.uploadFile(new JsonToMultipartFile("{\"name\":\"%s\",\"parents\":[\"appDataFolder\"]}".formatted(FILE_NAME), "metadata"), file);
        } else {
            googleDriveClient.updateFile(id, new JsonToMultipartFile("{}", "metadata"), file);
        }
    }
}
