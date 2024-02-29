package ru.nozdratenko.sdpo.helper;

import org.json.JSONException;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.task.PrintTask;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import java.awt.print.*;
import java.io.*;

public class PrinterHelper {
    public static JSONObject lastPrint = null;
    public static String head = null;
    public static String licence = null;

    public static void print(JSONObject json) throws PrintException, IOException, ru.nozdratenko.sdpo.exception.PrinterException {
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

        if (json.has("user_eds")) {
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
            PrinterHelper.print(name, result, type, admit, date, signature, medicName, validity);
        }
    }

    public static void print(String name, String result, String type, String admit, String date, String signature, String medicName, String validity) {
        PrinterJob pj = PrinterJob.getPrinterJob();

        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(new PrinterResolution(72, 72, PrinterResolution.DPI));

        int count = 1;
        try {
            count = Sdpo.systemConfig.getInt("print_count");
        } catch (IllegalArgumentException | IllegalStateException | JSONException e) {
            //
        }

        aset.add(new Copies(count));
        aset.add(new MediaPrintableArea(0f, 0f, 160 / 72f, 280 / 72f, MediaPrintableArea.INCH));

        pj.setPrintable(new PrintTask(name, result, type, admit, date, signature, medicName, head, licence, validity));

        try {
            pj.print(aset);
        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }
}

