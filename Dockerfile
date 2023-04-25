FROM clojure:openjdk-17 AS build

WORKDIR /
COPY . /

RUN clj -Sforce -T:build all

FROM azul/zulu-openjdk-alpine:17

COPY --from=build /target/luciano-standalone.jar /luciano/luciano-standalone.jar

EXPOSE $PORT

ENTRYPOINT exec java $JAVA_OPTS -Dlogback.configurationFile=/env/prod/resources/logback.xml -jar /luciano/luciano-standalone.jar
