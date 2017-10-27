package org.tenbitworks.repositories;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.tenbitworks.model.Member;
import org.tenbitworks.model.MemberTrainings;

public interface MemberTrainingsRepository extends CrudRepository<MemberTrainings, Long> {
	List<MemberTrainings> findAllByMember(Member member);
}
