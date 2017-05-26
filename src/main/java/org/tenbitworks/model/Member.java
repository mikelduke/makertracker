package org.tenbitworks.model;


import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "member")
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
	private String zipCode;

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

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public MemberStatus getStatus() {
		return status;
	}

	public void setStatus(MemberStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Member{" +
				"id=" + id +
				", email='" + email + '\'' +
				", status='" + status + '\'' +
				", memberName='" + memberName + '\'' +
				'}';
	}
}