FROM ghcr.io/katzen48/chrotoscloud-v2:simple-init-container

RUN apt-get update && apt-get install -y unzip

COPY entrypoint.sh /usr/local/bin/
RUN chmod u+x /usr/local/bin/entrypoint.sh

ENV SERVER_SOFTWARE paper
ENV SERVER_SOFTWARE_VERSION 1.18.1

ENTRYPOINT ["entrypoint.sh"]