Simple application management app using spring-boot and H2 database. It can be started by executing:
    mvn spring-boot:run

Following endpoints are published:

* [GET] /api/applications - returns all existing applications (excluding deleted)
* [GET] /api/applications/title/{title} - searches applications that contain given phrase in title (case insensitive)
* [GET] /api/applications/status/{status} - searches applications that have given status
* [GET] /api/applications/title/{title}/status/{status} - searches both by title and status

All search methods return 10 results per page, requested page number can be passed by query parameter "page". 
Applications are ordered by date of last modification.

* [POST] /api/applications - creates new application, returns id
* [PUT] /api/applications/{id} - updates content of application
* [GET] /api/applications/{id}/history - returns archival versions of given application
* [PUT] /api/applications/{id}/verify - changes status to Verified
* [PUT] /api/applications/{id}/accept - changes status to Accepted
* [PUT] /api/applications/{id}/publish - changes status to Published
* [PUT] /api/applications/{id}/reject - changes status to Rejected
* [DELETE] /api/applications/{id} - changes status to Deleted (application won't be visible in system)

Sample requests in Postman format are placed in CRUD.postman_collection.json file. It assues application running at default http://127.0.0.1:8080/
