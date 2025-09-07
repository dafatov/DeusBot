package ru.demetrious.deus.bot.adapter.output.google;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final ReverseDataMapper reverseDataMapper;
    private final GoogleDriveClient googleDriveClient;

    @Override
    public Optional<PullsData> findPullsData() {
        return googleDriveClient.getFiles().files().stream()
            .findFirst()
            .map(file -> {
                FILE_ID.set(file.id());
                return googleDriveClient.getFile(file.id());
            })
            .map(reverseDataMapper::map);
    }

    @Override
    public void updatePullsData(PullsData pullsData) {
        JsonToMultipartFile file = new JsonToMultipartFile(writeValueAsString(pullsData), "example.json");
        String id = FILE_ID.get();

        FILE_ID.remove();
        if (isNull(id)) {
            googleDriveClient.uploadFile(new JsonToMultipartFile("{\"name\":\"example.json\",\"parents\":[\"appDataFolder\"]}", "metadata"), file);
        } else {
            googleDriveClient.updateFile(id, new JsonToMultipartFile("{}", "metadata"), file);
        }
    }
}
