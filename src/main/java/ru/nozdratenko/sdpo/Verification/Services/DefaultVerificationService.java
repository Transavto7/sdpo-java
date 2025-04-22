package ru.nozdratenko.sdpo.Verification.Services;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Core.Network.Request;
import ru.nozdratenko.sdpo.Verification.Entities.Verification;
import ru.nozdratenko.sdpo.exception.ApiException;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("production")
public class DefaultVerificationService implements VerificationService {
    public Verification createVerification() throws IOException, ApiException {
        Request response = new Request("sdpo/verification");
        String result = response.sendPost();
        JSONObject resultJson = new JSONObject(result);
        SdpoLog.info("!!! Verification object: " + resultJson);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return new Verification(
            UUID.fromString(resultJson.getString("id")),
            resultJson.getString("code"),
            LocalDateTime.parse(resultJson.getString("next_try_at"), formatter),
            resultJson.getInt("attempts")
        );
    }
    
    public boolean verify(UUID verificationId, String code) throws IOException, ApiException {
        Request response = new Request("sdpo/verification");
        String result = response.sendGet(Map.of("id", verificationId.toString(), "code", code));
        JSONObject resultJson = new JSONObject(result);

        SdpoLog.info("!!! Verification result: " + resultJson + " for verification id: " + verificationId.toString() + " code: " + code);

        return resultJson.getBoolean("verify");
    }

    public void addAttempt(UUID verificationId) throws IOException, ApiException {
        Request response = new Request("sdpo/verification/retry");
        response.sendPost((new JSONObject()).put("id", verificationId.toString()).toString());
        SdpoLog.info("!!! Verification add attempt id: " + verificationId);
    }
}
