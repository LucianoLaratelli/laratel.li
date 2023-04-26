FROM eclipse-temurin:17-alpine as temurin-deps

COPY ./target/luciano-standalone.jar /app/blog.jar

RUN unzip /app/blog.jar -d temp &&  \
    jdeps  \
      --print-module-deps \
      --ignore-missing-deps \
      --recursive \
      --multi-release 17 \
      --class-path="./temp/BOOT-INF/lib/*" \
      --module-path="./temp/BOOT-INF/lib/*" \
      /app/blog.jar > /modules.txt

FROM eclipse-temurin:17-alpine as temurin-jdk

COPY --from=temurin-deps /modules.txt /modules.txt

RUN apk add --no-cache binutils && \
    jlink \
     --verbose \
     --add-modules "$(cat /modules.txt),jdk.crypto.ec,jdk.crypto.cryptoki" \
     --strip-debug \
     --no-man-pages \
     --no-header-files \
     --compress=2 \
     --output /jre

FROM alpine:latest
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=temurin-jdk /jre $JAVA_HOME

EXPOSE $PORT

COPY ./target/luciano-standalone.jar /app/blog.jar

WORKDIR /app

ENTRYPOINT exec java $JAVA_OPTS -Dlogback.configurationFile=/env/prod/resources/logback.xml -jar blog.jar
