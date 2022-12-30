package ru.nozdratenko.sdpo.helper;

import org.json.JSONException;
import ru.nozdratenko.sdpo.Sdpo;
import ru.nozdratenko.sdpo.file.FileBase;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.*;

public class PrinterHelper {

    public static void print(String name, String result, String type, String admit, String date, String signature) {
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

        pj.setPrintable(new PrintTask(name, result, type, admit, date, signature));

        try {
            pj.print(aset);
        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }

    public static Graphics getImage(Graphics g, int width, String name, String result, String type, String admit, String date, String signature) throws IOException {

        Font big = new Font("Arial", Font.TRUETYPE_FONT, 8);
        Font normal = new Font("Arial", Font.TRUETYPE_FONT, 5);

        g.setFont(big);
        g.setColor(Color.BLACK);

        int center = (width / 2) - (g.getFontMetrics().stringWidth(name) / 2);
        g.drawString(name, center, 10);

        g.setFont(normal);
        printString(0, g, result + " " + type, width);
        printString(1, g, "Медецинский осмотр", width);
        printString(2, g, "к исполнению трудовых обязаностей", width);
        printString(3, g, admit, width);

        g.setFont(big);
        center = (width / 2) - (g.getFontMetrics().stringWidth(date) / 2);
        g.drawString(date, center, 60);

        if (signature == null) {
            signature = "Неизвестно";
        }

        g.setFont(normal);
        center = (width / 2) - (g.getFontMetrics().stringWidth("ЭЦП " + signature) / 2);
        g.drawString("ЭЦП " + signature, center, 70);
        return g;
    }

    private static void printString(int i, Graphics g, String text, int width)  {
        int center = (width / 2) - (g.getFontMetrics().stringWidth(text) / 2);
        g.drawString(text, center, 20 + (10 * i));
    }

    public static class PrintTask implements Printable {

        private String name;
        private String result;
        private String type;
        private String admit;
        private String date;
        private String signature;

        public PrintTask(String name, String result, String type, String admit, String date, String signature) {
            this.name = name;
            this.result = result;
            this.type = type;
            this.admit = admit;
            this.date = date;
            this.signature = signature;
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
                            this.signature);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return PAGE_EXISTS;
            }
            return NO_SUCH_PAGE;
        }
    }

//    public static BufferedImage getImage(String name, String result, String type, String admit, String date, String signature) throws IOException {
//        InputStream fis = PrinterHelper.class.getClassLoader().getResourceAsStream("printer-out.jpg");
//        BufferedImage bufferedImage = ImageIO.read(fis);
//        Font big = new Font("Arial", Font.TRUETYPE_FONT, 26);
//        Font normal = new Font("Arial", Font.TRUETYPE_FONT, 20);
//
//        Graphics g = bufferedImage.getGraphics();
//        g.setFont(big);
//        g.setColor(Color.BLACK);
//
//        int center = (bufferedImage.getWidth() / 2) - (g.getFontMetrics().stringWidth(name) / 2);
//        g.drawString(name, center, 35);
//
//        g.setFont(normal);
//        printString(0, g, result + " " + type, bufferedImage.getWidth());
//        printString(1, g, "Медецинский осмотр", bufferedImage.getWidth());
//        printString(2, g, "к исполнению трудовых обязаностей", bufferedImage.getWidth());
//        printString(3, g, admit, bufferedImage.getWidth());
//
//        g.setFont(big);
//        center = (bufferedImage.getWidth() / 2) - (g.getFontMetrics().stringWidth(date) / 2);
//        g.drawString(date, center, 200);
//
//        if (signature == null) {
//            signature = "Неизвестно";
//        }
//
//        g.setFont(normal);
//        center = (bufferedImage.getWidth() / 2) - (g.getFontMetrics().stringWidth("ЭЦП " + signature) / 2);
//        g.drawString("ЭЦП " + signature, center, 240);
//
//        File outputfile = new File(FileBase.concatPath(FileBase.getMainFolderUrl(), "test.jpg"));
//        ImageIO.write(bufferedImage, "jpg", outputfile);
//        return bufferedImage;
//    }
//
//    public static Graphics getImage(Graphics g, int width, String name, String result, String type, String admit, String date, String signature) throws IOException {
//
//        Font big = new Font("Arial", Font.TRUETYPE_FONT, 26);
//        Font normal = new Font("Arial", Font.TRUETYPE_FONT, 20);
//
//        g.setFont(big);
//        g.setColor(Color.BLACK);
//
//        int center = (width / 2) - (width / 2);
//        g.drawString(name, center, 35);
//
//        g.setFont(normal);
//        printString(0, g, result + " " + type, width);
//        printString(1, g, "Медецинский осмотр", width);
//        printString(2, g, "к исполнению трудовых обязаностей", width);
//        printString(3, g, admit, width);
//
//        g.setFont(big);
//        center = (width / 2) - (g.getFontMetrics().stringWidth(date) / 2);
//        g.drawString(date, center, 200);
//
//        if (signature == null) {
//            signature = "Неизвестно";
//        }
//
//        g.setFont(normal);
//        center = (width / 2) - (g.getFontMetrics().stringWidth("ЭЦП " + signature) / 2);
//        g.drawString("ЭЦП " + signature, center, 240);
//        return g;
//    }
//
//    private static void printString(int i, Graphics g, String text, int width)  {
//        int center = (width / 2) - (g.getFontMetrics().stringWidth(text) / 2);
//        g.drawString(text, center, 80 + (26 * i));
//    }
//
//    public static void print(BufferedImage image) throws IOException, PrintException, PrinterException, java.awt.print.PrinterException {
//        PrintService ps = PrintServiceLookup.lookupDefaultPrintService();
//        DocPrintJob job = ps.createPrintJob();
//        job.addPrintJobListener(new PrintJobAdapter() {
//            public void printDataTransferCompleted(PrintJobEvent event){
//                System.out.println("data transfer complete");
//            }
//            public void printJobNoMoreEvents(PrintJobEvent event){
//                System.out.println("received no more events");
//            }
//        });
//
//        Doc doc = new SimpleDoc(asInputStream(image), DocFlavor.INPUT_STREAM.JPEG, null);
//
//        PrintRequestAttributeSet attrib = new HashPrintRequestAttributeSet();
//
//        int count = 1;
//        try {
//            count = Sdpo.systemConfig.getInt("print_count");
//        } catch (IllegalArgumentException e) {
//            //
//        } catch (IllegalStateException e) {
//            //
//        } catch (JSONException e) {
//            //
//        }
//
//        attrib.add(new Copies(count));
//        attrib.add(new MediaPrintableArea(0, 0, 50, 30, MediaPrintableArea.MM));
//        attrib.add(MediaSize.findMedia(50, 30, MediaPrintableArea.MM));
//        job.print(doc, attrib);
//    }
//
//    public static InputStream asInputStream(BufferedImage bi) throws IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(bi, "png", baos);
//        return new ByteArrayInputStream(baos.toByteArray());
//    }
}
