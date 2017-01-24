package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.WebUser;

public interface SignupRepository extends JpaRepository<WebUser, Long> {

}
