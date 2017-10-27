package org.tenbitworks.repositories;


import org.springframework.data.repository.CrudRepository;
import org.tenbitworks.model.TrainingType;

public interface TrainingTypeRepository extends CrudRepository<TrainingType, Long> {
	
}
