package com.topics.appointment.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topics.appointment.model.bean.Pet;

@Service
public class PetService {

	@Autowired
	private PetRepository petRepository;

	@Transactional
	public Pet insertPet(Pet pet) {
		return petRepository.save(pet);
	}
	
	public List<Pet> getPetsByMemberId(int memberId) {
		return petRepository.getPetsByMemberId(memberId);
	}
	public List<Pet> getAllPets() {
		return petRepository.findAll();
	}
	
	public Pet getPetById(int petId) {
		return petRepository.findByPetId(petId);
	}

}