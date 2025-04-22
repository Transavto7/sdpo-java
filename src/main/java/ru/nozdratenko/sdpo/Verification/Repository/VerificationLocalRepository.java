package ru.nozdratenko.sdpo.Verification.Repository;

import org.springframework.stereotype.Repository;
import ru.nozdratenko.sdpo.Verification.Entities.Verification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class VerificationLocalRepository implements VerificationRepository {
    private final Map<UUID, Verification> storage = new HashMap<>();

    public void add(Verification verification){
        storage.put(verification.getId(), verification);
    }

    public void save(Verification verification){
        storage.put(verification.getId(), verification);
    }

    public void remove(UUID verificationId){
        storage.remove(verificationId);
    }

    public Verification get(UUID verificationId){
        return storage.get(verificationId);
    }
}
