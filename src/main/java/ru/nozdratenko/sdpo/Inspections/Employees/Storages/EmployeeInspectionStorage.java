package ru.nozdratenko.sdpo.Inspections.Employees.Storages;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.lang.Nullable;
import ru.nozdratenko.sdpo.Core.FileSystem.FileBase;
import ru.nozdratenko.sdpo.Inspections.Employees.Enums.EmployeeInspectionType;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EmployeeInspectionStorage extends FileBase {
    public JSONArray store = new JSONArray();
    public EmployeeInspectionStorage() {
        this("storage/employee_inspections.json");
    }

    public EmployeeInspectionStorage(String path) {
        super(path);

        try {
            String str = read();
            if (!str.isEmpty()) {
                this.store = new JSONArray(read());
            }
        } catch (Exception e) {
            SdpoLog.error(e);
        }
    }

    public void putInspection(JSONObject inspection) {
        store.put(inspection);
    }

    public int count() {
        return this.store.length();
    }

    public void save() {
        try {
            this.create();
            this.writeFile(store.toString(1));
        } catch (IOException e) {
            SdpoLog.error(e);
        }
    }

    public JSONObject getInspection(int index) {
        return this.store.getJSONObject(index);
    }

    public void remove(int index) {
        this.store.remove(index);
    }

    @Nullable
    public EmployeeInspectionType getLastInspectionTypeForEmployee(Object value) {
        for (int i = this.store.length() - 1; i >= 0; i--) {
            JSONObject inspection = this.store.getJSONObject(i);

            if (inspection.has("person_id") && inspection.get("person_id").toString().equals(value.toString())) {
                DateTimeFormatter barFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDate inspectionDate = LocalDate.parse(inspection.getString("created_at"), barFormatter);
                if (LocalDate.now().isEqual(inspectionDate)) {
                    try {
                        return EmployeeInspectionType.fromTitle(inspection.getString("type_anketa"));
                    } catch (Exception e) {
                        SdpoLog.info("Failed to parse type_anketa! Setting default type!");
                        SdpoLog.error(e);

                        return null;
                    }
                }
            }
        }

        return null;
    }
}
