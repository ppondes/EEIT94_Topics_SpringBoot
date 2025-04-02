package com.topics.appointment.model.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "packages")
@Getter
@Setter
public class ServicePackage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "package_id")
	private Integer packageId;

	@Column(name = "package_name", nullable = false, length = 10)
	private String packageName;

	@Column(name = "package_price", nullable = false)
	private Double packagePrice;

	@Column(name = "PACKAGE_DESCRIPTION", length = 20)
	private String packageDescription;

	public ServicePackage() {
	}

	public ServicePackage(String packageName, Double packagePrice, String packageDescription) {
		this.packageName = packageName;
		this.packagePrice = packagePrice;
		this.packageDescription = packageDescription;
	}

}