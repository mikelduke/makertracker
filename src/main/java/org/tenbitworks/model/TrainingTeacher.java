package org.tenbitworks.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trainings_teacher")
@Builder
public class TrainingTeacher {
	
	@Id
	@GeneratedValue
	private long id;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="member_id")
	private Member member;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="training_type_id")
	private TrainingType trainingType;
	
	@NotNull
	private Date addedDate;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="username")
	private User addedBy;
	
	public TrainingTeacher(long id) {
		this.id = id;
	}
}
