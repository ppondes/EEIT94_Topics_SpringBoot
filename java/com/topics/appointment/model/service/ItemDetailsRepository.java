package com.topics.appointment.model.service;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.topics.appointment.model.bean.ItemDetails;

import jakarta.transaction.Transactional;

@Repository
public interface ItemDetailsRepository extends JpaRepository<ItemDetails, Integer> {

	@Transactional
    @Modifying
    @Query("DELETE FROM ItemDetails i WHERE i.appointment.appointmentId = :appointmentId")
    void deleteByAppointmentId(@Param("appointmentId") int appointmentId);
	
	@Query("FROM ItemDetails i WHERE i.appointment.appointmentId = :appointmentId")
    ItemDetails findByAppointmentId(int appointmentId);
	
	@Query("SELECT id.item.itemName FROM ItemDetails id WHERE id.appointment.appointmentId = :appointmentId")
    List<String> findServiceNamesByAppointmentId(@Param("appointmentId") int appointmentId);
	
	
	boolean existsByAppointment_AppointmentIdAndItem_ItemId(int appointmentId, int serviceId);

	ItemDetails findByAppointmentIdAndItemId(int appointmentId, int itemId);
}
