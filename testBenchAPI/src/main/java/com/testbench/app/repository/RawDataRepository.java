package com.testbench.app.repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RawDataRepository implements RawDataRepo{

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public List<Integer> getIgnitionResults(String imei, String packetType, String startTimestamp, String endTimestamp) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar cal = Calendar.getInstance();
	    Date date = cal.getTime();
	    String todaysdate = dateFormat.format(date);
		System.out.println("today's date = "+todaysdate);
		
		
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("imei", imei);
        mapSqlParameterSource.addValue("packetType", packetType);
        mapSqlParameterSource.addValue("startTimestamp", startTimestamp);
        mapSqlParameterSource.addValue("endTimestamp", endTimestamp);
        
        return namedParameterJdbcTemplate.query("select ignumber from public.\"parsed_loc_device_record_"+todaysdate+"\" "
        		+ "where imeino = :imei and packettype = :packetType and datatimestamp >= CAST(:startTimestamp as timestamp without time zone) "
        		+ "and datatimestamp <= CAST(:endTimestamp as timestamp without time zone)"
        		, mapSqlParameterSource,(rs,rowMapper)->rs.getInt(1));
	}

	@Override
	public List<Integer> getDigitalInputResults(String imei, String startTimestamp, String endTimestamp) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar cal = Calendar.getInstance();
	    Date date = cal.getTime();
	    String todaysdate = dateFormat.format(date);
		System.out.println("today's date = "+todaysdate);
		
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("imei", imei);
        mapSqlParameterSource.addValue("startTimestamp", startTimestamp);
        mapSqlParameterSource.addValue("endTimestamp", endTimestamp);
        
        return namedParameterJdbcTemplate.query("select digitalipstatus from public.\"parsed_loc_device_record_"+todaysdate+"\" "
        		+ "where imeino = :imei and datatimestamp >= CAST(:startTimestamp as timestamp without time zone) "
        		+ "and datatimestamp <= CAST(:endTimestamp as timestamp without time zone)"
        		+ "", mapSqlParameterSource,(rs,rowMapper)->rs.getInt(1));
	}

	@Override
	public List<String> getProtocolResults(String imei, String startTimestamp, String endTimestamp) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar cal = Calendar.getInstance();
	    Date date = cal.getTime();
	    String todaysdate = dateFormat.format(date);
		System.out.println("today's date = "+todaysdate);
		
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("imei", imei);
        mapSqlParameterSource.addValue("startTimestamp", startTimestamp);
        mapSqlParameterSource.addValue("endTimestamp", endTimestamp);
        
        return namedParameterJdbcTemplate.query("select packettype from public.\"parsed_loc_device_record_"+todaysdate+"\" "
        		+ "where imeino = :imei and datatimestamp >= CAST(:startTimestamp as timestamp without time zone) "
        		+ "and datatimestamp <= CAST(:endTimestamp as timestamp without time zone)"
        		+ "", mapSqlParameterSource,(rs,rowMapper)->rs.getString(1));
	}
}
