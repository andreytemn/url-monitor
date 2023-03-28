# url-monitor

A REST API JSON Java microservice that allows to monitor particular http/https URLs.

The url-monitor service should allow you to:
- create, edit and delete monitored URLs and list them for a particular user (CRUD)
- monitor URLs in the background and log status codes and the returned payload
- list the last 10 monitored results for each particular monitored URL

## Getting started

```bash
git clone https://github.com/andreytemn/url-monitor.git
cd url-monitor
docker-compose up --build
```

## Connecting to uri-monitor

The service will be available on `localhost:8080/endpoints`. It is seeded with two default users:

    Applifting: info@applifting.cz, accessToken: 93f39e2f-80de-4033-99ee-249d92736a25

    Batman: batman@example.com, accessToken: dcb20f8a-5657-4f1b-9f7f-ce65739b359e

## Running tests

Before running tests, you need to start the testing database. You can to it with the following command:

```bash
docker-compose up mysql-test
```

Then you can run the tests with maven:
```bash
mvn test
```

## Examples of requests

Get the monitored endpoints for the user:
```
GET localhost:8080/endpoints

Headers
AccessToken: 93f39e2f-80de-4033-99ee-249d92736a25
```
Create a new endpoint
```

POST localhost:8080/endpoints

Headers
AccessToken: 93f39e2f-80de-4033-99ee-249d92736a25

Body
{
  "name": "Example Endpoint",
  "url": "http://example.com",
  "monitoredInterval": 1
}
```

Update the endpoint
```
PUT localhost:8080/endpoints/a1260f8a-5657-4f1b-9f7f-ce65739b7710

Headers
AccessToken: 93f39e2f-80de-4033-99ee-249d92736a25

Body
{
  "name": "Example Endpoint",
  "url": "http://example.com",
  "monitoredInterval": 1
}
```
Delete the endpoint
```
DELETE localhost:8080/endpoints/a1260f8a-5657-4f1b-9f7f-ce65739b7710

Headers
AccessToken: 93f39e2f-80de-4033-99ee-249d92736a25

Body
{
  "name": "Endpoint",
  "url": "http://google.com",
  "monitoredInterval": 2
}
```
Get monitoring results
```
DELETE localhost:8080/endpoints/a1260f8a-5657-4f1b-9f7f-ce65739b7710

Headers
AccessToken: 93f39e2f-80de-4033-99ee-249d92736a25

Body
{
  "name": "Endpoint",
  "url": "http://google.com",
  "monitoredInterval": 2
}
```

## Authors

[Andrei Temnikov](https://www.linkedin.com/in/andrei-temnikov-a39595103/)

## License

This project is licensed under the MIT License - see the LICENSE.md file for details.