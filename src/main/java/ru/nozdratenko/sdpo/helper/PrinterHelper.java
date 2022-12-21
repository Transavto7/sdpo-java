package ru.nozdratenko.sdpo.helper;

import ru.nozdratenko.sdpo.exception.PrinterException;
import ru.nozdratenko.sdpo.file.FileBase;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.*;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class PrinterHelper {
    public static BufferedImage getImage(String name, String result, String type, String admit, String date, String signature) throws IOException {
        InputStream fis = PrinterHelper.class.getClassLoader().getResourceAsStream("printer-out.jpg");
        BufferedImage bufferedImage = ImageIO.read(fis);
        Font big = new Font("Arial", Font.TRUETYPE_FONT, 26);
        Font normal = new Font("Arial", Font.TRUETYPE_FONT, 18);

        Graphics g = bufferedImage.getGraphics();
        g.setFont(big);
        g.setColor(Color.BLACK);

        int center = (bufferedImage.getWidth() / 2) - (g.getFontMetrics().stringWidth(name) / 2);
        g.drawString(name, center, 35);

        g.setFont(normal);
        printString(0, g, result + " " + type, bufferedImage);
        printString(1, g, "Медецинский осмотр", bufferedImage);
        printString(2, g, "к исполнению трудовых обязаностей", bufferedImage);
        printString(3, g, admit, bufferedImage);

        g.setFont(big);
        center = (bufferedImage.getWidth() / 2) - (g.getFontMetrics().stringWidth(date) / 2);
        g.drawString(date, center, 200);

        g.setFont(normal);
        center = (bufferedImage.getWidth() / 2) - (g.getFontMetrics().stringWidth("ЭЦП " + signature) / 2);
        g.drawString("ЭЦП " + signature, center, 240);

        File outputfile = new File(FileBase.concatPath(FileBase.getMainFolderUrl(), "test.jpg"));
        ImageIO.write(bufferedImage, "jpg", outputfile);
        return bufferedImage;
    }

    private static void printString(int i, Graphics g, String text, BufferedImage image)  {
        int center = (image.getWidth() / 2) - (g.getFontMetrics().stringWidth(text) / 2);
        g.drawString(text, center, 80 + (24 * i));
    }

    public static void print(BufferedImage image) throws IOException, PrintException, PrinterException, java.awt.print.PrinterException {
        PrintService ps = PrintServiceLookup.lookupDefaultPrintService();
        DocPrintJob job = ps.createPrintJob();
        job.addPrintJobListener(new PrintJobAdapter() {
            public void printDataTransferCompleted(PrintJobEvent event){
                System.out.println("data transfer complete");
            }
            public void printJobNoMoreEvents(PrintJobEvent event){
                System.out.println("received no more events");
            }
        });

        Doc doc = new SimpleDoc(asInputStream(image), DocFlavor.INPUT_STREAM.JPEG, null);

        PrintRequestAttributeSet attrib = new HashPrintRequestAttributeSet();
        attrib.add(new Copies(1));
        attrib.add(new MediaPrintableArea(5, 5, 50, 30, MediaPrintableArea.MM));
        attrib.add(MediaSize.findMedia(2, 1, Size2DSyntax.INCH));
        job.print(doc, attrib);
    }

    public static InputStream asInputStream(BufferedImage bi) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
