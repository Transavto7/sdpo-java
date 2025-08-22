package ru.nozdratenko.sdpo.Inspections.Technical.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Inspections.Technical.Controllers.Bodies.FindCarByHashOrNumberBody;
import ru.nozdratenko.sdpo.Inspections.Technical.Controllers.ViewModels.Car;
import ru.nozdratenko.sdpo.Core.Network.ApiResponse;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CarController {

    @PostMapping("api/car/by-number-or-hash")
    public ResponseEntity<ApiResponse<?>> getCarByNumberOrHash(@RequestBody FindCarByHashOrNumberBody request) throws IOException {
        if (Sdpo.isConnection()) {
            Request response = new Request("sdpo/cars/by-hash-or-number");
            try {
                Map<String, String> params = new HashMap<>();
                params.put("needle", request.getNeedle());

                String result = response.sendGet(params);
                Car car = new ObjectMapper().readValue(result, Car.class);

                return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Авто успешно получено", car)
                );
            } catch (ApiException e) {
                SdpoLog.error(e);
                return ResponseEntity.status(303).body(new ApiResponse<>(false, null, e.getResponse().toMap()));
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse<>(false, "Отсутствует подключение к сети", null));
    }

    @GetMapping("api/car/{id}")
    public ResponseEntity getCar(@PathVariable String id) throws IOException {
        if (Sdpo.isConnection()) {
            Request response = new Request("sdpo/car/" + id);
            try {
                String result = response.sendGet();
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } catch (ApiException e) {
                SdpoLog.error(e);
                return ResponseEntity.status(303).body(e.getResponse().toMap());
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Отсутствует подключение к сети");
        return ResponseEntity.status(500).body(jsonObject);
    }

}
