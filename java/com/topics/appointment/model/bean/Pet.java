package com.topics.appointment.model.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "pets")
@Getter
@Setter
public class Pet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pet_id")
	private int petId;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Owner owner;

	@Column(name = "pet_name")
	private String petName;

	@Column(name = "pet_type")
	private String petType;

	@Column(name = "pet_size")
	private String petSize;

	@Column(name = "pet_fur")
	private String petFur;

	@Column(name = "pet_status")
	private int petStatus;

	public Pet() {
	}

	public Pet(Owner owner, int petId, String petName, String petType, String petSize, String petFur, int petStatus) {
		this.owner = owner;
		this.petId = petId;
		this.petName = petName;
		this.petType = petType;
		this.petSize = petSize;
		this.petFur = petFur;
		this.petStatus = petStatus;
	}

}