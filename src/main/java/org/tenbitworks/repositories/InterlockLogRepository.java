package org.tenbitworks.repositories;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.tenbitworks.model.InterlockLogEntry;

public interface InterlockLogRepository extends PagingAndSortingRepository<InterlockLogEntry, Long> {
	
}
