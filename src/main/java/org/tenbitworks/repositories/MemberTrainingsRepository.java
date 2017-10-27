package org.tenbitworks.repositories;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.tenbitworks.model.Member;
import org.tenbitworks.model.MemberTrainings;
import org.tenbitworks.model.TrainingType;

public interface MemberTrainingsRepository extends CrudRepository<MemberTrainings, Long> {
	List<MemberTrainings> findAllByMember(Member member);
	List<MemberTrainings> findAllByTrainingType(TrainingType trainingType);
}
