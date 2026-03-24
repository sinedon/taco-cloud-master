package edu.iu.p566.videoScheduler.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.iu.p566.videoScheduler.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);
}