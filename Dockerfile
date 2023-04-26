FROM azul/zulu-openjdk-alpine:17

COPY ./target/luciano-standalone.jar /luciano/luciano-standalone.jar

EXPOSE $PORT

ENTRYPOINT exec java $JAVA_OPTS -Dlogback.configurationFile=/env/prod/resources/logback.xml -jar /luciano/luciano-standalone.jar
