package ru.nozdratenko.sdpo.Verification.Repository;

import ru.nozdratenko.sdpo.Verification.Entities.Verification;

import java.util.UUID;

public interface VerificationRepository {
    void add(Verification verification);
    void save(Verification verification);
    void remove(UUID verificationId);
    Verification get(UUID verificationId);
}
