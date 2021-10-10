Simple application management app using spring-boot and H2 database. Following endpoints are published:

[GET] /api/applications - returns existing applications
[POST] /api/applications - creates new application, returns id
[PUT] /api/applications/{id} - updates content of application
[PUT] /api/applications/{id}/verify - changes status to Verified
[PUT] /api/applications/{id}/accept - changes status to Accepted
[PUT] /api/applications/{id}/publish - changes status to Published
[PUT] /api/applications/{id}/reject - changes status to Rejected
[DELETE] /api/applications/{id} - changes status to Deleted (application won't be visible in system)
