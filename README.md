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

## Authors

[Andrei Temnikov](https://www.linkedin.com/in/andrei-temnikov-a39595103/)

## License

This project is licensed under the MIT License - see the LICENSE.md file for details.