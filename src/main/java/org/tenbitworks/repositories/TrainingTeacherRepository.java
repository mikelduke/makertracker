package org.tenbitworks.repositories;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.tenbitworks.model.Member;
import org.tenbitworks.model.TrainingTeacher;
import org.tenbitworks.model.TrainingType;

public interface TrainingTeacherRepository extends CrudRepository<TrainingTeacher, Long> {
	List<TrainingTeacher> findAllByTrainingType(TrainingType trainingType);
	List<TrainingTeacher> findAllByMember(Member member);
}
