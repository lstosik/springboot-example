package net.purevirtual.springbootexample.boundary;

import java.time.LocalDateTime;
import java.util.List;
import net.purevirtual.springbootexample.boundary.dto.ContentRequest;
import net.purevirtual.springbootexample.boundary.dto.NegativeRequest;
import net.purevirtual.springbootexample.entity.Application;
import net.purevirtual.springbootexample.entity.ApplicationRevision;
import net.purevirtual.springbootexample.entity.ApplicationStatus;
import net.purevirtual.springbootexample.repo.ApplicationRepository;
import net.purevirtual.springbootexample.repo.ApplicationRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    
    private static final int PAGE_SIZE = 10;
    private static final Sort DEFAULT_ORDER = Sort.by("modificationTime");
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private ApplicationRevisionRepository applicationRevisionRepository;
    
    @GetMapping
    public List<Application> list(@RequestParam(defaultValue = "1", required = false) int page) {
        Pageable selectedPage = selectPage(page);
        return applicationRepository.findByStatusNot(ApplicationStatus.DELETED, selectedPage);
    }
    
    @GetMapping("/status/{status}")
    public List<Application> listWithStatus(@PathVariable ApplicationStatus status,
            @RequestParam(defaultValue = "1") int page
    ) {
        Pageable selectedPage = selectPage(page);
        return applicationRepository.findByStatus(status, selectedPage);
    }
    
    @GetMapping("/title/{title}")
    public List<Application> listWithTitle(@PathVariable String title,
            @RequestParam(defaultValue = "1") int page) {
        Pageable selectedPage = selectPage(page);
        return applicationRepository.findByTitleContainingIgnoreCase(title, selectedPage);
    }
    
    @GetMapping("/title/{title}/status/{status}")
    public List<Application> listWithTitleAndStatus(
            @PathVariable String title,
            @PathVariable ApplicationStatus status,
            @RequestParam(defaultValue = "1") int page) {
        Pageable selectedPage = selectPage(page);
        return applicationRepository.findByTitleContainingIgnoreCaseAndStatus(title, status, selectedPage);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long create(@RequestBody ContentRequest request) {
        Application application = new Application();
        application.setContent(request.getContent());
        application.setTitle(request.getTitle());
        application.setStatus(ApplicationStatus.CREATED);
        application.setModificationTime(LocalDateTime.now());
        return applicationRepository.save(application).getId();
    }
    
    @DeleteMapping("/{id}")
    public void delete(@RequestBody NegativeRequest request, @PathVariable Long id) {
        Application application = applicationRepository.getById(id);
        checkStatus(application, ApplicationStatus.CREATED);
                
        archive(application);
        application.setStatus(ApplicationStatus.DELETED);
        application.setChangeReason(request.getReason());
        applicationRepository.save(application);
    }
    
    @GetMapping("/{id}")
    public Application get(@PathVariable Long id) {
        return applicationRepository.getById(id);
    }
    
    @GetMapping("/{id}/history")
    public List<ApplicationRevision> getHistory(@PathVariable Long id) {
        Application application = applicationRepository.getById(id);
        return applicationRevisionRepository.findByApplication(application);
    }
    
    @PutMapping("/{id}")
    public void update(@RequestBody ContentRequest request, @PathVariable Long id) {
        Application application = applicationRepository.getById(id);
        checkStatus(application, ApplicationStatus.CREATED, ApplicationStatus.VERIFIED);
        archive(application);

        application.setContent(request.getContent());
        application.setTitle(request.getTitle());
        applicationRepository.save(application);
    }
    
    @PutMapping("/{id}/reject")
    public void reject(@RequestBody NegativeRequest request, @PathVariable Long id) {
        Application application = applicationRepository.getById(id);
        checkStatus(application, ApplicationStatus.VERIFIED, ApplicationStatus.ACCEPTED);
        
        archive(application);
        application.setStatus(ApplicationStatus.REJECTED);
        application.setChangeReason(request.getReason());
        applicationRepository.save(application);
    }
    
    @PutMapping("/{id}/verify")
    public void verify(@PathVariable Long id) {
        Application application = applicationRepository.getById(id);
        checkStatus(application, ApplicationStatus.CREATED);
        
        archive(application);        
        application.setStatus(ApplicationStatus.VERIFIED);
        applicationRepository.save(application);
    }
    
    @PutMapping("/{id}/accept")
    public void accept(@RequestBody ContentRequest request, @PathVariable Long id) {
        Application application = applicationRepository.getById(id);
        checkStatus(application, ApplicationStatus.VERIFIED);

        archive(application);
        application.setStatus(ApplicationStatus.ACCEPTED);
        applicationRepository.save(application);
    }
    
    @PutMapping("/{id}/publish")
    public void publish(@PathVariable Long id) {
        Application application = applicationRepository.getById(id);
        checkStatus(application, ApplicationStatus.ACCEPTED);
        
        archive(application);
        application.setStatus(ApplicationStatus.PUBLISHED);
        applicationRepository.save(application);
    }

    /**
     * Saves the state of application before change was applied
     * @param application 
     */
    private void archive(Application application) {
        ApplicationRevision revision = new ApplicationRevision();
        revision.setApplication(application);
        revision.setChangeReason(application.getChangeReason());
        revision.setContent(application.getContent());
        revision.setTitle(application.getTitle());
        revision.setModificationTime(application.getModificationTime());
        applicationRevisionRepository.save(revision);
        
        application.setModificationTime(LocalDateTime.now());
        revision.setChangeReason(null);
    }

    /**
     * Verifies if application is in given state
     * @param application
     * @param allowedStatuses 
     */
    private void checkStatus(Application application, ApplicationStatus ... allowedStatuses) {
        for (ApplicationStatus allowedStatus : allowedStatuses) {
            if(application.getStatus() == allowedStatus) {
                return;
            }
        }
        throw new InvalidStatusException(application, allowedStatuses);
    }
    
    private PageRequest selectPage(int page) {
        return PageRequest.of(Math.max(0, page - 1), PAGE_SIZE, DEFAULT_ORDER);
    }
}
