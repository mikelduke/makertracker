package org.tenbitworks.model;


import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Entity
@Table(name = "member")
@Data
@JsonInclude(Include.NON_NULL)
public class Member {

	@Id
	@GeneratedValue
	private UUID id;

	@NotNull
	private String email;

	@NotNull
	private String memberName;

	@OneToOne
	@JoinColumn(name="username")
	private User user;

	@Enumerated(EnumType.STRING)
	private MemberStatus status;

	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	private String phoneNumber;
	private String description;
	private String address;
	
	@Column(length=5)
	@NumberFormat
	private String zipCode;

	@Column(unique = true, length = 50)
	private String rfid;
	
	public Member() { }

	public Member(UUID id) { 
		this.id = id;
	}

	public Member(String uuidString) {
		this.id = UUID.fromString(uuidString);
	}

	public Member(String memberName, String email ) {
		this.email = email;
		this.memberName = memberName;
	}
}