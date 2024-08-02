package com.sjprogramming.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sjprogramming.restapi.entity.Students;

public interface StudentRepository extends JpaRepository<Students, Integer> {
	
	

}
