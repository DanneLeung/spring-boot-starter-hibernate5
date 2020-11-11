# Spring Boot Starter Hibernate5
This is a spring boot starter project for  hibernate 5.x auto configuration.

* Java8
* Spring boot 2.x
* Hibernate 5.x


## Build locally
run "git clone repo_url" to clone repository to your local driver, then run "mvn clean install" command to build jar and install it to your local m2 repositoy.

## Add dependency
 add dependency to your project pom.xml
```
  <dependency>
    <groupId>com.xcesys.extras</groupId>
    <artifactId>spring-boot-starter-hibernate5</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </dependency>
```
## Add configuration
Add a hibernate5 config section follows "spring" to application.xml, checkout Hibernate5Properties class for details.

```
  hibernate5:
    packages: com.cloud
    open-in-view: true # add OpenInView filter to web application
    # mapping-resources:
    #  - someMapping.hbm.xml 
    properties:
      hibernate: # hibernate properties goes here
        dialect: org.hibernate.dialect.MySQL8Dialect
        cache:
          use_query_cache: true
          use_second_level_cache: true
        region:
          factory_class: org.hibernate.cache.jcache.JCacheRegionFactory
        hbm2ddl:
          auto: none
        show-sql: false
        format_sql: false
        temp.use_jdbc_metadata_defaults: false # disable metadata read

```

