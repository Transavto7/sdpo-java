package ru.nozdratenko.sdpo.Settings.Services;

import lombok.AllArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AppVersionService {
    private final Optional<BuildProperties> buildProperties;

    public String getVersion() {
        return buildProperties
                .map(BuildProperties::getVersion)
                .orElse("unknown");
    }

    public String getName() {
        return buildProperties
                .map(BuildProperties::getName)
                .orElse("sdpo-java");
    }
}
