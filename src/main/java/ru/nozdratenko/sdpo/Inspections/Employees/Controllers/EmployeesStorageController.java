package ru.nozdratenko.sdpo.Inspections.Employees.Controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nozdratenko.sdpo.Sdpo;

@RestController
@RequestMapping("/employees/inspection")
@AllArgsConstructor
public class EmployeesStorageController {

    @GetMapping("/local/list")
    public ResponseEntity inspectionsList() {
        return ResponseEntity.ok(Sdpo.employeeInspectionStorage.getStore().toList());
    }
}
