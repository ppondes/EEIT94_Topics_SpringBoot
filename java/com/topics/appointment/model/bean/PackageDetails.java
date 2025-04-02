package com.topics.appointment.model.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "package_details")
@Getter
@Setter
public class PackageDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "package_details_id")
	private int packageDetailsId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "appointment_id")
	private Appointment appointment;

	@Column(name = "appointment_id", insertable = false, updatable = false)
	private int appointmentId;

	@ManyToOne
	@JoinColumn(name = "package_id", nullable = false)
	private ServicePackage servicePackage;

	@Column(name = "package_id", insertable = false, updatable = false)
	private int packageId;

	@Column(name = "package_details_quantity", nullable = false, columnDefinition = "INT DEFAULT 1")
	private int packageDetailsQuantity;

	public PackageDetails() {
	}

	public PackageDetails(Appointment appointment, ServicePackage servicePackage, int packageDetailsQuantity) {
		this.appointment = appointment;
		this.servicePackage = servicePackage;
		this.packageDetailsQuantity = packageDetailsQuantity;
	}

}