package com.epam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.model.Question;
@Repository
public interface StudentRepository extends JpaRepository<Question, Integer>{
}
