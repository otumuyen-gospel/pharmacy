package juma.api;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user1
 */
public class Generator {
     private final File file;
     Cart cart;
     String name,currency;
     double price,amount;
     Document document = new Document();
     Font medium = FontFactory.getFont(FontFactory.COURIER, 15, Font.NORMAL, BaseColor.BLACK);
     Font big = FontFactory.getFont(FontFactory.COURIER, 20, Font.NORMAL, BaseColor.RED);
     Font black = FontFactory.getFont(FontFactory.COURIER, 10, Font.NORMAL, BaseColor.BLACK );
     PdfWriter writer = null;
     public Generator(Cart cart ,File dir) throws Exception{
         this.cart = cart;
         file = new File(dir.getAbsolutePath()+"/invoice.pdf");
         file.createNewFile();
         writer = PdfWriter.getInstance(document, new FileOutputStream(file.getAbsolutePath()));
         document.open();
         
     }
     public File getFile(){
         return file;
     }
     public void closeFile(){
        document.close();
        writer.close();
    }
    public void header(String name, String currency,String email, String telephone, byte[]b) throws Exception{
        this.name = name;
        this.currency = currency;
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setSpacingAfter(10f);
        titleTable.setWidthPercentage(100);
        Paragraph title = new Paragraph("Invoice Slip",big);
        title.setAlignment(1);
        PdfPCell titleCell = new PdfPCell(title);
        titleCell.setBorder(0);
        titleCell.setUseBorderPadding(true);
        titleTable.addCell(titleCell);
        document.add(titleTable);

        //create header
        Image image = Image.getInstance(b);
        image.setAlignment(1);
        image.scaleAbsolute(24, 24);
        document.add(image);
        Paragraph addr = new Paragraph(name,black);
        addr.setAlignment(1);
        Paragraph tel = new Paragraph(email,black);
        tel.setAlignment(1);
        Paragraph emai = new Paragraph(telephone,black);
        emai.setAlignment(1);
        document.add(addr);
        document.add(tel);
        document.add(emai);
    
        Paragraph subtitle = new Paragraph("Invoice",medium);
        subtitle.setAlignment(1);
        subtitle.setSpacingBefore(5f);
        subtitle.setSpacingAfter(5f);
        document.add(subtitle);
    }
    public void body() throws Exception{
        PdfPTable table = new PdfPTable(1);
        table.getDefaultCell().setBorder(0);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10f);
        table.setSpacingBefore(10f);
        //set columns width
        float[]columnWidths = {1f};
        table.setWidths(columnWidths);
        PdfPCell list24 = new PdfPCell(new Paragraph("Invoice No: "+cart.get(0).getInvoice() ,black));
        list24.setBorder(0);
        list24.setHorizontalAlignment(2);
        table.addCell(list24);
        PdfPCell list21 = new PdfPCell(new Paragraph("Date: "+new java.util.Date() ,black));
        list21.setBorder(0);
        list21.setHorizontalAlignment(2);
        table.addCell(list21);
        PdfPCell list23 = new PdfPCell(new Paragraph("Payment Status: Paid",black));
        list23.setBorder(0);
        list23.setHorizontalAlignment(2);
        table.addCell(list23);

        document.add(table);
    }
    public void footer(){
        try{
            
            PdfPTable table = new PdfPTable(1);
            table.getDefaultCell().setBorder(1);
            table.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
            table.setWidthPercentage(100);
            table.setSpacingAfter(5f);
            table.setSpacingBefore(5f);
            
            PdfPTable header = new PdfPTable(4);
            header.getDefaultCell().setBorder(0);
            PdfPCell header1 = new PdfPCell(new Paragraph("Product Name" ,black));
            header1.setHorizontalAlignment(2);
            header1.setBorder(0);
            header1.setPadding(5);
            header.addCell(header1);
            PdfPCell header2 = new PdfPCell(new Paragraph("Quantity" ,black));
            header2.setHorizontalAlignment(2);
            header2.setBorder(0);
            header2.setPadding(5);
            header.addCell(header2);
            PdfPCell header3 = new PdfPCell(new Paragraph("price("+currency+")" ,black));
            header3.setHorizontalAlignment(2);
            header3.setBorder(0);
            header3.setPadding(5);
            header.addCell(header3);
            PdfPCell header4 = new PdfPCell(new Paragraph("Amount("+currency+")" ,black));
            header4.setHorizontalAlignment(2);
            header4.setBorder(0);
            header4.setPadding(5);
            header.addCell(header4);
            table.addCell(header);
            PdfPTable middle = new PdfPTable(4);
            middle.getDefaultCell().setBorder(0);
            for(Query query : cart){
                PdfPCell middle1 = new PdfPCell(new Paragraph(query.getProduct() ,black));
                middle1.setHorizontalAlignment(2);
                middle1.setBorder(0);
                middle1.setPadding(5);
                middle.addCell(middle1);
                PdfPCell middle2 = new PdfPCell(new Paragraph(String.valueOf(query.getQuantity()) ,black));
                middle2.setHorizontalAlignment(2);
                middle2.setBorder(0);
                middle2.setPadding(5);
                middle.addCell(middle2);
                PdfPCell middle3 = new PdfPCell(new Paragraph(String.valueOf(query.getPrice()) ,black));
                middle3.setHorizontalAlignment(2);
                middle3.setBorder(0);
                middle3.setPadding(5);
                middle.addCell(middle3);
                PdfPCell middle4 = new PdfPCell(new Paragraph(String.valueOf(query.getAmount()) ,black));
                middle4.setHorizontalAlignment(2);
                middle4.setBorder(0);
                middle4.setPadding(5);
                middle.addCell(middle4);
                price += query.getPrice();amount +=query.getAmount();
            }
            table.addCell(middle);
            
            PdfPTable amt = new PdfPTable(4);
            PdfPCell amt1 = new PdfPCell(new Paragraph("Total Price" ,black));
            amt1.setHorizontalAlignment(2);
            amt1.setBorder(0);
            amt1.setPadding(5);
            amt1.setColspan(3);
            amt.addCell(amt1);
            PdfPCell amt2 = new PdfPCell(new Paragraph(String.valueOf(price) ,black));
            amt2.setHorizontalAlignment(2);
            amt2.setBorder(0);
            amt2.setPadding(5);
            amt2.setColspan(1);
            amt.addCell(amt2);
            table.addCell(amt);
            
            PdfPTable bottom = new PdfPTable(4);
            PdfPCell bottom1 = new PdfPCell(new Paragraph("Total Amount" ,black));
            bottom1.setHorizontalAlignment(2);
            bottom1.setBorder(0);
            bottom1.setPadding(5);
            bottom1.setColspan(3);
            bottom.addCell(bottom1);
            PdfPCell bottom2 = new PdfPCell(new Paragraph(String.valueOf(amount) ,black));
            bottom2.setHorizontalAlignment(2);
            bottom2.setBorder(0);
            bottom2.setPadding(5);
            bottom2.setColspan(1);
            bottom.addCell(bottom2);
            table.addCell(bottom);
            
            document.add(table);
            Paragraph last = new Paragraph(name,black);
            last.setAlignment(1);
            document.add(last);
            
            
            PdfPTable sign = new PdfPTable(2);
            sign.setWidthPercentage(40);
            sign.getDefaultCell().setBorder(0);
            sign.setSpacingBefore(80f);
            PdfPCell sign1 = new PdfPCell(new Paragraph("Signature" ,black));
            sign1.setHorizontalAlignment(2);
            sign1.setBorder(0);
            sign1.setBorderWidthTop(1);
            sign1.setPadding(5);
            sign1.setColspan(2);
            sign.addCell(sign1);
            document.add(sign);
            
            
            PdfPTable end = new PdfPTable(2);
            end.setWidthPercentage(100);
            end.getDefaultCell().setBorder(0);
            end.setSpacingBefore(80f);
            PdfPCell end1 = new PdfPCell(new Paragraph("this is an automated invoice if you find any error"
                    + " please let us know immediately. thanks for your patronage" ,black));
            end1.setHorizontalAlignment(1);
            end1.setBorder(0);
            end1.setBorderWidthTop(1);
            end1.setPadding(5);
            end1.setColspan(2);
            end.addCell(end1);
            document.add(end);
            
            //start new page
            document.newPage();
        }catch(Exception e){
            
        }
    }
    
    
}
