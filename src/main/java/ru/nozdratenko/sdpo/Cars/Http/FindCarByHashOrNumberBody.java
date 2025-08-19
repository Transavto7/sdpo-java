package ru.nozdratenko.sdpo.Cars.Http;

import lombok.Data;

@Data
public class FindCarByHashOrNumberBody {
    private String needle;
}
