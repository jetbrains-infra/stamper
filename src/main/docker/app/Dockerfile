FROM nashikhmin/stamper-env:0.1
ADD test-env-runner-0.1.jar app.jar
COPY scripts /data/scripts

VOLUME ["/data"]
RUN sh -c 'touch /app.jar'

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -Dspring.profiles.active=container -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
