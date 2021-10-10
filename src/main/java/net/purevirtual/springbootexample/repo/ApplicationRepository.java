package net.purevirtual.springbootexample.repo;

import java.util.List;
import net.purevirtual.springbootexample.entity.Application;
import net.purevirtual.springbootexample.entity.ApplicationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long>, ApplicationRepositoryCustom {
    List<Application> findByStatus(ApplicationStatus status, Pageable pageable);
    List<Application> findByStatusNot(ApplicationStatus status, Pageable pageable);    
    List<Application> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    List<Application> findByTitleContainingIgnoreCaseAndStatus(String title, ApplicationStatus status, Pageable pageable);
}
