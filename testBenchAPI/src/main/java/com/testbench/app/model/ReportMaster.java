package com.testbench.app.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "report_master", schema = "dblocator")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportMaster {
	@Id
	@GeneratedValue
    UUID id;
	
	@Column(name = "vendor_name")
	String vendorName;
	
	@Column(name = "device_imei")
	String imei;
	
	@Column(name = "device_model")
	String deviceModel;
	
	@Column(name = "address")
	String address;
	
	@Column(name = "contact_person")
	String contactPerson;
}
