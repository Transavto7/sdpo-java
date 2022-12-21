package ru.nozdratenko.sdpo.helper;

import ru.nozdratenko.sdpo.exception.PrinterException;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.*;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PrinterHelper {
    public static void print() throws IOException, PrintException, PrinterException, java.awt.print.PrinterException {
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


        InputStream fis = PrinterHelper.class.getClassLoader().getResourceAsStream("printer-out.jpg");
        Doc doc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.JPEG, null);

        PrintRequestAttributeSet attrib = new HashPrintRequestAttributeSet();
        attrib.add(new Copies(1));
        attrib.add(new MediaPrintableArea(5, 5, 50, 30, MediaPrintableArea.MM));
        attrib.add(MediaSize.findMedia(2, 1, Size2DSyntax.INCH));
        job.print(doc, attrib);
    }
}
