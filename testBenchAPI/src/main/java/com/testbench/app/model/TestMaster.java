package com.testbench.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "test_master", schema = "dblocator")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestMaster {

	@Id
	int id;
	
	@Column(name="name")
	String name;
}
