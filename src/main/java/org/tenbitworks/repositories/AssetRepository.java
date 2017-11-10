package org.tenbitworks.repositories;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.tenbitworks.model.Asset;
import org.tenbitworks.model.TrainingType;

public interface AssetRepository extends CrudRepository<Asset, Long> {
	Asset findOneByTenbitId(String tenbitId);
	List<Asset> findAllByTrainingType(TrainingType trainingType);
}
