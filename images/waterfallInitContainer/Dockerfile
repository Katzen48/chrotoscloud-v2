FROM ghcr.io/katzen48/chrotoscloud-v2:simple-init-container

COPY bungeecord-all.jar .
COPY entrypoint.sh /usr/local/bin/
RUN chmod u+x /usr/local/bin/entrypoint.sh

ENV SERVER_SOFTWARE waterfall
ENV SERVER_SOFTWARE_VERSION 1.18

ENTRYPOINT ["entrypoint.sh"]