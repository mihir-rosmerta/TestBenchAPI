package com.testbench.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.testbench.app.model.TestMaster;

@Repository
public interface TestMasterRepo extends JpaRepository<TestMaster, Integer>{

}
