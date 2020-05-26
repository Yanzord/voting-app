## Voting Application Project

This project was created for a technical challenge and its README is divided in the following sections:

- [The Challenge](https://github.com/Yanzord/voting-app/blob/master/README.md#the-challenge)
- [Considerations](https://github.com/Yanzord/voting-app/blob/master/README.md#considerations)
- [Configuration](https://github.com/Yanzord/voting-app/blob/master/README.md#configuration)
- [How It Works](https://github.com/Yanzord/voting-app/blob/master/README.md#how-it-works)
- [Future Improvements](https://github.com/Yanzord/voting-app/blob/master/README.md#future-improvements)

## The Challenge

In cooperatives, each member has one vote and decisions are taken in assemblies, by vote. From there, you need to create a back-end solution to manage these voting sessions. This solution must be executed in the cloud and promote the following functionalities through a REST API:
- Register a new agenda;
- Open a voting session on an agenda (the voting session must be open for a specified time in the opening call or 1 minute by default);
- Receive votes from members on agendas (votes are only 'Yes' / 'No'. Each member is identified by a unique id and can vote only once per agenda);
- Count the votes and give the result of the vote on the agenda.


For exercise purposes, the security of the interfaces can be abstracted and any call to the interfaces can be considered as authorized. The choice of language, frameworks and libraries is free (as long as it does not infringe use rights).

It is important that the agendas and votes are persisted and that they are not lost with the application restart.


### Bonus tasks
Bonus tasks are not mandatory, but we can evaluate other knowledge that you may have.

We always suggest that the candidate consider and delivers as far as he can do, considering his
level of knowledge and quality of delivery.
#### Bonus Task 1 - Integration with external systems
Integrate with a system that verifies, from the member's CPF, he can vote
- GET https://user-info.herokuapp.com/users/{cpf}
- If the CPF is invalid, an API will return HTTP Status 404 (Not found). You can use CPFs to generate valid CPFs;
- If the CPF is valid, an API returned by the user can (ABLE_TO_VOTE) or cannot (UNABLE_TO_VOTE) perform an operation
Service return examples

#### Bonus Task 2 - Messaging and Queues
Information classification: Internal Use
The voting result needs to be informed for the remaining platform, this should preferably be done through messaging. When a voting session closes, post a message with the result of the vote.

#### Bonus Task 3 - Performance
Imagine that your application can be used in scenarios where there are hundreds of thousands of votes. It must behave in a performative manner in these situations;
- Performance tests are a good way to guarantee and observe how your application behaves.

#### Bonus Task 4 - API Version
How would you version an API for your application? What strategy to use?

## Considerations

Thinking about scalability, decoupling and easy maintenance, I decided to divide my application into three microservices, 
one responsible for saving the information related to the voting agendas, another responsible for saving the voting sessions, 
and a third aggregating service that performs the logic necessary for the creation of voting agendas, as well as the record of their voting sessions.

This implementation may have hindered the application's performance, seeing that every time the logic service needs an information 
in the database it has to make a request to the others services. However, it makes it easier to change the application logic and add new features.

#### Technologies

This project is running with [Spring Boot](https://spring.io/projects/spring-boot), it has data persistence with [MongoDB](https://www.mongodb.com/), 
but [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix) is the key here:
- HTTP Requests with [OpenFeign](https://spring.io/projects/spring-cloud-openfeign);
- Service discovery with [Eureka](https://spring.io/guides/gs/service-registration-and-discovery/);
- Fault tolerance with [Hystrix](https://spring.io/guides/gs/circuit-breaker/).

To finish it has [Log4j](http://logging.apache.org/log4j/1.2/) for logging and [Docker](https://www.docker.com/) for containerization.

## Configuration

To run this project you'll need [Docker](https://docs.docker.com/desktop/) and [Docker Compose](https://docs.docker.com/compose/install/).

Clone this repo to your PC:

    git clone https://github.com/Yanzord/voting-app.git
    
Pull these two Docker images:
    
    docker pull mongo
    docker pull springcloud/eureka
    
Execute the following command to build the war file of the services:
    
    ./gradle-build-all.sh
    
Execute the following command to build the services docker images:

    ./build-docker-images.sh
    
Execute the following command to run the application:

    docker-compose up -d
    
## How it works

The main application has 4 endpoints:

- **POST /app/agenda/** -> register a new voting agenda.
    - Request body:
    ```
    {
        "description": "Do you want to code?"
    }
    ```
    - Response body:
    ```
    {
        "id": "5ecce541870f2f5e08ab9c39",
        "description": "Do you know how to code?",
        "agendaResult": null,
        "status": "NEW"
    }
    ```

- **POST /app/session/** -> creates a new voting session, duration is declared in minutes, 1 minute by default if none informed.
    - Request body:  
    ```
    {
        "agendaId": "5ecce541870f2f5e08ab9c39",
        "duration": "2"
    }
    ```
    - Response body:
    ```
    {
        "id": "5ecce547281753676d328633",
        "agendaId": "5ecce541870f2f5e08ab9c39",
        "duration": 2,
        "startDate": "2020-05-26T09:45:43.536",
        "endDate": "2020-05-26T09:47:43.536",
        "votes": null,
        "status": "OPENED"
    }
    ```
  - Returns **400 BAD REQUEST** when the given agendaId already has an created session.
  - Returns **404 NOT FOUND** when the given agendaId does not match any registered agenda in the database.

- **POST /app/session/{agendaId}** -> register a new vote, returns the session.
    - Request body:  
    ```
    {
    	"associateId": "1",
    	"associateCPF": "86284357001",
    	"voteChoice": "SIM"
    }
    ```
    - Response body:
    ```
    {
        "id": "5ecce547281753676d328633",
        "agendaId": "5ecce541870f2f5e08ab9c39",
        "duration": 2,
        "startDate": "2020-05-26T09:45:43.536",
        "endDate": "2020-05-26T09:47:43.536",
        "votes": [
            {
                "associateId": "1",
                "associateCPF": "86284357001",
                "voteChoice": "SIM"
            }
        ],
        "status": "OPENED"
    }
    ```
    - Returns **400 BAD REQUEST** when:
        - The session duration is over;
        - The associate has already voted;
        - The associateCPF is unable to vote;
        - The associateCPF is invalid.
    - Returns **404 NOT FOUND** when:
        - The given agendaId does not match any registered agenda in the database;
        - The given agendaId does not match any registered session in the database.

- **GET /app/result/{agendaId}** -> get the given agendaId result.
    - Response body:
    ```
    {
        "totalUpVotes": "1",
        "totalDownVotes": "0",
        "result": "SIM"
    }
    ```
    - Returns **400 BAD REQUEST** when:
        - The agenda is new, and the session is open.
    - Returns **404 NOT FOUND** when:
        - The given agendaId does not match any registered agenda in the database;
        - The given agendaId does not match any registered session in the database.


The application has an dashboard provided by Hystrix Dashboard:
   - Dashboard url: http://localhost:8080/hystrix
   - Type the hystrix.stream url into the hystrix dashboard field to start monitoring: http://localhost:8080/actuator/hystrix.stream
## Future Improvements

- Messaging;
- Performance tests;
- API versioning.