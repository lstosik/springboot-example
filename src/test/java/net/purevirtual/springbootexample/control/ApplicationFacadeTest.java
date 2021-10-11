package net.purevirtual.springbootexample.control;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import net.purevirtual.springbootexample.boundary.InvalidStatusException;
import net.purevirtual.springbootexample.entity.Application;
import net.purevirtual.springbootexample.entity.ApplicationRevision;
import net.purevirtual.springbootexample.entity.ApplicationStatus;
import net.purevirtual.springbootexample.repo.ApplicationRepository;
import net.purevirtual.springbootexample.repo.ApplicationRevisionRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
public class ApplicationFacadeTest {
    
    @InjectMocks
    ApplicationFacade instance = new ApplicationFacade();
    
    @Mock
    private ApplicationRepository applicationRepository;
    
    @Mock
    private ApplicationRevisionRepository applicationRevisionRepository;
    private List<ApplicationRevision> revisions;
    
    @BeforeEach
    public void setUp() {
        
    }

    private void recordRevisions() {
        revisions = new ArrayList<>();
        when(applicationRevisionRepository.save(any())).thenAnswer(new Answer<Void>(){
            @Override
            public Void answer(InvocationOnMock context) throws Throwable {
                ApplicationRevision revision = (ApplicationRevision)context.getArgument(0);
                revisions.add(revision);
                return null;
            }
            
        });
    }
    
    @Test
    public void testUpdateStatus_simple_ok() {
        System.out.println("testUpdateStatus_simple_ok");
        recordRevisions();
        Long applicationId = 123L;
        Application application = new Application();
        application.setStatus(ApplicationStatus.VERIFIED);
        when(applicationRepository.getById(applicationId)).thenReturn(application);
        ApplicationStatus newStatus = ApplicationStatus.ACCEPTED;
        
        instance.updateStatus(applicationId, newStatus);
        
        assertThat(application.getChangeReason()).isNullOrEmpty();
        assertThat(application.getStatus()).isEqualTo(newStatus);
        assertThat(revisions).hasSize(1);
    }

    /**
     * Test of updateStatus method, of class ApplicationFacade.
     */
    @Test
    public void testUpdateStatus_3args_ok() {
        System.out.println("testUpdateStatus_3args_ok");
        recordRevisions();
        Long applicationId = 123L;
        Application application = new Application();
        application.setStatus(ApplicationStatus.ACCEPTED);
        when(applicationRepository.getById(applicationId)).thenReturn(application);
        ApplicationStatus newStatus = ApplicationStatus.REJECTED;
        String reason = "some reason";
        instance.updateStatus(applicationId, newStatus, reason);
        
        assertThat(application.getChangeReason()).isEqualTo(reason);
        assertThat(application.getStatus()).isEqualTo(newStatus);
        assertThat(revisions).hasSize(1);
    }
    
    @Test
    public void testUpdateStatus_3args_wrong() {
        System.out.println("testUpdateStatus_3args_wrong");
        Long applicationId = 123L;
        Application application = new Application();
        application.setStatus(ApplicationStatus.ACCEPTED);
        when(applicationRepository.getById(applicationId)).thenReturn(application);
        ApplicationStatus newStatus = ApplicationStatus.DELETED;
             
        String reason = "some reason";
        assertThrows(
                InvalidStatusException.class,
                () -> instance.updateStatus(applicationId, newStatus, reason));
        assertThat(application.getStatus()).isNotEqualTo(newStatus);
    }

    /**
     * Test of archive method, of class ApplicationFacade.
     */
    @Test
    public void testArchive() {
        System.out.println("archive");
        recordRevisions();
        LocalDateTime oldTime = LocalDateTime.parse("2007-12-03T10:15:30");
        Application application = new Application();
        application.setStatus(ApplicationStatus.CREATED);
        application.setContent("old content");
        application.setTitle("old title");
        application.setChangeReason("reason");
        application.setModificationTime(oldTime);
        instance.archive(application);
        
        assertThat(revisions).hasSize(1);
        ApplicationRevision revision = revisions.get(0);
        assertThat(revision.getApplication()).isEqualTo(application);
        assertThat(revision.getTitle()).isEqualTo(application.getTitle());
        assertThat(revision.getContent()).isEqualTo(application.getContent());
        assertThat(revision.getChangeReason()).isEqualTo("reason");
        assertThat(revision.getModificationTime()).isEqualTo(oldTime);
        assertThat(application.getChangeReason()).isNull();
        assertThat(application.getModificationTime()).isNotNull();
    }
    
}
