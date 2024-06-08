package ru.nozdratenko.sdpo.controller.device;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.task.ThermometerResultTask;

@RestController
public class ThermometerController {

    @PostMapping(value = "/device/thermometer")
    @ResponseBody
    public ResponseEntity thermometer() {
        ThermometerResultTask task = Sdpo.thermometerResultTask;

        if (task.exist()) {
            double result = task.result;
            task.clear();
            return ResponseEntity.ok().body(result);
        }

        return ResponseEntity.ok().body("next");
    }
}
