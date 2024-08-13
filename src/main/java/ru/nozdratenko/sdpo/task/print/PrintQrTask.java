package ru.nozdratenko.sdpo.task.print;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

public class PrintQrTask extends Book {

    protected PDDocument document;

    public PrintQrTask(PDDocument document) {
        this.document = document;
        this.getPageFormat();
        this.append(new PDFPrintable(document, Scaling.SCALE_TO_FIT), this.getPageFormat(), document.getNumberOfPages());
    }

    private PageFormat getPageFormat() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPaper(this.getPaper());
        pageFormat.setOrientation(PageFormat.LANDSCAPE);
        return pageFormat;
    }

    private Paper getPaper() {
        Paper paper = new Paper();
        paper.setSize(160, 84);
        paper.setImageableArea(10, 0, 155, 80);
        return paper;
    }


}
