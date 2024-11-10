# 🌱 Spring and Spring Boot Overview

Spring and Spring Boot are Java frameworks that help developers build applications in an efficient and scalable way. Think of them as toolkits for organizing and structuring code.

## 🌼 Spring

Spring is a **comprehensive framework** that offers a variety of modules to support different types of applications.

For this project **Cash Card API**, we use:
- **Spring MVC** for the web app
- **Spring Data** for data access
- **Spring Security** for authentication and authorization

> ⚠️ Spring's versatility can make setup a bit complex. Developers often need to configure different components manually to get everything running.

## 🚀 Spring Boot

Spring Boot simplifies the Spring experience! 🧰 It like an **opinionated version of Spring**—it comes with many pre-configured settings and dependencies commonly used in Spring apps. This makes it super easy to get started without manual setup.

Spring Boot includes an **embedded web server**— we don’t need an external server to deploy web apps.

### Summary
- **Spring:** Powerful, flexible but can be overwhelming.
- **Spring Boot:** Streamlined, fast to set up, with many built-in features.

## 🔄 Spring's Inversion of Control Container

Spring Boot utilizes Spring Core’s **Inversion of Control (IoC) container**. This feature will be essential for this application.

💡 **Inversion of Control** gives you flexibility with dependencies. You can specify different configurations for different scenarios (e.g., local development vs. production database) without hard-coding.

- Often, **dependency injection (DI)** is used within IoC, although not exactly the same thing.
- In Spring, the terms are often used interchangeably.

More about [Spring’s IoC Container](https://docs.spring.io/spring-framework/reference/core/beans.html) in the official docs.

## 🛍️ Spring Initializr

When starting a new Spring Boot project, **Spring Initializr** is the way to go. It is where you configure what you need and select dependencies for the app.

- Fill in project metadata, add dependencies, and generate a complete, ready-to-run **Spring Boot application**.

---

# 🌐 API Contracts & JSON

How should the API behave?

- How should API consumers interact with the API?
- What data do consumers need to send in various scenarios?
- What data should the API return to consumers, and when?
- What does the API communicate when it's used incorrectly (or something goes wrong)?

We should document these agreements not only in a shared documentation system, but also in a way that supports automated tests passing (or failing) based upon these decisions.

This is where the concept of contracts comes in.

## 🤝 API Contracts
The software industry has adopted several patterns for capturing agreed-upon API behavior in documentation and code. These agreements are often called "contracts". Two examples include Consumer Driven Contracts and Provider Driven Contracts. 

We define an API contract as a formal agreement between a software provider and a consumer that abstractly communicates how to interact with each other. This contract defines how API providers and consumers interact, what data exchanges looks like, and how to communicate success and failure cases.

The provider and consumers do not have to share the same programming language, only the same API contracts. For this project, there's one contract between the Cash Card service and all services using it. Below is an example of that first API contract.
```text
   Request
       URI: /cashcards/{id}
       HTTP Verb: GET
       Body: None
   
   Response:
       HTTP Status:
           200 OK if the user is authorized and the Cash Card was successfully retrieved
           401 UNAUTHORIZED if the user is unauthenticated or unauthorized
           404 NOT FOUND if the user is authenticated and authorized but the Cash Card cannot be found
       Response Body Type: JSON
       Example Response Body:
           {
           "id": 99,
           "amount": 123.45
           }
```

### 💡 Why Are API Contracts Important?
API contracts are important because they communicate the behavior of a REST API. They provide specific details about the data being serialized (or deserialized) for each command and parameter being exchanged. The API contracts are written in such a way that can be easily translated into API provider and consumer functionality, and corresponding automated tests.

## 📄 What Is JSON?
JSON (Javascript Object Notation) provides a data interchange format that represents the particular information of an object in a format that you can easily read and understand.

```json
{
  "id": 99,
  "amount": 123.45
}
```
Other popular data formats include YAML (Yet Another Markup Language) and XML (Extensible Markup Language). When compared to XML, JSON reads and writes quicker, is easier to use, and takes up less space. You can use JSON with most modern programming languages and on all major platforms. It also works seamlessly with Javascript-based applications.

For these reasons, JSON has largely superseded XML as the most widely used format for APIs used by Web apps, including REST APIs.

---

# 🧪 Testing First

## What Is Test Driven Development (TDD)?

Typically, automated tests are written **after** the application feature code, but in **Test Driven Development (TDD)**, tests are written **first**! By defining expected behavior before implementation, TDD helps us design the system based on what we **want it to do**, rather than what it already does.

### 🔍 Why Use TDD?
- TDD ensures we write only the **minimum code** necessary to meet the requirements.
- Passing tests mean both working code and a safeguard against future regressions.

## 📐 The Testing Pyramid

Different types of tests have unique roles and impact at various levels of the system. Balancing **speed**, **maintenance cost**, and **confidence** forms the “testing pyramid”:

![](.README_images/1507e6c4.png)
1. **Unit Tests**:
    - **Scope**: Tests isolated “units” of the system.
    - **Characteristics**: Simple, fast, and abundant.
    - **Purpose**: Essential for designing cohesive, loosely coupled software.

2. **Integration Tests**:
    - **Scope**: Tests a group of units working together.
    - **Characteristics**: Slower and more complex than unit tests.
    - **Purpose**: Ensures components interact correctly.

3. **End-to-End Tests**:
    - **Scope**: Tests the entire system from the user’s perspective.
    - **Characteristics**: Slow, often fragile, but thorough.
    - **Purpose**: Validates that the system works as a whole.

## 🔄 The Red, Green, Refactor Loop

How can development teams **move fast and stay reliable**? By continuously **refactoring** to keep code clean and manageable, supported by a solid test suite. The **Red, Green, Refactor loop** is central to TDD:

- **Red**: Write a failing test that defines the desired functionality.
- **Green**: Implement the simplest solution to make the test pass.
- **Refactor**: Simplify and improve code without changing functionality.

🔁 **Repeat** until you’ve built the functionality you need, with robust tests to back it up!

---

# 🌐 Implementing GET with REST and Spring Boot
![](.README_images/a9ac75f3.png)

## What Are REST, CRUD, and HTTP? 

### 🌀REST: Representational State Transfer 
- A RESTful system manages the **state** of resources, known as **Resource Representations**.
- REST focuses on managing **values of resources** (data objects) through an API, which are often stored in databases..️

### 🛠️CRUD: Basic Operations 
- CRUD stands for **Create, Read, Update, and Delete**—the four basic actions for manipulating data.
- REST prescribes specific HTTP methods to perform these actions.

### 🌐HTTP in RESTful APIs 
- **HTTP** (Hypertext Transfer Protocol) facilitates the data flow in REST by allowing a **Request** to be sent to a **URI**, with a **Response** generated in return. 🔁

### 📨Components of HTTP Requests and Responses 

#### Request 
- **Method** (Verb like GET, POST)
- **URI** (Endpoint)
- **Body**

#### Response
- **Status Code** (indicates success or failure)
- **Body** (contains data if applicable)

## 🧰RESTful CRUD Operations with HTTP Methods 

When building REST APIs, each CRUD operation has a designated **HTTP method** and **status code** response, as shown below:

| 🛠️ Operation | 🌐 API Endpoint       | 📬 HTTP Method | 🔔 Response Status |
|--------------|------------------------|----------------|---------------------|
| **Create**   | `/cashcards`           | **POST**       | 201 (CREATED)       |
| **Read**     | `/cashcards/{id}`      | **GET**        | 200 (OK)            |
| **Update**   | `/cashcards/{id}`      | **PUT**        | 204 (NO CONTENT)    |
| **Delete**   | `/cashcards/{id}`      | **DELETE**     | 204 (NO CONTENT)    |

### Key Concepts 💡
- **Endpoint URI**: `/cashcards` for **Cash Card** objects.
- **READ, UPDATE, DELETE** operations require a **unique identifier** (`{id}`) to specify the target resource.
   - Example: `/cashcards/42` references the Cash Card with ID `42`.
- **CREATE** operations do **not** require an ID, as a unique identifier will be generated upon creation.

---

# 📥 The Request Body

When following **REST** conventions for **CREATE** or **UPDATE** operations, we need to submit data to the **API** through the request body. This **request body** contains the data necessary to either create or update a resource.

- For example, when creating a new **Cash Card**, the body might include an initial cash amount, and for **UPDATE**, the request body would specify the changes to the cash value.

# 💳 Cash Card Example

For a **READ** operation, the **URI (endpoint)** path format is `/cashcards/{id}`, where `{id}` is replaced by the actual **Cash Card ID** (without braces), and the **HTTP method** used is **GET**.

- **GET requests**: The body of a GET request remains empty.

```text
Request:
  Method: GET
  URL: http://cashcard.example.com/cashcards/123
  Body: (empty)
```

The response to a successful Read request has a body containing the JSON representation of the requested Resource, with a Response Status Code of 200 (OK). Therefore, the response to the above Read request would look like this:
```json
{
   "Request": {
      "Method": "GET",
      "URL": "http://cashcard.example.com/cashcards/123",
      "Body": ""
   }
}
```

---

# 🌱 REST in Spring Boot

# 📝 Spring Annotations and Component Scan

**Spring** creates and configures objects called **Beans**, often instantiated by Spring itself rather than with Java's `new` keyword. There are several ways to instruct Spring to create Beans.

- One way is to use **Spring Annotations**. This directs **Spring** to create an instance of the class during **Component Scan** at application startup.
- The Bean is then stored in **Spring's IoC container** and can be **injected** into any other code that requires it.

## 🌐 Spring Web Controllers

In **Spring Web**, incoming **Requests** are managed by **Controllers**:
```java
@RestController
class CashCardController {
}
```
- Marking a class with **@RestController** directs Spring to create an instance of the class during Spring’s Component Scan phase. This happens at application startup. 
- The Bean is stored in Spring’s IoC container. From here, the bean can be injected into any code that requests it.

Spring routes requests to the **Controller** based on incoming **API requests**, and routes each to the appropriate handler method.

![](.README_images/2cd4bf87.png)

## 🖥️ Creating a Read Request Handler Method
A Controller method can be designated a handler method, to be called when a request that the method knows how to handle (called a “matching request”) is received.
```java
private CashCard findById(Long requestedId) {
}
```
To handle **Read** operations:
1. Since REST specifies that **READ endpoints** should use **GET**, you need to tell **Spring** to route only **GET requests** to this method.
2. Use the **@GetMapping** annotation and specify the **URI path**.
```java
@GetMapping("/cashcards/{requestedId}")
private CashCard findById(Long requestedId) {
}
```

## 🔄 Mapping Request Parameters

**Spring** must know how to retrieve the **requestedId** parameter value. This is done using the **@PathVariable** annotation. Because the parameter name matches `{requestedId}` in the **@GetMapping** URI path, Spring can correctly **assign** the value to **requestedId**.
```java
@GetMapping("/cashcards/{requestedId}")
private CashCard findById(@PathVariable Long requestedId) {
}
```

## 📤 Returning the Response

REST specifies that the **Response** should contain:
- A **Cash Card** in the **body**
- A **Response Code** of `200 (OK)`

**Spring Web** provides the **ResponseEntity** class for this purpose, which includes utility methods to produce **Response Entities**. To create a **ResponseEntity** with a `200` status and a **CashCard** body, use:
The final implementation looks like this:

```java
@RestController
class CashCardController {
   @GetMapping("/cashcards/{requestedId}")
   private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
      CashCard cashCard = /* Here would be the code to retrieve the CashCard */;
      return ResponseEntity.ok(cashCard);
   }
}
```

---

# 📦 Repositories & Spring Data

If we to return real data, from a database, Spring Data works with Spring Boot to make database integration simple.

## 🏗️ Controller-Repository Architecture

The **Separation of Concerns** principle states that well-designed software should be modular, with each module having distinct and separate concerns from any other module.

If we have hard-coded response from the Controller, this setup violates the Separation of Concerns principle by mixing the concerns of a Controller, which is an abstraction of a web interface, with the concerns of reading and writing data to a data store, such as a database. 
- To avoid this we use a common software architecture pattern to enforce data management separation via the Repository pattern.

A common architectural framework that divides these layers, typically by function or value, such as business, data, and presentation layers, is called **Layered Architecture**. 
- The Repository and Controller are two layers in a Layered Architecture. 
  - The Controller is in a layer near the Client (as it receives and responds to web requests) while the Repository is in a layer near the data store (as it reads from and writes to the data store).
  - There may be intermediate layers as well, as dictated by business needs. We don't need any additional layers, at least not yet!

The Repository is the interface between the application and the database, and provides a common abstraction for any database, making it easier to switch to a different database when needed.

![](.README_images/cd1e29e4.png)

Spring Data provides a collection of robust data management tools, including implementations of the Repository pattern.

## 💾 Choosing a Database

In this project we use an **embedded, in-memory database**. 
- “Embedded” simply means that it’s a Java library, so it can be added to the project just like any other dependency.
- “In-memory” means that it stores data in memory only, as opposed to persisting data in permanent, durable storage. 
- At the same time, our in-memory database is largely compatible with production-grade relational database management systems (RDBMS) like MySQL, SQL Server, and many others. Specifically, it uses JDBC (the standard Java library for database connectivity) and SQL (the standard database query language).

![](.README_images/b555a3ab.png)

### 🔄 In Memory Embedded vs External Database

There are tradeoffs to using an in-memory database instead of a persistent database. 
- On one hand, in-memory allows you to develop without installing a separate RDBMS, and ensures that the database is in the same state (i.e., empty) on every test run.
- However, you do need a persistent database for the live "production" application. This leads to a **Dev-Prod Parity** mismatch: Your application might behave differently when running the in-memory database than when running in production.

This project uses H2, it is highly compatible with other relational databases, so dev-prod parity won’t be a big issue.

## ⚙️ Auto Configuration
All we need for full database functionality is to add two dependencies. Showing one of the most powerful features of Spring Boot: Auto Configuration.
- Without Spring Boot, we’d have to configure Spring Data to speak to H2. 
- However, because we’ve included the Spring Data dependency (and a specific data provider, H2), Spring Boot will automatically configure your application to communicate with H2.

## 🔄 Spring Data’s CrudRepository
This project uses a specific type of Repository: Spring Data’s CrudRepository.

Complete implementation of all CRUD operations by extending CrudRepository:
```java
interface CashCardRepository extends CrudRepository<CashCard, Long> {
}
```
With the above code, a caller can call any number of predefined CrudRepository methods, such as findById:
```java
cashCard = cashCardRepository.findById(99);
```
CrudRepository and everything it inherits from is an Interface with no actual code.
- Based on the specific Spring Data framework used (here Spring Data JDBC) Spring Data takes care of this implementation for us during the IoC container startup time.
- The Spring runtime will then expose the repository as yet another bean that you can reference wherever needed in your application.

There are trade-offs. 
- For example the CrudRepository generates SQL statements to read and write your data, which is useful for many cases, but sometimes you need to write your own custom SQL statements for specific use cases.

---

# 🚀 Implementing POST

1. **Who specifies the ID** - the client, or the server?
2. **In the API Request**, how do we represent the object to be created?
3. **Which HTTP method should we use in the Request?**
4. **What does the API send as a Response?**

- “Who specifies the ID?” 
  - In reality, this is up to the API creator! REST is not exactly a standard; it’s merely a way to use HTTP to perform data operations.

The simplest solution is to let the server create the ID. Databases are efficient at managing unique IDs. Other alternatives include:

- We could require the client to provide the ID. This might make sense if there were a pre-existing unique ID, but that’s not the case.
- We could allow the client to provide the ID optionally (and create it on the server if the client does not supply it). The [Yagni article](https://martinfowler.com/bliki/Yagni.html) explains why this isn't a good idea.

## 🔁 Idempotence and HTTP

An idempotent operation is defined as one which, if performed more than once, results in the **same** outcome. 
- In a REST API, an idempotent operation is one that even if it were to be performed several times, the resulting data on the server would be the same as if it had been performed only once.

For each method, the HTTP standard specifies whether it is idempotent or not. 
- GET, PUT, and DELETE are idempotent, whereas POST and PATCH are not.

The server will create IDs for every Create operation, so the Create operation in this API is **NOT idempotent**. Since the server will create a new ID (on every Create request), if you call Create twice - even with the same content - you’ll end up with two different objects with the same content, but with different IDs. 
- To summarize: **Every Create request will generate a new ID, thus no idempotency**.

![](.README_images/f24148f8.png)

REST permits POST as one of the proper methods to use for Create operations, the one used here.

## 📤📥 The POST Request and Response

### 📤The Request

The POST method allows a Body, so we'll use the Body to send a JSON representation of the object:

Request:
- Method: POST
- URI: /cashcards/
- Body:
```json
{
    "amount": 123.45
}
```
- The GET operation includes the ID of the Cash Card in the URI, but not in the request Body.

There no ID in the Request because we decided to allow the server to create the ID. Thus, the data contract for the Read operation is different from that of the Create operation.

### 📥The Response

On successful creation, the most accurate HTTP Response Status Code for REST APIs is: **201 CREATED**.
- In this case, a response code of 200 OK does not answer the question “Was there any change to the server data?”. By returning the 201 CREATED status, the API is specifically communicating that data was added to the data store on the server.

To recap, an HTTP Response contains two things: a Status Code, and a Body. But that’s not all. 
- A Response also contains Headers. Headers have a name and a value. The HTTP standard specifies that the Location header in a 201 CREATED response should contain the URI of the created resource. 
- This is handy because it allows the caller to easily fetch the new resource using the GET endpoint (the one we implemented prior).

Response:
- Status Code: 201 CREATED
- Header: Location=/cashcards/42

## 📡 Spring Web Convenience Methods

In this project, we use the `ResponseEntity.created(uriOfCashCard)` method to create a response. 
- This method requires you to specify the location, ensures the Location URI is well-formed (by using the URI class), adds the Location header, and sets the Status Code for you.
- And by doing so, this saves us from using more verbose methods. For example, the following two code snippets are equivalent (as long as uriOfCashCard is not null):
```java
return  ResponseEntity
        .created(uriOfCashCard)
        .build();
```
Versus:
```java
return ResponseEntity
        .status(HttpStatus.CREATED)
        .header(HttpHeaders.LOCATION, uriOfCashCard.toASCIIString())
        .build();
```

---

# 📜 Returning a List with GET

## 🔄 Requesting a List of Cash Cards

This **API** should be able to return multiple **Cash Cards** in response to a single **REST request**.

## 📑 The Data Contract

When you make an **API request** for several **Cash Cards**, you’d ideally make a single request that returns a list of **Cash Cards**. We need a new **data contract**.
- Instead of a single **Cash Card**, the new contract should specify that the response is a **JSON Array** of **Cash Card objects**.

Example response:
```json
[
    {
        "id": 1,
        "amount": 123.45
    },
    {
        "id": 2,
        "amount": 50.0
    }
]
```

---

**CrudRepository**, has a **findAll** method that can be used to easily fetch all the **Cash Cards** in the database.
```java
@GetMapping()
private ResponseEntity<Iterable<CashCard>> findAll() {
   return ResponseEntity.ok(cashCardRepository.findAll());
}
```

---

🤔 However, it turns out there’s a lot more to this operation than just returning all the **Cash Cards** in the database.
- **How do I return only the Cash Cards that the user owns?**.
- **What if there are hundreds (or thousands?!) of Cash Cards?** Should the API return an unlimited number of results or return them in “chunks”?
- **Should the Cash Cards be returned in a particular order (i.e., should they be sorted)?**

## 📄 Pagination and Sorting

We use a specialized version of the `CrudRepository`, called the `PagingAndSortingRepository`.

- **Paging** functionality.
  - Ideally, an API should not be able to produce a response with unlimited size, because this could overwhelm the client or server memory, not to mention taking quite a long time.
  - To ensure that an API response doesn’t include an astronomically large number of Cash Cards, we utilize Spring Data’s pagination functionality. **Pagination** in Spring (and many other frameworks) specifies the page length (e.g., 10 items) and the page index (starting with 0). For example:
      - If a user has 25 Cash Cards, and you elect to request the second page where each page has 10 items, you would request a page of size 10, and page index of 1.

For pagination to produce the correct page content, the items must be sorted in some specific order. Why?

- Imagine we have a bunch of Cash Cards with the following amounts:
    - $0.19
    - $1,000.00
    - $50.00
    - $20.00
    - $10.00

Here is an example using a page size of 3. Since there are 5 Cash Cards, we’d make two requests to return all of them. Page 1 (index 0) contains three items, and page 2 (index 1, the last page) contains 2 items.

- If we specify that the items should be **sorted by amount in descending order**, then this is how the data is paginated:

  **Page 1:**
    - $1,000.00
    - $50.00
    - $20.00

  **Page 2:**
    - $10.00
    - $0.19

### ❓ Regarding Unordered Queries

- Although Spring provides an “unordered” sorting strategy, let’s be explicit when we select which fields for sorting. Why?
    - Imagine you elect to use “unordered” pagination. In reality, the order is not random but predictable; it never changes on subsequent requests.
    - Let’s say you make a request, and Spring returns the following “unordered” results:

      **Page 1:**
        - $0.19
        - $1,000.00
        - $50.00

      **Page 2:**
        - $20.00
        - $10.00

Although they look random, every time you make the request, the cards will come back in exactly this order, so each item is returned on exactly one page.

- If we create a new Cash Card with an amount of $42.00. Which page will it be on? There’s no way to know other than making the request and seeing where the new Cash Card lands.

- It's more useful to opt for ordering by a specific field. **There are a few good reasons to do so:**
    - Minimize cognitive overhead: Other developers (not to mention users) will probably appreciate a thoughtful ordering.
    - Minimize future errors: What happens when a new version of Spring, Java, or the database suddenly causes the “random” order to change overnight?

### 🔍 Spring Data Pagination API

Spring Data provides the `PageRequest` and `Sort` classes for pagination.

```java
Page<CashCard> page2 = cashCardRepository.findAll(
    PageRequest.of(
        1,  // page index for the second page - indexing starts at 0
        10, // page size (the last page might have fewer items)
        Sort.by(new Sort.Order(Sort.Direction.DESC, "amount"))));
```

## 📄 The Request and Response

### **We use Spring Web to extract the data to feed the pagination functionality:**

- **Pagination**: Spring can parse out the page and size parameters if you pass a `Pageable` object to a `PagingAndSortingRepository` `find...()` method.

- **Sorting**: Spring can parse out the sort parameter, which consists of the field name and direction separated by a comma. ⚠️ **No space before or after the comma is allowed!** Again, this data is part of the `Pageable` object.

### 🌐 The URI

**Step-by-step for composing a URI for the new endpoint** (omitting the `https://domain` prefix).

1. Get the second page
```text
/cashcards?page=1
```
2. ...where a page has length of 3
```text
/cashcards?page=1&size=3
```
3. ...sorted by the current Cash Card balance
```text
/cashcards?page=1&size=3&sort=amount
```
4. ...in descending order (highest balance first)
```text
/cashcards?page=1&size=3&sort=amount,desc
```

### 🖥️ The Java Code

**Here’s the complete implementation of the Controller method for the new “get a page of Cash Cards” endpoint:**
```java
@GetMapping
private ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
   Page<CashCard> page = cashCardRepository.findAll(
           PageRequest.of(
                   pageable.getPageNumber(),
                   pageable.getPageSize(),
                   pageable.getSortOr(Sort.by(Sort.Direction.DESC, "amount"))));
   return ResponseEntity.ok(page.getContent());
}
```

### 🔍 Detail analysis:

- **First, let’s parse the needed values out of the query string:**
    - `Pageable` allows Spring to parse out the page number and size query string parameters.
      - **Note**: If the caller doesn’t provide the parameters, Spring provides defaults: `page=0`, `size=20`.
    - We use `getSortOr()` so that even if the caller doesn’t supply the sort parameter, there is a default. Unlike the page and size parameters, for which it makes sense for Spring to supply a default, it wouldn’t make sense for Spring to arbitrarily pick a sort field and direction.
    - We use the `page.getContent()` method to return the Cash Cards contained in the `Page` object to the caller.

So, what does the `Page` object contain besides the Cash Cards? Here's the `Page` object in JSON format. The Cash Cards are contained in the `content`. The rest of the fields contain information about how this Page is related to other Pages in the query.
```json
{
  "content": [
    {
      "id": 1,
      "amount": 10.0
    },
    {
      "id": 2,
      "amount": 0.19
    }
  ],
  "pageable": {
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 3,
    "pageNumber": 1,
    "pageSize": 3,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 5,
  "totalPages": 2,
  "first": false,
  "size": 3,
  "number": 1,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 2,
  "empty": false
}
```

Although we could return the entire Page object to the client, we don't need all that information. We'll define our data contract to only return the Cash Cards, not the rest of the Page data.
