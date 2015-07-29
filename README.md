EUNIS web application
=====================
 
How to build:
-------------
You can build the application and run the unit tests with an embedded database. You don't need a database installation. Just do:
 
    mvn -Denv=unittest test

If you encounter problems, take a look at unittest.properties. This configuration is the one that is used by the Continuous Integration.

If you want your own database, then install MySQL, create a database and a user with full rights to the database. Then you copy unittest.properties to local.properties and modify the values. You should run the unit tests on the database to ensure your database connection is properly configured.

If you are using newer versions of MySQL, check that the version of mysql-connector jar (in /pom.xml) matches.

If you are using Tomcat > 7, add in `CATALINA_OPTS`:

    -Dorg.apache.el.parser.SKIP_IDENTIFIER_CHECK=true
 
For correct URI handling, the `<Connector>` in server.xml needs the following setting: URIEncoding="UTF-8".

To create the tables do:
  
```sh
cd web
mvn -Dmaven.test.skip=true liquibase:update
```

How to install:
---------------

Create images/intros in $app.home (as defined in local.properties).

When you build on the same machine as you use to install in production, you don't want to run the unit tests as it will empty your database. So always remember to tell it to skip tables.

```sh
mvn -Dmaven.test.skip=true install
cd web; mvn -Dmaven.test.skip=true liquibase:update
cp web/target/eunis.war /var/lib/tomcat6/eunis_apps/ROOT.war
```

To do a full clean build, as done in Continous Integration, run:

```sh
mvn -Denv=unittest clean cobertura:cobertura javadoc:javadoc findbugs:findbugs pmd:pmd pmd:cpd checkstyle:checkstyle
```
