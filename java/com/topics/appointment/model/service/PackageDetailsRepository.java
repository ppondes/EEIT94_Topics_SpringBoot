package com.topics.appointment.model.service;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.topics.appointment.model.bean.PackageDetails;

@Repository
public interface PackageDetailsRepository extends JpaRepository<PackageDetails, Integer> {

	 @Transactional
	    @Modifying
	    @Query("DELETE FROM PackageDetails p WHERE p.appointment.appointmentId = :appointmentId")
	    void deleteByAppointmentId(@Param("appointmentId") int appointmentId);

	 @Query("SELECT pd.servicePackage.packageId FROM PackageDetails pd WHERE pd.appointment.appointmentId = :appointmentId")
	    List<Integer> findPackageIdsByAppointmentId(@Param("appointmentId") int appointmentId);

	PackageDetails findByAppointmentIdAndPackageId(int appointmentId, int packageId);
}
