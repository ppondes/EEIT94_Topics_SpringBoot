package com.topics.appointment.model.service;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.topics.appointment.model.bean.Items;

@Repository
public interface ItemsRepository extends JpaRepository<Items, Integer> {

	

}
