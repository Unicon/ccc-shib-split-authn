FROM unicon/shibboleth-idp:3.1.2-beta

MAINTAINER Unicon, Inc.

COPY shibboleth-idp/ /opt/shibboleth-idp/
COPY run-jetty.sh /usr/local/bin/

RUN chmod 750 /usr/local/bin/run-jetty.sh