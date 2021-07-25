# vertx-async-to-sync

## Problem statement

Suppose we have two services - A and B. 

In a trivial and everyday scenario, client  makes request to A. A then does some operations and makes a rest call request to B. B does some operations of it's own and gets back with a response to A. A receives the response from B, does some operations and sends response back to the client. 

What if the api provided by B is **asynchronous** in nature?

For problem purpose, let's now give B some defined characteristics-
1. Expects caller's callbackUrl in request body
2. Upon receiving request from caller, performs some basic validtions of the request body.
3. If the validation is successful, returns an *acknowledgement* response to the caller
4. After that, it does the actual *job* in *asynchronous* manner
5. Upon completion of the *job*, it sends the response in caller's callbackUrl (passed during original request)

This complicates our previously mentioned trivial scenario. A will now only get the acknowledgement response in the initial rest call to B. It will receive the *actual* callback response in it's callbackUrl.

If we are to keep the behavior / user journey unchanged in client side (makes request to A and expects actual *response*) - this requires a level of engineering. 

This project aims to solve the problem statement using vert.x and it's built in event bus.


![alt text](https://github.com/tahniat-ashraf/vertx-async-to-sync/blob/master/vertx-async-to-sync.png)

## Summary of Approach

*Note: vertx-consumer-sync module replicates A, vertx-producer-async module replicates B*

To add certain level of complexity, B service provides **CRUD** service based APIs on the entity class **POST**. MongoDB has been used as the underlying database. 
When client makes request to A, A then makes request to B. Upon making a request to B, A starts a timer of 29 seconds, within which both the  *acknowledgement* and *callback* must reach A. 

- In a *happy* scenario - B receives request from A, validates the request. Validation is successful. B sends *acknowledgement* response back to A. It then performs some MongoDB operation and sends the response in A's callbackUrl. This whole process is completed with 29 second. A sends the final response back to client.
- *Not so happy* scenario (1) - B receives request from A, validates the request. Validation fails. Sends *failure*  *acknowledgement* response back to A. A sends the final failure message back to client.
- *Not so happy* scenario (2) - B receives request from A, validates the request. Validation is successful. B sends *acknowledgement* response back to A. It then performs some MongoDB operations and sends the response in A's callbackUrl. The whole process exceeds the limit of 29 seconds. A sends timeout response back to client.

## How to Run

### Pre-requisites
1. Docker
2. Java 11

### Steps
1. Open terminal. Start the docker containers. Write `docker-compose up -d`
2. Open terminal. Start verx-consumer-sync (service **A**)
```
cd vertx-consumer-sync
mvn clean package
java -jar target/*-fat.jar
```
3. Similarly, start vertx-producer-async (service **B**)

```
cd ../vertx-producer-async
mvn clean package
java -jar target/*-fat.jar
```
4. Open terminal. Insert 100 sample posts in our mongodb collection for test purpose
`curl --location --request POST 'localhost:9081/addBulkPosts'`

## Testing

### CURL Commands

- Find all Posts

`curl --location --request GET 'localhost:9080/posts'`
- Get Post by id

`curl --location --request GET 'localhost:9080/posts/1'`
- Create new Post

```
curl --location --request POST 'localhost:9080/posts' \
--header 'Content-Type: application/json' \
--data-raw '{
    "userId": 1,
    "id": 101,
    "title": "Lorem Ipsum Title",
    "body": "Lorem Ipsum Body"
}'
```
- Update Post

```
curl --location --request PUT 'localhost:9080/posts' \
--header 'Content-Type: application/json' \
--data-raw '{
    "userId": 1,
    "id": 101,
    "title": "Lorem Ipsum Title - NEW",
    "body": "Lorem Ipsum Body - NEW"
}'
```
- Delete Post by id

`curl --location --request DELETE 'localhost:9080/posts/101'`
