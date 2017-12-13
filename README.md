# cs-entities
Simple CloudStack Entities Framework

The library provides the convenient way of working with Apache CloudStack entities through the following mechanisms:
1. Creating and retrieving the declared entities on/from Apache CloudStack server, such as users, accounts, virtual machines, tags.
It based on extensible Request which simplify creating requests to CloudStack.
2. Base set of Apache CloudStack events, for retrieving their from Apache CloudStack messages.

## Install with SBT

Add the following to your `build.sbt`
```scala
libraryDependencies += "com.bwsw" %% "cs-entities" % "4.9.3"
```
## Getting Started      

The diagram below is a simple illustration of how the library's classes should be used. \
![Sequence](docs/diagrams/cs_entities_user_sequence.png)

Implement your GenericDao, Request, and Response hierarchy for working with another entities \

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
    docker run --rm --name resmo-simulator-kafka -d -p ${CS_PORT}:${CS_PORT} resmo/cloudstack-sim
```

3. After the end of the cloudstack simulator deploying (you can check it like in ![here](jenkins/run_cs_simulator.sh)) execute: `sbt it:test`

## Versioning

Library has the same version as Apache CloudStack server

## License

This project is licensed under the Apache License - see the [LICENSE](LICENSE) file for details
  