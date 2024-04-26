package com.klimmenkov.testtask.repository;

import com.klimmenkov.testtask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByBirthDateBetween(Date fromDate, Date toDate);

}
