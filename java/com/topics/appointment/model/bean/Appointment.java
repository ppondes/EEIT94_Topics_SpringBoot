
package com.topics.appointment.model.bean;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Appointment {

	@Id
	@Column(name = "appointment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int appointmentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
	private Owner owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pet_id", referencedColumnName = "pet_id", nullable = false)
	private Pet pet;

	@Column(name = "appointment_date")
	private String appointmentDate;

	@Column(name = "appointment_timeslot")
	private String appointmentTimeslot;

	@Column(name = "appointment_total")
	private int appointmentTotal;

	@Column(name = "appointment_status")
	private int appointmentStatus;

	@Column(name = "payment_status")
	private int paymentStatus;

	@Transient
	private String serviceNames = "";

	@Transient
	private String additionalPackages = "";

	@OneToMany(mappedBy = "appointment", fetch = FetchType.LAZY)
	private List<ItemDetails> items;

	@OneToMany(mappedBy = "appointment", fetch = FetchType.LAZY)
	private List<PackageDetails> packages;

	public Appointment() {
	};

	public Appointment(int appointmentId, Owner owner, Pet pet, String appointmentDate, String appointmentTimeslot,
			int appointmentTotal, int appointmentStatus, int paymentStatus) {
		this.appointmentId = appointmentId;
		this.owner = owner;
		this.pet = pet;
		this.appointmentDate = appointmentDate;
		this.appointmentTimeslot = appointmentTimeslot;
		this.appointmentTotal = appointmentTotal;
		this.appointmentStatus = appointmentStatus;
		this.paymentStatus = paymentStatus;
	}

	public Appointment(Owner owner, Pet pet, String appointmentDate, String appointmentTimeslot, int appointmentTotal,
			int appointmentStatus, int paymentStatus) {
		this.owner = owner;
		this.pet = pet;
		this.appointmentDate = appointmentDate;
		this.appointmentTimeslot = appointmentTimeslot;
		this.appointmentTotal = appointmentTotal;
		this.appointmentStatus = appointmentStatus;
		this.paymentStatus = paymentStatus;
	}

	
}
