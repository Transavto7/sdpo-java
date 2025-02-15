package ru.nozdratenko.sdpo.helper.PrinterHelpers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONException;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.task.print.PrintQrRotateTask;
import ru.nozdratenko.sdpo.task.print.PrintQrTask;
import ru.nozdratenko.sdpo.task.print.PrintTask;

import javax.print.PrintException;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.PrinterResolution;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;

public interface PrinterHelper {
    void print(JSONObject json) throws PrintException, IOException, ru.nozdratenko.sdpo.exception.PrinterException;
    void print(String name, String result, String type, String admit, String date, String signature, String medicName, String validity);
    void printFromPDF (PDDocument document) throws PrinterException, IOException;
    void printFromPDFRotate (PDDocument document) throws PrinterException, IOException;
    JSONObject getLastPrint();
    String getLastQRPath();
    void setLastQRPath(String path);
}

