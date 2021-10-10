package net.purevirtual.springbootexample.repo;

import java.util.List;
import java.util.Optional;
import net.purevirtual.springbootexample.entity.Application;
import net.purevirtual.springbootexample.entity.ApplicationStatus;
import org.springframework.data.repository.CrudRepository;

public interface ApplicationRepository extends CrudRepository<Application, Long>, ApplicationRepositoryCustom {
    List<Application> findByStatusNot(ApplicationStatus status);
}
