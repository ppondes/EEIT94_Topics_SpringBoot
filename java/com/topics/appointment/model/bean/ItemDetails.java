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
@Table(name = "item_details")
@Getter
@Setter
public class ItemDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_detail_id")
	private int itemDetailId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "appointment_id")
	private Appointment appointment;

	@Column(name = "appointment_id", insertable = false, updatable = false)
	private int appointmentId;

	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Items item;

	@Column(name = "item_id", insertable = false, updatable = false)
	private int itemId;

	@Column(name = "item_detail_quantity", nullable = false, columnDefinition = "INT DEFAULT 1")
	private int itemDetailQuantity;

	public ItemDetails() {
	}

	public ItemDetails(Appointment appointment, Items item, int itemDetailQuantity) {
		if (appointment == null) {
			throw new IllegalArgumentException("Appointment cannot be null.");
		}
		this.appointment = appointment;
		this.item = item;
		this.itemDetailQuantity = itemDetailQuantity;
	}
	
}