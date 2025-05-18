package com.illam.chiya.utils;


import java.io.FileOutputStream;

import org.springframework.stereotype.Component;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfWriter;



@Component
public class PdfGenerator {

	public String generateTicket(String html,String filename) {
		 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			DefaultFontProvider defaultFontProvider = new DefaultFontProvider();
			PdfWriter pdfWrter =new PdfWriter(byteArrayOutputStream);
			ConverterProperties converterProperties= new ConverterProperties();
			converterProperties.setFontProvider(defaultFontProvider);
			HtmlConverter.convertToPdf(html, pdfWrter, converterProperties);
			FileOutputStream fileOut= new FileOutputStream("C:\\Users\\utsav\\OneDrive\\Documents\\workspace-spring-tool-suite-4-4.18.0.RELEASE\\Chiya-Sansar\\src\\main\\resources\\static\\bills"+filename+".pdf");
			byteArrayOutputStream.writeTo(fileOut);
			byteArrayOutputStream.close();
			byteArrayOutputStream.flush();
			fileOut.close();
			return null;
			
		} catch (Exception e) {
			
		}
		
		
		return null;
	}
	
}
