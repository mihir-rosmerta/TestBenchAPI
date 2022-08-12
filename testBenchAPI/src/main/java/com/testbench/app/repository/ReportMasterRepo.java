package com.testbench.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.testbench.app.model.ReportMaster;

@Repository
public interface ReportMasterRepo extends JpaRepository<ReportMaster, Integer>{
	
	@Query(value = "select * from dblocator.report_master where id::::text = :serialNo", nativeQuery = true)
	public ReportMaster getReportBySerialNo(String serialNo);
}
