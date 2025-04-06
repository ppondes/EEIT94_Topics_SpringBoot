package com.topics.appointment.model.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.topics.appointment.model.bean.Pet;
@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {

    @Query("SELECT p FROM Pet p WHERE p.owner.memberId = :memberId")
	List<Pet> getPetsByMemberId(@Param("memberId")int memberId);
	
	Pet findByPetId(int petId);
}
