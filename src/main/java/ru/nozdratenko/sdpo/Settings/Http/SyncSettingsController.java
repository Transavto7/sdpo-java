package ru.nozdratenko.sdpo.Settings.Http;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.Settings.Queries.GetSettingsQuery;

@Controller
@AllArgsConstructor
public class SyncSettingsController {
    private final Sdpo sdpo;
    private final GetSettingsQuery query;

    @PostMapping("/setting/sync")
    public ResponseEntity syncSettings() {
        this.sdpo.initSettings();
        return ResponseEntity.status(HttpStatus.OK).body(this.query.handle().toMap());
    }
}
