# Tax Forecaster

### Description

The goal of this project is to calculate the amount of tax paid based on the employee's salary and the tax rate entered
in different salary ranges.

### Config

The project configurations are registered in two places in the project: one is the config package that includes security
and swagger, and another is the .properties file that contains the following configurations:

* Server : The configurations in this section are related to the entire project, such as the port (which defaults
  to `1200`) and the type of logs displayed in the console.
* Database : The database type configuration is currently MySql with port 3306 and also `ddl-auto` related to hibernate
  which is configured as `update`.

### Tools

The following guides illustrate how to use some features concretely:

* Caffeine : The project cache is handled by this tool, which has high write and read speeds, as well as using the
  W-TinyLFU algorithm for deletion and supporting Async Loading.
* jjwt-api : Java library for creating and managing JSON Web Token (JWT) that is simpler, faster, and more secure than
  other libraries such as nimbus-jose-jwt and auth0-java-jwt.

### Project Packages

* Config : All project and runner configurations such as swagger, oauth, etc., as well as if @bean is defined, are
  implemented in this class of this package.
* Common : Classes that are related to all packages and used throughout the project are included in this package, such
  as Exceptions and the user class.
* Controller : All APIs are handled in this package which is the closest package to the client. Also, the Model package
  which contains all the dtos and models.
* Service : This package contains classes whose task is to implement the project's business and is associated with the
  class repository.
* Repository : This package contains classes that are related to the database and all queries are handled in these
  classes.
* Entity : In this package, classes contain @Entity which specifies the columns and database connections.

