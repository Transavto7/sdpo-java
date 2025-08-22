package ru.nozdratenko.sdpo.Inspections.Technical.Controllers.Bodies;

import lombok.Data;

@Data
public class FindCarByHashOrNumberBody {
    private String needle;
}
