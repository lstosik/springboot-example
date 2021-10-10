package net.purevirtual.springbootexample.repo;

import java.util.List;
import net.purevirtual.springbootexample.entity.Application;
import net.purevirtual.springbootexample.entity.ApplicationRevision;
import org.springframework.data.repository.CrudRepository;

public interface ApplicationRevisionRepository extends CrudRepository<ApplicationRevision, Long> {
    List<ApplicationRevision> findByApplication(Application application);
}
