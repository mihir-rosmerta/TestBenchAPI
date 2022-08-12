package com.testbench.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="test_log_master", schema = "dblocator")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestLogMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;
	
	@ManyToOne
	@JoinColumn(name = "test_id", referencedColumnName = "id")
	TestMaster testId;
	
	@ManyToOne
	@JoinColumn(name = "report_id", referencedColumnName = "id")
	ReportMaster reportId;
	
	@Column(name = "test_details_string")
	String testDetails;
	
	@Column(name = "test_result_string")
	String testResult;
	
	@Column(name = "start_timestamp")
	String startTimeStamp;
	
	@Column(name = "end_timestamp")
	String endTimeStamp;
	
	@Column(name = "pass")
	int pass;
}
