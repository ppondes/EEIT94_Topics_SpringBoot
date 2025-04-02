package com.topics.appointment.model.bean;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "owner")
public class Owner {//暫時使用，之後關聯member

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int memberId;

	@Column(name = "member_name", nullable = false, length = 50)
	private String memberName;

	@Column(name = "phone_number", nullable = false, length = 15)
	private String phoneNumber;

	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "addr", nullable = false, length = 255)
	private String address;

	@Column(name = "join_date", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date joinDate;

}