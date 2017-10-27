package org.tenbitworks.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name = "training_type")
public class TrainingType {
	
	@Id
	@GeneratedValue
	long id;
	
	@NotNull
	@Column(unique = true)
	String name;
	
	@Column(name = "description", length = 1000)
	String description;
	
	public TrainingType() { }

	public TrainingType(long id) {
		this.id = id;
	}
}
