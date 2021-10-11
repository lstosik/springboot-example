package net.purevirtual.springbootexample.control;

import java.time.LocalDateTime;
import net.purevirtual.springbootexample.boundary.InvalidStatusException;
import net.purevirtual.springbootexample.entity.Application;
import net.purevirtual.springbootexample.entity.ApplicationRevision;
import net.purevirtual.springbootexample.entity.ApplicationStatus;
import net.purevirtual.springbootexample.repo.ApplicationRepository;
import net.purevirtual.springbootexample.repo.ApplicationRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationFacade {
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private ApplicationRevisionRepository applicationRevisionRepository;
    
    public void updateStatus(Long applicationId, ApplicationStatus newStatus) {
        updateStatus(applicationId, newStatus, null);
    }
    
    public void updateStatus(Long applicationId, ApplicationStatus newStatus, String reason) {
        Application application = applicationRepository.getById(applicationId);
        if (!newStatus.canChangeFrom(application.getStatus())) {
            throw new InvalidStatusException(application, newStatus.getPredecessors());
        }
        
        archive(application);
        application.setStatus(newStatus);
        if (reason != null) {
            application.setChangeReason(reason);
        }
        applicationRepository.save(application);
    }
    
    /**
     * Saves the state of application before change was applied
     * @param application 
     */
    public void archive(Application application) {
        ApplicationRevision revision = new ApplicationRevision();
        revision.setApplication(application);
        revision.setChangeReason(application.getChangeReason());
        revision.setContent(application.getContent());
        revision.setTitle(application.getTitle());
        revision.setStatus(application.getStatus());
        revision.setModificationTime(application.getModificationTime());
        applicationRevisionRepository.save(revision);
        
        application.setModificationTime(LocalDateTime.now());
        application.setChangeReason(null);
    }

}
