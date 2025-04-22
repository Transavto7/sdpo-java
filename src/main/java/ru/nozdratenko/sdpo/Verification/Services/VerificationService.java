package ru.nozdratenko.sdpo.Verification.Services;

import ru.nozdratenko.sdpo.Verification.Entities.Verification;
import ru.nozdratenko.sdpo.exception.ApiException;

import java.io.IOException;
import java.util.UUID;

public interface VerificationService {
    Verification createVerification() throws IOException, ApiException;
    boolean verify(UUID verificationId, String code) throws IOException, ApiException;
    void addAttempt(UUID verificationId) throws IOException, ApiException;
}
