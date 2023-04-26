FROM clojure:openjdk-17 AS deps

# Copy only the deps.edn file into the image
WORKDIR /app
COPY deps.edn .

# Install the dependencies and save them to the cache
RUN clojure -Spath -Srepro >/dev/null
RUN --mount=type=cache,target=/root/.m2/repository \
    clojure -Spath -Srepro >/dev/null

# Use the latest version of the Clojure image as of the time this file was written
FROM clojure:openjdk-17 AS build

# Copy the project files and the cached dependencies into the image
WORKDIR /app
COPY --from=deps /app /app

COPY . .
RUN clojure -T:build all

# Use the Azul Zulu OpenJDK image as the final image
FROM azul/zulu-openjdk-alpine:17

# Copy the built jar file into the final image
COPY --from=build /target/luciano-standalone.jar /luciano/luciano-standalone.jar

EXPOSE $PORT

# Start the application
ENTRYPOINT exec java $JAVA_OPTS -Dlogback.configurationFile=/env/prod/resources/logback.xml -jar /luciano/luciano-standalone.jar
