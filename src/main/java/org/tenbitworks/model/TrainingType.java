package org.tenbitworks.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

@Data
@Entity
@Table(name = "training_type")
public class TrainingType {
	
	@Id
	@GeneratedValue
	long id;
	
	@NotNull
	@NotEmpty
	@Column(unique = true)
	String name;
	
	@Column(name = "description", length = 1000)
	String description;
	
	public TrainingType() { }

	public TrainingType(long id) {
		this.id = id;
	}
}
