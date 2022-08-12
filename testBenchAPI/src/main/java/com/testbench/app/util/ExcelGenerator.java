package com.testbench.app.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.testbench.app.model.ReportMaster;
import com.testbench.app.model.TestLogMaster;

public class ExcelGenerator {
	
	List<TestLogMaster> listOfTestLogMaster;
	ReportMaster reportMaster;
	XSSFWorkbook workbook;
    XSSFSheet sheet;
    
    public ExcelGenerator(List<TestLogMaster> listOfTestLogMaster, ReportMaster reportMaster) {
    	this.listOfTestLogMaster = listOfTestLogMaster;
    	this.reportMaster = reportMaster;
    	this.workbook = new XSSFWorkbook();
    }
    
    private void writeHeader() {
        sheet = workbook.createSheet("Report");
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(10);
        font.setFontName("Arial");
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        style.setBorderBottom(BorderStyle.DOTTED);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.DOTTED);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.DOTTED);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.DOTTED);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);
        
        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setBold(false);
        font2.setFontHeight(10);
        font2.setFontName("Arial");
        
        
        style2.setBorderBottom(BorderStyle.DOTTED);
        style2.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style2.setBorderLeft(BorderStyle.DOTTED);
        style2.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style2.setBorderTop(BorderStyle.DOTTED);
        style2.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style2.setBorderRight(BorderStyle.DOTTED);
        style2.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style2.setFont(font2);
        
        Row row = sheet.createRow(0);  
        for(int i = 0; i <= 4; i++) {
        	createCell(row, i, "", style);
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        createCell(row, 0, "Device Details", style);
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar cal = Calendar.getInstance();
	    Date date = cal.getTime();
	    String todaysdate = dateFormat.format(date);
		System.out.println("today's date = "+todaysdate);
		
        row = sheet.createRow(1);
        createCell(row, 0, "Test Date", style2);
        createCell(row, 1, todaysdate, style2);
        
        row = sheet.createRow(2);
        int size = listOfTestLogMaster.size();
        createCell(row, 0, "Start Time", style2);
        createCell(row, 1, listOfTestLogMaster.get(0).getStartTimeStamp(), style2);
        createCell(row, 3, "End Time", style2);
        createCell(row, 4, listOfTestLogMaster.get(size-1).getEndTimeStamp(), style2);
        
        row = sheet.createRow(3);
        createCell(row, 0, "Test Serial No.", style2);
        createCell(row, 1, reportMaster.getId().toString(), style2);
        
        row = sheet.createRow(4);
        createCell(row, 0, "Vendor", style2);
        createCell(row, 1, reportMaster.getVendorName(), style2);
        
        row = sheet.createRow(5);
        createCell(row, 0, "IMEI", style2);
        createCell(row, 1, reportMaster.getImei(), style2);
        
        row = sheet.createRow(6);
        createCell(row, 0, "Model", style2);
        createCell(row, 1, reportMaster.getDeviceModel(), style2);
        
        row = sheet.createRow(7);
        for(int i = 0; i <= 4; i++) {
        	createCell(row, i, "", style);
        }
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 4));
        createCell(row, 0, "Test Results", style);
        
        row = sheet.createRow(8);
        createCell(row, 0, "S.No", style);
        createCell(row, 1, "Test", style);
        createCell(row, 2, "Stage", style);
        createCell(row, 3, "Status", style);
        createCell(row, 4, "Details", style);
    }
    
    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (valueOfCell instanceof Integer) {
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        } else {
            cell.setCellValue((Boolean) valueOfCell);
        }
        cell.setCellStyle(style);
    }
    
    private void write() {
        int rowCount = 9;
        XSSFFont font = workbook.createFont();
        font.setFontHeight(10);
        font.setFontName("Arial");
        
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.DOTTED);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.DOTTED);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.DOTTED);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.DOTTED);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        
        CellStyle styleGreen = workbook.createCellStyle();
        styleGreen.setAlignment(HorizontalAlignment.CENTER);
        styleGreen.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        styleGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleGreen.setBorderBottom(BorderStyle.DOTTED);
        styleGreen.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styleGreen.setBorderLeft(BorderStyle.DOTTED);
        styleGreen.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        styleGreen.setBorderTop(BorderStyle.DOTTED);
        styleGreen.setTopBorderColor(IndexedColors.BLACK.getIndex());
        styleGreen.setBorderRight(BorderStyle.DOTTED);
        styleGreen.setRightBorderColor(IndexedColors.BLACK.getIndex());
        styleGreen.setFont(font);
        
        CellStyle styleRed = workbook.createCellStyle();
        styleRed.setAlignment(HorizontalAlignment.CENTER);
        styleRed.setFillForegroundColor(IndexedColors.RED.getIndex());
        styleRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleRed.setBorderBottom(BorderStyle.DOTTED);
        styleRed.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styleRed.setBorderLeft(BorderStyle.DOTTED);
        styleRed.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        styleRed.setBorderTop(BorderStyle.DOTTED);
        styleRed.setTopBorderColor(IndexedColors.BLACK.getIndex());
        styleRed.setBorderRight(BorderStyle.DOTTED);
        styleRed.setRightBorderColor(IndexedColors.BLACK.getIndex());
        styleRed.setFont(font);
        
        int index = 1;
        for (TestLogMaster testLog: listOfTestLogMaster) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, index++, style);
            createCell(row, columnCount++, testLog.getTestId().getName(), style);
            createCell(row, columnCount++, testLog.getTestDetails(), style);
            if(testLog.getPass() == 1) {
            	createCell(row, columnCount++, "Pass", styleGreen);
            }
            else {
            	createCell(row, columnCount++, "Fail", styleRed);
            }
            
            createCell(row, columnCount++, testLog.getTestResult(), style);
        }
    }
    
    public void generateExcelFile(HttpServletResponse response) throws IOException {
        writeHeader();
        write();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
    
}
