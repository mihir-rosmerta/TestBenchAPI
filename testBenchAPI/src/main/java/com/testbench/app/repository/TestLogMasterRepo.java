package com.testbench.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.testbench.app.model.TestLogMaster;

@Repository
public interface TestLogMasterRepo extends JpaRepository<TestLogMaster, Integer>{

	@Query(value = "select tlm.* from dblocator.test_log_master tlm where tlm.report_id::::text = :serialNo", nativeQuery = true)
	public List<TestLogMaster> getAllTests(String serialNo);
}
