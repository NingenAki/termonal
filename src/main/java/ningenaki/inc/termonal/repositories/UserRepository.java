package ningenaki.inc.termonal.repositories;

import org.springframework.stereotype.Repository;

import ningenaki.inc.termonal.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
}