package com.warren.clob.repos;

import com.warren.clob.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<User,Integer> {
    void addUser(User user);
    void deleteById(long id);
}
