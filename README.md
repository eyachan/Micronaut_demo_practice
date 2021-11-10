## General ideal with links to micronaut
## Micronaut 2.5.7 Documentation

- [User Guide](https://docs.micronaut.io/2.5.7/guide/index.html)
- [API Reference](https://docs.micronaut.io/2.5.7/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/2.5.7/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---
## requirement
- docker installed
- maven
- openJDK 11

## example Json to send as request body
```json
{
  "idBook":"12",
  "bookTitle":"micronaut"
}
```

## Run dockerfile
go under the target folder and you will find dockerfile and the jar called demo_from_scratch-0.1.jar and then run the following command
```docker 
docker build -t demo_from_scratch-0.1.jar .
```