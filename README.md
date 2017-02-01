# ccc-shib-split-authn
This project demonstrates how to modify the Shibboleth IdP v3 to support unique users coming from 2 different authn/attribute sources. The main strategy of this implementation is to only modify resources in the "public" areas of the IdP.

While this has been configured to support two LDAP servers, it should be trivial to switch authentication sources to another forms-based authentication type, such as JAAS/JDBC.

## Project layout
IdP configuration files that are either added or changed to utilize the split authn support are located in `src/main/IDP_HOME`. The directory structure generally mirrors that of an installed IdP.

This project includes a few Java source files. Those can be found in `src/main/java` and in `src/test/java`.

This project utilizes Docker for testing (see `Testing` below). The test env configuration is in `src/test/docker-compose`.

## Developer Notes
Notes on the types of changes made to each resource is available in the [project wiki](https://github.com/Unicon/ccc-shib-split-authn/wiki).

## Releasing
`./gradlew assembleDist` will build and package the distributable. New releases should be put in the release section of the [project's Github repo](https://github.com/Unicon/ccc-shib-split-authn/releases/).

## Installation
Download the latest stable release from <https://github.com/Unicon/ccc-shib-split-authn/releases/>.

After unpacking the distribution, review the layout of the directory structure. It should line up with your IdP installation. A breakdown of the changes made to each resource is available in the [project wiki](https://github.com/Unicon/ccc-shib-split-authn/wiki).

Many of the files have matching equivalents of the default installation, so it may not be desirable to just drop them into your overlay directly. It might be more prudent to use a diff/merge utility to identify changes between the distribution's files and yours.

### Notable configuration Settings

* In the `idp.properties` file, `idp.additionalProperties` include references to other property files, specifically ldap properties for separate LDAP instances. Also `idp.authn.flows` is set to `Splitauthn`. This enables the flow contained in this distribution.
* The `studentLdap.properties` and `employeeLdap.properties` are mirror images of the `ldap.properties` file. All of the properties work as expected except the search filter. `(uid=$requestContext.principalName.replace("@student", ""))` (or `(uid=$requestContext.principalName.replace("@student", ""))`) have an additonal call that strips the realm identifier used during attribute lookup.
* The `attribute-resolver.xml` is generally the same, but one should note that each ldap-based attribute definition has two resolver dependencies, one to each ldap. An activation condition on each DataConnector determines which source is used to populate the attribute definition.
* The `general-authn.xml` file has an added flow definition that defines the flow contained in this distribution.
* In the `global.xml`, the `custom.AttributeResolverResources` has an extra definition that defines "custom" beans used by the Split-authn flow.

The label of the "role/realm" can be changed by adding a property `idp.login.realm=<label>` to the `messages/messages.properties` file.

## Testing
This project utilizes a pure Shibboleth IdP v3 Docker image along with two 389-ds LDAP images for testing. There is also an Shibboleth SP-based image to simulate the whole SAML process end-to-end.

A `hosts` file entry should be setup to point `idp.ccc.local` to the IP address of the docker host.

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

## Release notes

- 1.1.0 
  - Updated for IdP version 3.3.0. 
  - `extra-beans.xml` removed and its contents placed in the standard `global.xml`
  - With the change to use `global.xml`, `services.xml` mods are no longer needed and the file has been removed from this solution.
  - Better documented in each file where its origin in the baseline IdP codebase
- 1.0.2
  - Updated to IdP version 3.2.1
  - Fixing location of jar in the build
- 1.0.1 - Fixing a problem in the `login.vm`
- 1.0.0 - Initial release