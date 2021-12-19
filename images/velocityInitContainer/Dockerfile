FROM ghcr.io/katzen48/chrotoscloud-v2:simple-init-container

COPY velocity-all.jar .
COPY entrypoint.sh /usr/local/bin/
RUN chmod u+x /usr/local/bin/entrypoint.sh

ENV SERVER_SOFTWARE velocity
ENV SERVER_SOFTWARE_VERSION 3.1.1

ENTRYPOINT ["entrypoint.sh"]