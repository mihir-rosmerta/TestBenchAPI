package com.testbench.app.repository;

import java.util.List;

public interface RawDataRepo {

	public List<Integer> getIgnitionResults(String imei, String packetType, String startTimestamp, String endTimestamp);
	
	public List<Integer> getDigitalInputResults(String imei, String startTimestamp, String endTimestamp);
	
	public List<String> getProtocolResults(String imei, String startTimestamp, String endTimestamp);
	
}
