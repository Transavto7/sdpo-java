package ru.nozdratenko.sdpo.Inspections.Technical.Controllers;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Core.Network.ApiResponse;
import ru.nozdratenko.sdpo.Inspections.Technical.Services.TechnicalInspectionSenderService;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.helper.PrinterHelpers.PrinterHelper;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.util.Map;

@RestController
@RequestMapping("/technical/inspection")
@AllArgsConstructor
public class ReprintTechnicalInspection {
    private final PrinterHelper printerHelper;

    @PostMapping("/reprint")
    public ResponseEntity save() {
        try {
            this.printerHelper.printTechnical(this.printerHelper.getLastTechnicalPrint());

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Задание отправлено на принтер!", null));
        } catch (PrinterException e) {
            SdpoLog.error("PrinterException reprint technical inspection: " + e);
            return ResponseEntity.status(500).body(new ApiResponse<>(true, "Ошибка при отправке задания на принтер!", null));
        } catch (Exception e) {
            SdpoLog.error("Error create technical inspection: " + e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
