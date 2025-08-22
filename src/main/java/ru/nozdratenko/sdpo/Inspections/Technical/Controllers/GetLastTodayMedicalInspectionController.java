package ru.nozdratenko.sdpo.Inspections.Technical.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.nozdratenko.sdpo.Core.Network.ApiResponse;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.Inspections.Technical.Controllers.Bodies.FindMedicalInspectionByDriverIdBody;
import ru.nozdratenko.sdpo.Inspections.Technical.Controllers.ViewModels.LastMedicalInspection;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class GetLastTodayMedicalInspectionController {

    @PostMapping("api/medical/by-driver-hash")
    public ResponseEntity<ApiResponse<?>> getCarByNumberOrHash(@RequestBody FindMedicalInspectionByDriverIdBody request) throws IOException {
        if (Sdpo.isConnection()) {
            Request response = new Request("sdpo/medical-inspections/by-driver-hash");
            try {
                Map<String, String> params = new HashMap<>();
                params.put("driver_hash_id", request.getDriverHashId());

                String result = response.sendGet(params);
                LastMedicalInspection lastMedicalInspection = new ObjectMapper().readValue(result, LastMedicalInspection.class);

                return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Последнее МО успешно получено", lastMedicalInspection)
                );
            } catch (ApiException e) {
                SdpoLog.error(e);
                return ResponseEntity.status(303).body(new ApiResponse<>(false, null, e.getResponse().toMap()));
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse<>(false, "Отсутствует подключение к сети", null));
    }

}
