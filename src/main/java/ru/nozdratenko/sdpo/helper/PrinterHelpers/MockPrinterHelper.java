package ru.nozdratenko.sdpo.helper.PrinterHelpers;

import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.task.print.PrintQrRotateTask;
import ru.nozdratenko.sdpo.task.print.PrintQrTask;
import ru.nozdratenko.sdpo.task.print.PrintTask;
import ru.nozdratenko.sdpo.task.print.TechnicalPrintTask;
import ru.nozdratenko.sdpo.util.SdpoLog;

import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.PrinterResolution;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Profile("develop")
public class MockPrinterHelper implements PrinterHelper {
    public String head = null;
    public String licence = null;
    @Getter
    public JSONObject lastPrint = null;
    @Getter
    public JSONObject lastTechnicalPrint = null;
    @Getter
    @Setter
    public String lastQRPath = "";

    public void print(JSONObject json) throws PrintException, IOException, ru.nozdratenko.sdpo.exception.PrinterException {
        lastPrint = json;

        String name = "Неизвестный водитель";
        String result = "прошел";
        String type = "Предрейсовый/Предсменный";
        String admit = "допущен";
        String date = "0000-00-00 00:00:00";
        String signature = "неизвестная-подпись";
        String medicName = "неизвестный сотрудник";
        String validity = "";

        if (json.has("driver_fio")) {
            name = json.getString("driver_fio");
        }

        if (json.has("admitted")) {
            if (!json.get("admitted").equals("Допущен")) {
                result = "не прошел";
                admit = "не допущен";
            }
        }

        if (json.has("created_at")) {
            date = json.getString("created_at");
        }

        if (json.has("user_eds") && !json.isNull("user_eds")) {
            signature = json.getString("user_eds");
        }

        if (json.has("user_name")) {
            medicName = json.getString("user_name");
        }

        if (json.has("type_view")) {
            type = json.getString("type_view");
        }

        head = null;
        if (json.has("stamp_head")) {
            head = json.getString("stamp_head");
        }

        licence = null;
        if (json.has("stamp_licence")) {
            licence = json.getString("stamp_licence");
        }

        if (json.has("validity")) {
            validity = json.getString("validity");
        }

        if (admit.equals("допущен")) {
            this.print(name, result, type, admit, date, signature, medicName, validity);
        }
    }

    public void printTechnical(JSONObject json) throws PrintException, IOException, ru.nozdratenko.sdpo.exception.PrinterException, ParseException {
        lastTechnicalPrint = json;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse("0000-00-00 00:00:00");
        String signature = "неизвестная-подпись";
        String technicName = "неизвестный сотрудник";
        String validity = "";
        JSONObject technic = Sdpo.settings.mainConfig.getJson().getJSONObject("selected_technic");

        try {
            signature = technic.getString("eds");
            technicName = technic.getString("name");
        } catch (JSONException e) {
            SdpoLog.error("Error get medic id");
        }
        try {
            validity = "Срок действия с " + technic.get("validity_eds_start") + " по " + technic.get("validity_eds_end");
        } catch (JSONException e) {
            SdpoLog.error("Error get medic eds validity");
        }

        if (json.has("date")) {
            date = sdf.parse(json.getString("date"));
        }

        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(new PrinterResolution(72, 72, PrinterResolution.DPI));

        int count = 1;
        try {
            count = Sdpo.settings.systemConfig.getInt("print_count");
        } catch (IllegalArgumentException | IllegalStateException | JSONException e) {
            //
        }

        aset.add(new Copies(count));
        aset.add(new MediaPrintableArea(0f, 0f, 160 / 72f, 280 / 72f, MediaPrintableArea.INCH));

        this.sendPrintTask(new TechnicalPrintTask(date, signature, technicName, validity), aset);
    }

    public void print(String name, String result, String type, String admit, String date, String signature, String medicName, String validity) {
        SdpoLog.info("PrinterHelper::print " + name + " " + result + " " + type + " " + admit + " " + date + " "
            + signature + " " + medicName + " " + validity);


        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(new PrinterResolution(72, 72, PrinterResolution.DPI));

        int count = 1;
        try {
            count = Sdpo.settings.systemConfig.getInt("print_count");
        } catch (IllegalArgumentException | IllegalStateException | JSONException e) {
            //
        }

        aset.add(new Copies(count));
        aset.add(new MediaPrintableArea(0f, 0f, 160 / 72f, 280 / 72f, MediaPrintableArea.INCH));

        JSONObject stamp = Sdpo.settings.mainConfig.getJson().getJSONObject("selected_stamp");

        if (Sdpo.isConnection()) {
            JSONObject raw = Sdpo.serviceDataStorage.getFromApi();
            if (!raw.isNull("stamp_head") || !raw.isNull("stamp_licence")) {
                stamp = Sdpo.serviceDataStorage.getFromApi();
                Sdpo.serviceDataStorage.selectStamp(stamp);
            }
        }

        if (!stamp.isNull("stamp_head")) {
            head = stamp.getString("stamp_head");
        }

        if (!stamp.isNull("stamp_licence")) {
            licence = stamp.getString("stamp_licence");
        }


        this.sendPrintTask(new PrintTask(name, result, type, admit, date, signature, medicName, head, licence, validity), aset);
    }

    public void sendPrintTask(Printable task, PrintRequestAttributeSet attributes) {
        try {
            PrinterJob pj = PrinterJob.getPrinterJob();

            PrintService[] services = PrinterJob.lookupPrintServices();
            boolean hasPdfPrinter = false;

            for (PrintService service : services) {
                if (service.getName().toLowerCase().contains("pdf")) {
                    pj.setPrintService(service);
                    hasPdfPrinter = true;
                    break;
                }
            }

            if (!hasPdfPrinter) {
                SdpoLog.info("PrinterHelper::print: PDF printer not found!");
                return;
            }

            pj.setPrintable(task);

            pj.print(attributes);
        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }

    public void printFromPDF(PDDocument document) throws PrinterException, IOException {
        File file = new File("test-pdfs/printFromPDF.pdf");
        if (file.getParentFile().mkdirs()) {
            document.save(file);
        }
        SdpoLog.info("PrinterHelper::printFromPDF " + document.getDocumentId());
    }

    public void printFromPDFRotate(PDDocument document) throws PrinterException, IOException {
        File file = new File("test-pdfs/printFromPDFRotate.pdf");
        if (file.getParentFile().mkdirs()) {
            document.save(file);
        }
        SdpoLog.info("PrinterHelper::printFromPDFRotate " + document.getDocumentId());
    }
}

