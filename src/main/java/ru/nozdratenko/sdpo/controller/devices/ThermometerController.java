package ru.nozdratenko.sdpo.controller.devices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.task.Termometer.ThermometerResultTask;
import ru.nozdratenko.sdpo.task.Termometer.ThermometerTaskRunner;

@RestController
public class ThermometerController {
    private final ThermometerTaskRunner thermometerTaskRunner;

    @Autowired
    public ThermometerController(ThermometerTaskRunner thermometerTaskRunner) {
        this.thermometerTaskRunner = thermometerTaskRunner;
    }

    @PostMapping(value = "/device/thermometer")
    @ResponseBody
    public ResponseEntity thermometer() {
        ThermometerResultTask task = this.thermometerTaskRunner.getThermometerResultTask();

        if (task.exist()) {
            double result = task.result;
            task.clear();
            return ResponseEntity.ok().body(result);
        }

        return ResponseEntity.ok().body("next");
    }
}
