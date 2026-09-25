package ningenaki.inc.termonal.server.repositories;

import org.springframework.stereotype.Repository;

import ningenaki.inc.termonal.server.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
}