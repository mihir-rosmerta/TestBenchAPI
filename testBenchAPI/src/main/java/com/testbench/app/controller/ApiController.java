package com.testbench.app.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.testbench.app.model.ReportMaster;
import com.testbench.app.model.TestLogMaster;
import com.testbench.app.model.TestMaster;
import com.testbench.app.repository.RawDataRepo;
import com.testbench.app.repository.ReportMasterRepo;
import com.testbench.app.repository.TestLogMasterRepo;
import com.testbench.app.repository.TestMasterRepo;
import com.testbench.app.util.ExcelGenerator;

@RestController
@RequestMapping("/api/v1/controller")
public class ApiController {

	@Autowired
	private TestMasterRepo testMasterRepo;
	
	@Autowired
	private TestLogMasterRepo testLogMasterRepo;
	
	@Autowired
	private RawDataRepo rawDataRepo;
	
	@Autowired
	private ReportMasterRepo reportMasterRepo; 
	
	@PostMapping("/formEntry")
	public ReportMaster formEntry(@RequestParam("vendor") String vendorName, @RequestParam("imei") String imei, @RequestParam("deviceModel") String deviceModel, 
			@RequestParam("address") String address, @RequestParam("contactPerson") String contactPerson) {
		ReportMaster reportMaster = new ReportMaster();
		reportMaster.setVendorName(vendorName);
		reportMaster.setImei(imei);
		reportMaster.setDeviceModel(deviceModel);
		reportMaster.setAddress(address);
		reportMaster.setContactPerson(contactPerson);
		reportMaster = reportMasterRepo.save(reportMaster);
		return reportMaster;
	}
	
	@PostMapping("/createVendor")
	public void createVendor() {
		
	}
	
	@GetMapping("/generateReport")
	public void generateReport(HttpServletResponse response, @RequestParam("testSerial") String testSerial) {
		List<TestLogMaster> allTests = testLogMasterRepo.getAllTests(testSerial);
		ReportMaster reportMaster = reportMasterRepo.getReportBySerialNo(testSerial);
		

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	    Calendar cal = Calendar.getInstance();
	    Date date = cal.getTime();
	    String todaysdate = dateFormat.format(date);
		System.out.println("today's date = "+todaysdate);
		
		response.setContentType("application/force-download");
        
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=report"+testSerial+"_"+todaysdate+".xlsx";
        response.setHeader(headerKey, headerValue);

        try {
        	ExcelGenerator generator = new ExcelGenerator(allTests,reportMaster);
			generator.generateExcelFile(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@PostMapping("/startPowerTest")
	public int startPowerTest(@RequestParam("testSerial") String testSerial, @RequestParam("testId") int testId, @RequestParam("testDetails") String testDetails
			, @RequestParam("testVoltage") int testVoltage, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
		ReportMaster reportMaster = reportMasterRepo.getReportBySerialNo(testSerial);
		
		double lowerBoundVoltage = testVoltage - (0.1)*testVoltage;
		double upperBoundVoltage = testVoltage + (0.1)*testVoltage;
		
		
		String[] response = new String[8];
		
		TestMaster testMaster = testMasterRepo.findById(testId).get();
		TestLogMaster testLogMaster = new TestLogMaster();
		testLogMaster.setTestId(testMaster);
		testLogMaster.setReportId(reportMaster);
		testLogMaster.setTestDetails(testDetails);
		testLogMaster.setStartTimeStamp(startTime);
		testLogMaster.setEndTimeStamp(endTime);
		int noOfStrings = response.length;
		
		testLogMaster.setTestResult(String.valueOf(noOfStrings));
		
		boolean passFlag = true;
		for(String voltage: response) {
			Double receivedVoltage = Double.parseDouble(voltage);
			if(receivedVoltage < lowerBoundVoltage || receivedVoltage > upperBoundVoltage) {
				passFlag = false;
				break;
			}
		}
		if(passFlag == false) {
			testLogMaster.setPass(0);
		}
		else if(noOfStrings >= 85 && noOfStrings <= 95) {
			testLogMaster.setPass(1);
		}
		else {
			testLogMaster.setPass(0);
		}
		testLogMaster = testLogMasterRepo.save(testLogMaster);
		return noOfStrings;
	}
	
	
	@PostMapping("/startIgnitionTestAndGetCollectedStrings")
	public List<Integer> startIgnitionTest(@RequestParam("testSerial") String testSerial, @RequestParam("testId") int testId
			, @RequestParam("testOnOrOff") int testType, @RequestParam("testDetails") String testDetails, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
		
		ReportMaster reportMaster = reportMasterRepo.getReportBySerialNo(testSerial);
		List<Integer> responses = rawDataRepo.getIgnitionResults(reportMaster.getImei(), "NR", startTime, endTime);
		
		TestMaster testMaster = testMasterRepo.findById(testId).get();
		TestLogMaster testLogMaster = new TestLogMaster();
		testLogMaster.setTestId(testMaster);
		testLogMaster.setReportId(reportMaster);
		testLogMaster.setTestDetails(testDetails);
		testLogMaster.setStartTimeStamp(startTime);
		testLogMaster.setEndTimeStamp(endTime);
		
		int noOfStrings = responses.size();
		
		testLogMaster.setTestResult(String.valueOf(noOfStrings));
		
		int countOfOffStrings = 0;
		boolean passFlag = false;
		for(int value: responses) {
			if(value == 0) {
				countOfOffStrings++;
			}
		}
		
		
		int halfTheNoOfStrings = noOfStrings/2;
		double noOfOffStringsLowerBound = halfTheNoOfStrings - (0.1)*halfTheNoOfStrings;
		double noOfOffStringsUpperBound = halfTheNoOfStrings + (0.1)*halfTheNoOfStrings;

		if(testType == 1) {
			if(countOfOffStrings <= halfTheNoOfStrings && countOfOffStrings >= noOfOffStringsLowerBound) {
				passFlag = true;
			}
		}
		else {
			if(countOfOffStrings >= halfTheNoOfStrings && countOfOffStrings <= noOfOffStringsUpperBound) {
				passFlag = true;
			}
		}
		
		if(passFlag == true && noOfStrings >= 170 && noOfStrings <= 190) {
			testLogMaster.setPass(1);
		}
		else {
			testLogMaster.setPass(0);
		}
		testLogMaster = testLogMasterRepo.save(testLogMaster);
		return responses;
	}
	
	@PostMapping("/startProtocolTestAndGetCollectedStrings")
	public List<String> startProtocolTest(@RequestParam("testSerial") String testSerial, @RequestParam("testId") int testId
			, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
		ReportMaster reportMaster = reportMasterRepo.getReportBySerialNo(testSerial);
		List<String> responses = rawDataRepo.getProtocolResults(reportMaster.getImei(), startTime, endTime);
		List<String> allowedProtocolsList = Arrays.asList("NR","HP","IN","IF","BD","BR");
		Set<String> allowedProtocols = new HashSet<>(allowedProtocolsList);
		Map<String,Integer> packetCount = new HashMap<String,Integer>();
		TestMaster testMaster = testMasterRepo.findById(testId).get();
				
		for(String protocol: allowedProtocolsList) {
			packetCount.put(protocol, 0);
		}
		
		
		for(String response: responses) {
			response = response.trim();
			if(allowedProtocols.contains(response)) {
				packetCount.put(response, packetCount.get(response)+1);
			}
		}
		
		for(String protocol: allowedProtocolsList) {
			
			int countOfStrings = packetCount.get(protocol);
			TestLogMaster testLogMaster = new TestLogMaster();
			testLogMaster.setTestId(testMaster);
			testLogMaster.setReportId(reportMaster);
			testLogMaster.setTestDetails(protocol);
			testLogMaster.setTestResult(String.valueOf(countOfStrings));
			testLogMaster.setStartTimeStamp(startTime);
			testLogMaster.setEndTimeStamp(endTime);
			
			if(countOfStrings == 0) {
				testLogMaster.setPass(0);
			}
			else {
				testLogMaster.setPass(1);
			}
			
			testLogMaster = testLogMasterRepo.save(testLogMaster);
		}
		
		return responses;
	}
	
	@PostMapping("/startDigitalOutput")
	public void startDigitalOutput(@RequestParam("testSerial") String testSerial, @RequestParam("testId") int testId
			, @RequestParam("testPass") int pass, @RequestParam("testDetails") String testDetails, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
		ReportMaster reportMaster = reportMasterRepo.getReportBySerialNo(testSerial);
		TestMaster testMaster = testMasterRepo.findById(testId).get();
		TestLogMaster testLogMaster = new TestLogMaster();
		testLogMaster.setTestId(testMaster);
		testLogMaster.setReportId(reportMaster);
		testLogMaster.setTestDetails(testDetails);
		testLogMaster.setStartTimeStamp(startTime);
		testLogMaster.setEndTimeStamp(endTime);
		
		testLogMaster.setTestResult("");
		if(pass == 1) {
			testLogMaster.setPass(1);
		}
		else {
			testLogMaster.setPass(0);
		}
		testLogMaster = testLogMasterRepo.save(testLogMaster);
	}
	
	@PostMapping("/startDigitalInputAndGetCollectedStrings")
	public List<Integer> startDigitalInput(@RequestParam("testSerial") String testSerial, @RequestParam("testId") int testId
			, @RequestParam("testOnOrOff") int testType, @RequestParam("testDetails") String testDetails, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
		ReportMaster reportMaster = reportMasterRepo.getReportBySerialNo(testSerial);
		TestMaster testMaster = testMasterRepo.findById(testId).get();
		TestLogMaster testLogMaster = new TestLogMaster();
		testLogMaster.setTestId(testMaster);
		testLogMaster.setReportId(reportMaster);
		testLogMaster.setTestDetails(testDetails);
		testLogMaster.setStartTimeStamp(startTime);
		testLogMaster.setEndTimeStamp(endTime);
		
		List<Integer> responses = rawDataRepo.getDigitalInputResults(reportMaster.getImei(), startTime, endTime);
		int noOfStrings = responses.size();
		
		testLogMaster.setTestResult(String.valueOf(noOfStrings));
		
		int countOfOffStrings = 0;
		int countOfOnStrings = 0;
		boolean passFlag = false;
		for(int i = 0; i < noOfStrings; i++) {
			int di = responses.get(i);
			if(di == 0) {
				countOfOffStrings++;
			}
			else {
				countOfOnStrings++;
			}
		}
		
		
		int halfTheNoOfStrings = noOfStrings/2;
		double noOfStringsUpperBound = halfTheNoOfStrings + (0.1)*halfTheNoOfStrings;
		
		if(testType == 1) {
			if(countOfOnStrings >= halfTheNoOfStrings && countOfOnStrings <= noOfStringsUpperBound) {
				passFlag = true;
			}
		}
		else {
			if(countOfOffStrings >= halfTheNoOfStrings && countOfOffStrings <= noOfStringsUpperBound) {
				passFlag = true;
			}
		}
		if(countOfOffStrings >= 12 && countOfOffStrings <= 18 && countOfOnStrings >= 12 && countOfOnStrings <= 17 && passFlag == true) {
			testLogMaster.setPass(1);
		}
		else {
			testLogMaster.setPass(0);
		}
		testLogMaster = testLogMasterRepo.save(testLogMaster);
		return responses;
	}
}
