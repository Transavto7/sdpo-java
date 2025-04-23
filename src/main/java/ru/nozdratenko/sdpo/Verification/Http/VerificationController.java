package ru.nozdratenko.sdpo.Verification.Http;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.Verification.Entities.Verification;
import ru.nozdratenko.sdpo.Verification.Services.VerificationService;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class VerificationController {
    private final VerificationService verificationService;

    @GetMapping("/inspection/verify")
    @ResponseBody
    public ResponseEntity<Verification> getVerification(@RequestParam("driver_id") String driverId) throws IOException, ApiException {
        return ResponseEntity.status(HttpStatus.OK).body(verificationService.createVerification(driverId));
    }

    @PostMapping("/inspection/verify")
    @ResponseBody
    public boolean checkVerification(@RequestBody Map<String, String> json) throws IOException, ApiException {
        return verificationService.verify(
            UUID.fromString(json.get("verification_id")),
            json.get("code")
        );
    }

    @PostMapping("/inspection/verify/retry/{verifyId}")
    @ResponseBody
    public void retry(@PathVariable UUID verifyId) throws IOException, ApiException {
        verificationService.addAttempt(verifyId);
    }
}
