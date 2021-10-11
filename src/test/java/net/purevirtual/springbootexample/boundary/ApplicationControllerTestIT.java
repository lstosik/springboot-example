package net.purevirtual.springbootexample.boundary;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.List;
import net.purevirtual.springbootexample.boundary.dto.ContentRequest;
import net.purevirtual.springbootexample.boundary.dto.NegativeRequest;
import net.purevirtual.springbootexample.entity.Application;
import net.purevirtual.springbootexample.entity.ApplicationStatus;
import org.apache.commons.lang3.RandomStringUtils;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationControllerTestIT {
    
    private static final String API_ROOT = "/api/applications";
    
    @LocalServerPort
    int port;
    
    @BeforeEach
    public void init(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    public void testList() {
        System.out.println("list");
        Response get = RestAssured.get(API_ROOT);
        List<Application> applications = get.as(List.class);
    }
    
    @Test
    public void testCreate() {
        System.out.println("create");
        ContentRequest contentRequest = new ContentRequest();
        contentRequest.setTitle("some title");
        contentRequest.setContent("some content");
        
        int id = create(contentRequest);
        Application application = getApplication(id);
        assertThat(application.getTitle()).isEqualTo(contentRequest.getTitle());
        assertThat(application.getContent()).isEqualTo(contentRequest.getContent());
    }
    
    @Test
    public void testCreate_empty() {
        System.out.println("create");
        ContentRequest contentRequest = new ContentRequest();
        
        Response get = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contentRequest)
                .post(API_ROOT);
        assertThat(get.getStatusCode()).isGreaterThanOrEqualTo(300);
    }
    
    @Test
    public void testGet_wrongID() {
        System.out.println("get_wrongID");

        Response get = RestAssured.given()
                .get(API_ROOT + "/123123123");
        assertThat(get.getStatusCode()).isEqualTo(404);
    }
    
    @Test
    public void testHappyPath() {
        System.out.println("happyPath");
        ContentRequest contentRequest = new ContentRequest();
        contentRequest.setTitle("some title");
        contentRequest.setContent("some content");
        
        int id = create(contentRequest);
        assertThat(getStatus(id)).isEqualTo(ApplicationStatus.CREATED);
        
        Response verify = RestAssured.given()
                .put(API_ROOT+"/"+id+"/verify");
        checkHttpCode(verify);
        assertThat(getStatus(id)).isEqualTo(ApplicationStatus.VERIFIED);
        
        Response accept = RestAssured.given()
                .put(API_ROOT+"/"+id+"/accept");
        checkHttpCode(accept);
        assertThat(getStatus(id)).isEqualTo(ApplicationStatus.ACCEPTED);
        
        Response publish = RestAssured.given()
                .put(API_ROOT+"/"+id+"/publish");
        checkHttpCode(publish);
        assertThat(getStatus(id)).isEqualTo(ApplicationStatus.PUBLISHED);
    }
    
    @Test
    public void testDelete() {
        System.out.println("delete");
        ContentRequest contentRequest = new ContentRequest();
        contentRequest.setTitle("some title");
        contentRequest.setContent("some content");
        
        int id = create(contentRequest);
        assertThat(getStatus(id)).isEqualTo(ApplicationStatus.CREATED);
        
        NegativeRequest negativeRequest = new NegativeRequest();
        negativeRequest.setReason("invalid application rejected");
        Response delete = RestAssured.given()
                .body(negativeRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .delete(API_ROOT+"/"+id);
        checkHttpCode(delete);
        assertThat(getStatus(id)).isEqualTo(ApplicationStatus.DELETED);
    }
    
    @Test
    public void testReject() {
        System.out.println("reject");
        ContentRequest contentRequest = randomContentRequest();
        
        int id = create(contentRequest);
        assertThat(getStatus(id)).isEqualTo(ApplicationStatus.CREATED);
        
        Response verify = RestAssured.given()
                .put(API_ROOT+"/"+id+"/verify");
        checkHttpCode(verify);
        
        NegativeRequest negativeRequest = new NegativeRequest();
        negativeRequest.setReason("application rejected");
        Response reject = RestAssured.given()
                .body(negativeRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .put(API_ROOT+"/"+id+"/reject");
        checkHttpCode(reject);
        assertThat(getStatus(id)).isEqualTo(ApplicationStatus.REJECTED);
    }

    private ContentRequest randomContentRequest() {
        ContentRequest contentRequest = new ContentRequest();
        contentRequest.setTitle("some title "+RandomStringUtils.randomAlphabetic(10));
        contentRequest.setContent("some content "+RandomStringUtils.randomAlphabetic(20));
        return contentRequest;
    }

    private int create(ContentRequest contentRequest) {
        Response get = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(contentRequest)
                .post(API_ROOT);
        checkHttpCode(get);
        int id = get.as(Integer.class);
        return id;
    }
    
    private ApplicationStatus getStatus(int applicationId) {
        return getApplication(applicationId).getStatus();
    }

    private Application getApplication(int applicationId) {
        Response get = RestAssured.given()
                .get(API_ROOT+"/"+applicationId);
        checkHttpCode(get);
        Application application = get.as(Application.class);
        return application;
    }

    private void checkHttpCode(Response get) {
        assertThat(get.getStatusCode()).isBetween(200, 299);
    }
}
