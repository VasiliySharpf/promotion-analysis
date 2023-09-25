FROM openjdk:17-ea-3-jdk-slim

RUN apt-get update

ENV JAVA_XMX 256m
ENV USER promotion_analysis

ADD ./target/*.jar /app/promotion-analysis-service.jar
ADD ./init.sh /app/init.sh
RUN groupadd -g 1000 ${USER} && useradd -u 1000 -g 1000 -d /app ${USER}
RUN chmod 755 /app/init.sh && chown -R ${USER} /app

USER $USER
ENTRYPOINT ["/app/init.sh"]