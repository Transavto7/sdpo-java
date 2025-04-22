package ru.nozdratenko.sdpo.Verification.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Verification {
    private final UUID id;
    private final String code;
    private LocalDateTime expireDate;
    private int attempts = 0;
}
