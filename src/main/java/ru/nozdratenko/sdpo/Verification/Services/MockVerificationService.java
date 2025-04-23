package ru.nozdratenko.sdpo.Verification.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Verification.Entities.Verification;
import ru.nozdratenko.sdpo.Verification.Repository.VerificationRepository;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("develop")
public class MockVerificationService implements VerificationService {
    private final VerificationRepository verificationRepository;

    public Verification createVerification(String driverId){
        Verification verification = new Verification(
            UUID.randomUUID(),
            "123456",
            LocalDateTime.now(),
            0
        );
        verificationRepository.add(verification);
        SdpoLog.info("!!! Verification object: " + verification);

        return verification;
    }
    
    public boolean verify(UUID verificationId, String code){
        Verification verification = verificationRepository.get(verificationId);
        if (verification == null){
            throw new VerificationNotFoundException("Verification not found");
        }
        SdpoLog.info("!!! Verify verification object: " + verification);

        return verification.getCode().equals(code);
    }

    public void addAttempt(UUID verificationId){
        Verification verification = verificationRepository.get(verificationId);
        if (verification == null){
            throw new VerificationNotFoundException("Verification not found");
        }

        verification.setAttempts(verification.getAttempts() + 1);
        verification.setExpireDate(LocalDateTime.now().plusMinutes(10));
        verificationRepository.save(verification);
        SdpoLog.info("!!! AddAttempt verification object: " + verification);
    }
}
