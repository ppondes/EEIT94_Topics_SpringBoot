package com.topics.appointment.model.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.topics.appointment.model.bean.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Integer> {

}
