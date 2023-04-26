FROM clojure:openjdk-17 AS deps

WORKDIR /app
COPY deps.edn .

RUN clojure -Spath -Srepro >/dev/null

FROM clojure:openjdk-17 AS build

WORKDIR /app
COPY --from=deps /root/.m2 /root/.m2

COPY . .
RUN clojure -T:build all

FROM azul/zulu-openjdk-alpine:17

COPY --from=build /app/target/luciano-standalone.jar /luciano/luciano-standalone.jar

EXPOSE $PORT

ENTRYPOINT exec java $JAVA_OPTS -Dlogback.configurationFile=/env/prod/resources/logback.xml -jar /luciano/luciano-standalone.jar
