package ru.nozdratenko.sdpo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.network.Request;

import java.io.IOException;

@RestController
public class InspectionController {

    @PostMapping("inspection/{id}")
    public ResponseEntity inspectionStart(@PathVariable String id) throws IOException {
        Request response = new Request("check-prop/hash_id/Driver/" + id);
        return ResponseEntity.status(HttpStatus.OK).body(response.sendGet());
    }

}
