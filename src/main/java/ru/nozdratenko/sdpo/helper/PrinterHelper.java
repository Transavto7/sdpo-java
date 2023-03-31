package ru.nozdratenko.sdpo.helper;

import org.json.JSONException;
import org.json.JSONObject;
import ru.nozdratenko.sdpo.Sdpo;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import java.awt.*;
import java.awt.print.*;
import java.io.*;

public class PrinterHelper {
    public static JSONObject lastPrint = null;

    public static void print(JSONObject json) throws PrintException, IOException, ru.nozdratenko.sdpo.exception.PrinterException {
        lastPrint = json;

        String name = "Неизвестный водитель";
        String result = "прошел";
        String type = "Предрейсовый/Предсменный";
        String admit = "допущен";
        String date = "0000-00-00 00:00:00";
        String signature = "неизвестная-подпись";
        String medicName = "неизвестный сотрудник";

        if (json.has("driver_fio")) {
            name = "" + json.get("driver_fio");
        }

        if (json.has("admitted")) {
            if (!json.get("admitted").equals("Допущен")) {
                result = "не прошел";
                admit = "не допущен";
            }
        }

        if (json.has("created_at")) {
            date = "" + json.get("created_at");
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

        if (admit.equals("допущен")) {
            PrinterHelper.print(name, result, type, admit, date, signature, medicName);
        }
    }

    public static void print(String name, String result, String type, String admit, String date, String signature, String medicName) {
        PrinterJob pj = PrinterJob.getPrinterJob();

        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(new PrinterResolution(72, 72, PrinterResolution.DPI));

        int count = 1;
        try {
            count = Sdpo.systemConfig.getInt("print_count");
        } catch (IllegalArgumentException e) {
            //
        } catch (IllegalStateException e) {
            //
        } catch (JSONException e) {
            //
        }

        aset.add(new Copies(count));
        aset.add(new MediaPrintableArea(0f, 0f, 160/72f, 280/72f, MediaPrintableArea.INCH));

        pj.setPrintable(new PrintTask(name, result, type, admit, date, signature, medicName));

        try {
            pj.print(aset);
        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }

    public static Graphics getImage(Graphics g, int width, String name, String result, String type,
                                    String admit, String date, String signature, String medicName) throws IOException {

        Font big = new Font("Arial", Font.TRUETYPE_FONT, 8);
        Font normal = new Font("Arial", Font.TRUETYPE_FONT, 6);
        Font small = new Font("Arial", Font.TRUETYPE_FONT, 4);

        g.setFont(big);
        g.setColor(Color.BLACK);
        int center = (width / 2) - (g.getFontMetrics().stringWidth("ООО \"Трансавто-7\"") / 2);
        g.drawString("ООО \"Трансавто-7\"", center, 10);

        g.setFont(small);
        center = (width / 2) - (g.getFontMetrics().stringWidth("Бессрочная лицензия от 09.12.2020 № Л041-1177-91/00366739") / 2);
        g.drawString("Бессрочная лицензия от 09.12.2020 № Л041-1177-91/00366739", center, 17);

        g.setFont(normal);
        g.setColor(Color.BLACK);
        center = (width / 2) - (g.getFontMetrics().stringWidth(name) / 2);
        g.drawString(name, center, 26);

        g.setFont(normal);
        printString(0, g, result + " " + type, width);
        printString(1, g, "Медицинский осмотр", width);
        printString(2, g, "к исполнению трудовых обязаностей", width);
        printString(3, g, admit, width);

        center = (width / 2) - (g.getFontMetrics().stringWidth(date) / 2);
        g.drawString(date, center, 66);

        if (signature == null) {
            signature = "Неизвестно";
        }

        g.setFont(normal);
        center = (width / 2) - (g.getFontMetrics().stringWidth(medicName) / 2);
        g.drawString(medicName, center, 73);

        center = (width / 2) - (g.getFontMetrics().stringWidth("ЭЦП " + signature) / 2);
        g.drawString("ЭЦП " + signature, center, 80);
        return g;
    }

    private static void printString(int i, Graphics g, String text, int width)  {
        int center = (width / 2) - (g.getFontMetrics().stringWidth(text) / 2);
        g.drawString(text, center, 35 + (7 * i));
    }

    public static class PrintTask implements Printable {

        private String name;
        private String result;
        private String type;
        private String admit;
        private String date;
        private String signature;
        private String medicName;

        public PrintTask(String name, String result, String type, String admit, String date, String signature, String medicName) {
            this.name = name;
            this.result = result;
            this.type = type;
            this.admit = admit;
            this.date = date;
            this.signature = signature;
            this.medicName = medicName;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            Paper paper = pageFormat.getPaper();
            paper.setSize(160, 130);
            pageFormat.setPaper(paper);

            if (signature == null) {
                signature = "Неизвестно";
            }

            if (pageIndex < 1) {
                try {
                    getImage(graphics, (int) pageFormat.getWidth(), this.name,
                            this.result,
                            this.type,
                            this.admit,
                            this.date,
                            this.signature,
                            this.medicName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return PAGE_EXISTS;
            }
            return NO_SUCH_PAGE;
        }
    }
}
