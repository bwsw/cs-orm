# cs-entities
Simple CloudStack Entities Framework

The library provides the convenient way of working with Apache CloudStack entities through the following mechanisms:
1. Creating and retrieving the declared entities on/from Apache CloudStack server, such as users, accounts, virtual machines, tags.
It based on the extensible request builders which simplify creating CloudStack requests.
2. Base set of Apache CloudStack events to work with CloudStack Event Log (see [official documentation](http://docs.cloudstack.apache.org/projects/cloudstack-administration/en/4.9/events.html)).

## Install with SBT

Add the following to your `build.sbt`
```scala
libraryDependencies += "com.bwsw" %% "cs-entities" % "4.9.3"
```
## Getting Started      
1. Create Executor instance with specified parameters to interact with Apache CloudStack server. \
2. Create JsonSerializer instance for parsing json responses from the server. \
3. Use Executor and JsonSerializer to create a GenericDao instance, it may be an existing GenericDao or a custom implementation \
(if you implement GenericDao, first of all you have to implement one Request for each DAO method).
4. Use existing Entity response hierarchy to work with users, accounts, vws or tags \
(if you create a new GenericDao implementation for [another](http://cloudstack.apache.org/api/apidocs-4.9/) Apache CloudStack entity then you have to implement a new Entity response hierarchy).

The ![diagrams](docs/diagrams) are provided for you to understand the process of library classes relationships.

## Example Usage

The example below shows how to create new user entity in CloudStack server and then retrieve it:
```scala
val creatorSettings = PasswordAuthenticationClientCreator.Settings("admin","password","/")
val executorSettings = Executor.Settings(Array(s"http://localhost:8888/client/api"), retryDelay = 1000)

val creator = new PasswordAuthenticationClientCreator(creatorSettings)
val executor = new Executor(executorSettings, creator, true)
val mapper = new JsonMapper(true)

val userId = UUID.randomUUID()
val userCreationSettings = UserCreateRequest.Settings(
      accountName="admin",
      email = "e@example.com",
      firstName = "first",
      lastName = "last",
      password = "passwd",
      username = "username"
    )

val userCreateRequest = new UserCreateRequest(userCreationSettings).withId(userId)
userDao.create(userCreateRequest)

val findRequest = new UserFindRequest().withId(userId)
val users = userDao.find(findRequest)
```
## Testing

### Unit tests

Run tests: `sbt test`

### Integration tests

1. Add local environment variables:
    * `CS_PORT` - host of Apache CloudStack simulator server, for example - "8888"
2. Run Apache CloudStack simulator in docker container:
```bash
    docker run --rm --name resmo-cloudstack-simulator -d -p ${CS_PORT}:${CS_PORT} resmo/cloudstack-sim
```

3. After the end of the cloudstack simulator deploying (you can check it like ![this](jenkins/run_cs_simulator.sh)) execute: `sbt it:test`

## Versioning

Library has the same version as Apache CloudStack server

## License

This project is licensed under the Apache License - see the [LICENSE](LICENSE) file for details
  