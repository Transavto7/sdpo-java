package ru.nozdratenko.sdpo.task.print;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class TechnicalPrintTask implements Printable {
    private final Date date;
    private String signature;
    private final String medicName;
    private final String validity;

    public TechnicalPrintTask(Date date, String signature, String medicName, String validity) {
        this.date = date;
        this.signature = signature;
        this.medicName = medicName;
        this.validity = Objects.requireNonNullElse(validity, "");
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
                this.getImage(graphics, (int) pageFormat.getWidth(),
                        this.formatDate(this.date),
                        this.signature,
                        this.medicName,
                        this.validity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return PAGE_EXISTS;
        }
        return NO_SUCH_PAGE;
    }


    public void getImage(Graphics g, int width, String date, String signature, String technicName, String validity) throws IOException {

        Font big = new Font("Arial", Font.PLAIN, 8);
        Font normal = new Font("Arial", Font.PLAIN, 7);
        Font small = new Font("Arial", Font.PLAIN, 5);
        String head = "ВЫПУСК НА ЛИНИЮ РАЗРЕШЕН";

        g.setFont(big);
        g.setColor(Color.BLACK);
        int center = (width / 2) - (g.getFontMetrics().stringWidth(head) / 2);
        g.drawString(head, center, 20);

        g.setFont(normal);
        center = (width / 2) - (g.getFontMetrics().stringWidth(date) / 2);
        g.drawString(date, center, 40);

        if (signature == null) {
            signature = "Неизвестно";
        }

        g.setFont(small);
        center = (width / 2) - (g.getFontMetrics().stringWidth("Контролер ТС:" + technicName) / 2);
        g.drawString("Контролер ТС: " + technicName, center, 60);

        center = (width / 2) - (g.getFontMetrics().stringWidth("ЭЦП " + signature) / 2);
        g.drawString("ЭЦП: " + signature, center, 65);

        center = (width / 2) - (g.getFontMetrics().stringWidth(validity) / 2);
        g.drawString(validity, center, 70);
    }

    public String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String formattedDate = dateFormat.format(date);
        String formattedTime = timeFormat.format(date);

        return "Дата: " + formattedDate + "  Время: " + formattedTime;
    }
}
