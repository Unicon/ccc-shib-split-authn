# ccc-shib-split-authn
This project demonstrates how to modify the Shibboleth IdP v3 to support unique users coming from 2 different authn/attribute sources. The main strategy of this implementation is to only modify resources in the "public" areas of the IdP.

## Project layout
IdP configuration files that are either added or changed to utilize the split authn support are located in `src/main/IDP_HOME`. The directory structure generally mirrors that of an installed IdP.

This project includes a few Java source files. Those can be found in `src/main/java` and in `src/test/java`.

This project utilizes Docker for testing (see `Testing` below). The test env configuration is in `src/test/docker-compose`.

## Developer Notes
Notes on the types of changes made to each resource is available in the [project wiki](https://github.com/Unicon/ccc-shib-split-authn/wiki).

## Installation


## Testing
This project utilizes a pure Shibboleth IdP v3 Docker image along with two 389-ds LDAP images for testing. There is also an Shibboleth SP-based image to simulate the whole SAML process end-to-end.

A `hosts` file entry should be setup to point `idp.ccc.local` to the IP address of the docker host (`docker-machine ip default`).

Assuming that Docker has been setup properly, and your Docker env variables are correct, as well, the containers can be started with:

```
./gradlew runContainers
```

Browsing to https://idp.ccc.local/ will bring you to a landing page. Clicking the link will initiate the SP/IdP flowx. There are test accounts `staff1/password` and `student1/password`. Each lives in a separate LDAP server.

The containers can be reset with:

```
./gradlew clean
```

To add in viewing the logs of Jetty, the IdP, the LDAP servers, and the SP, one can run:

```
./runwithLogs.sh
```

Ctrl+C will stop the logging process, but will leave the containers running. Use './gradlew clean' to terminate and remove those instances.