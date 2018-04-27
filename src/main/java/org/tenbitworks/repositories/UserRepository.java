package org.tenbitworks.repositories;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.tenbitworks.model.User;

public interface UserRepository extends CrudRepository<User, String> {

}
