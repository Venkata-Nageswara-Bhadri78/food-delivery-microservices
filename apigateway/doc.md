# Context Information

Here are the responsibilities of the api gateway.

It works on 8080 port and it routes the requests to all other services:

- 8080: Gateway(entry point)
- 8081: auth-service
- 8082: restaurant-service
- 8083: customer-service
- 8084: agent-service
- 8085: admin service

Note: 

- This api-gateway should allow few public end-points without any authentication(jwt token). 
- Should allow few enpoints to must require jwt token. 
- Should validate the request by UserType enum at the api gateway itself and then need to send the request to that specific service.

### End Point Types:

#### Public End-points:

- [http://localhost:8080/api/auth/**](http://localhost:8081/api/auth/)
- [http://localhost:8080/api/restaurant/**](http://localhost:8081/api/auth/)

#### Protected End-Points:

- [http://localhost:8080/api/customer/**](http://localhost:8081/api/auth/)

If user is making request to auth service then the link will be like [http://localhost:8080/api/auth/](http://localhost:8081/api/auth/)** and then api-gateway should filter /api/auth and need to send that remaining thing to auth-service(probable /register or /login).

In the same way if user is making request to restaurant-service then [http://localhost:8080/api/restaurant/](http://localhost:8082/api/restaurant/)** and this is publicly accessable without any authentication with auth-service. So the api gateway should filter this and send the request to restaurant-service without any authentication or validation since it is publicly accessable.

In the same way for customer-service, agent-service, admin-service then request to api gateway must require a jwt token. Then we validate both authentication and authorization inside the api-way itself and send the probable request to that particular service.
Example:
For customer service: POST [http://localhost:8081/api/customer/cart](http://localhost:8081/api/customer/cart)
Request: Header with Bearer Token, Request Body
Api Gateway: Validates JWT, check whether the UserType is CUSTOMER or not. If fails then returns the response back. Otherwise filter the request as /cart to customer-service and send the request to customer-service and get the response from customer-service and send that as it is to frontend.

I am completely ignoring eureka server and discovery client because implementing everything at a time making consfusion for me. For now let's hardcode the ports and urls and later update them with eureka.

# Main Task

You have to make the below changes in the api-gateway service

1. Api Gateway should route the public end-points to respective service and return the response back to the user.
2. If there are any protected endpoints then:
  1. The request for api-gateway already consists of jwt token. The api gateway should check whether the jwt is valid or not. if invalid then send the response exception back to user.
  2. If jwt token is valid then route that jwt and request to respective end-points.







Here are the responsibilities of the api gateway.

It works on 8080 port and it routes the requests to all other services:

- 8080: Gateway(entry point)
- 8081: auth-service
- 8082: restaurant-service
- 8083: customer-service
- 8084: agent-service
- 8085: admin service

Let's go with responsibilities and access for all the services

1. 8080: can be accessed by anyone
2. 8081: This have /register, /login: this can be accessed by CUSTOMER, AGENT, ADMIN
3. 8082: restaurant-service: Here admin can be accessed for post methods and all other end-points are publicly accessable

4. 8083: customer-service: auth-service can be accessed for POST /api/customer/customer end-point. Then all other endpoints are accessable by customer and admin

5. 8084: agent-service: auth-service can be accessed for POST /api/agent/agent end-point. Then all other endpoints are accessable by agent and admin.

6. 8085: admin-service: 
only POST /api/admin/admin can be accessed by auth-service. 
all other endpoints are accessable by admin.


Here are the access for the api-gateway:
auth-service: can be accessed by CUSTOMER, AGENT, ADMIN

