FROM openjdk:8u131-jre-alpine
VOLUME /tmp
ADD test-env-runner-0.1.jar app.jar
ADD ./data /data/
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]