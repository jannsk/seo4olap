Seo4Olap
==================

This application creates SEO-landingpages for every possible view of a datacube 
modeled in the RDF Data Cube Vocabulary [7]. 

The source code was developed for a master thesis called "Search engine optimized
presentation of statistical linked data" by Daniel Breucker at the Karlsruhe Institute
of Technology in 2016. 

The application runs on a Google App Engine (GAE) and makes use of GAE specific 
APIs, such as the Datastore and the Queuing Service.

## Products
- [App Engine][1]

## Language
- [Java][2]

## APIs
- [Google Cloud Endpoints][3]
- [Google App Engine Maven plugin][4]

## Setup Instructions

1. Update the value of `application` in `appengine-web.xml` to the app
   ID you have registered in the App Engine admin console and would
   like to use to host your instance of this sample.

2. Configure your domain and add datasets in src/main/resources/config/config.json.

3. Run the application with `mvn appengine:devserver`, and ensure it's
   running by visiting your local server's api explorer's address (by
   default [localhost:8080/_ah/api/explorer][5].)

4. Deploy your application to Google App Engine with

   $ mvn appengine:update
   
5. For live deployment, datasets have to be initialized on first use. Go to
   your-domain.com/admin to start initialization.

[1]: https://developers.google.com/appengine
[2]: http://java.com/en/
[3]: https://developers.google.com/appengine/docs/java/endpoints/
[4]: https://developers.google.com/appengine/docs/java/tools/maven
[5]: https://localhost:8080/_ah/api/explorer
[6]: https://console.developers.google.com/
[7]: https://www.w3.org/TR/vocab-data-cube/