package ru.nozdratenko.sdpo.task.print;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.io.IOException;
import java.util.Objects;

public class PrintTask  implements Printable {

    private final String name;
    private final String result;
    private final String type;
    private final String admit;
    private final String date;
    private String signature;
    private final String medicName;
    private final String head;
    private final String licence;

    private final String validity;

    public PrintTask(String name, String result, String type, String admit, String date, String signature,
                     String medicName, String head, String licence, String validity) {
        this.name = name;
        this.result = result;
        this.type = type;
        this.admit = admit;
        this.date = date;
        this.signature = signature;
        this.medicName = medicName;

        this.head = Objects.requireNonNullElse(head, "ООО \"Трансавто-7\"");
        this.licence = Objects.requireNonNullElse(licence, "Бессрочная лицензия от 09.12.2020 № Л041-1177-91/00366739");
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
                this.getImage(graphics, (int) pageFormat.getWidth(), this.name,
                        this.result,
                        this.type,
                        this.admit,
                        this.date,
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


    public void getImage(Graphics g, int width, String name, String result, String type,
                         String admit, String date, String signature, String medicName, String validity) throws IOException {

        Font big = new Font("Arial", Font.PLAIN, 8);
        Font normal = new Font("Arial", Font.PLAIN, 5);
        Font small = new Font("Arial", Font.PLAIN, 4);

        g.setFont(big);
        g.setColor(Color.BLACK);
        int center = (width / 2) - (g.getFontMetrics().stringWidth(head) / 2);
        g.drawString(head, center, 10);

        g.setFont(small);
        center = (width / 2) - (g.getFontMetrics().stringWidth(licence) / 2);
        g.drawString(licence, center, 17);

        g.setFont(normal);
        g.setColor(Color.BLACK);
        center = (width / 2) - (g.getFontMetrics().stringWidth(name) / 2);
        g.drawString(name, center, 24);

        g.setFont(normal);
        printString(0, g, result + " " + type, width);
        printString(1, g, "Медицинский осмотр", width);

        if (!type.toLowerCase().contains("после")) {
            printString(2, g, "к исполнению трудовых обязаностей", width);
            printString(3, g, admit, width);
        }

        center = (width / 2) - (g.getFontMetrics().stringWidth(date) / 2);
        g.drawString(date, center, 60);

        if (signature == null) {
            signature = "Неизвестно";
        }

        g.setFont(normal);
        center = (width / 2) - (g.getFontMetrics().stringWidth(medicName) / 2);
        g.drawString(medicName, center, 65);

        center = (width / 2) - (g.getFontMetrics().stringWidth("ЭЦП " + signature) / 2);
        g.drawString("ЭЦП " + signature, center, 70);

        center = (width / 2) - (g.getFontMetrics().stringWidth(validity) / 2);
        g.drawString(validity, center, 75);
    }

    private static void printString(int i, Graphics g, String text, int width)  {
        int center = (width / 2) - (g.getFontMetrics().stringWidth(text) / 2);
        g.drawString(text, center, 30 + (6 * i));
    }
}
