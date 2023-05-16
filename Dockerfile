# based on the practicalli dockerfile
# https://github.com/practicalli/clojure-service-template/blob/main/Dockerfile

FROM clojure:temurin-20-alpine AS builder

RUN mkdir -p /build

WORKDIR /build

COPY deps.edn Makefile /build/
RUN clojure -P

COPY ./ /build

RUN clojure -T:build uberjar

# ------------------------
# Setup Run-time Container

FROM eclipse-temurin:20-alpine

# Add operating system packages
# - dumb-init to ensure SIGTERM sent to java process running Clojure service
RUN apk add --no-cache \
    dumb-init~=1.2.5

RUN addgroup -S luciano && adduser -S luciano -G luciano

RUN mkdir -p /service && chown -R luciano. /service

USER luciano

# Copy service archive file from Builder image
WORKDIR /service
COPY --from=builder /build/luciano-service.jar /service/luciano-service.jar

EXPOSE 3000

ENV JDK_JAVA_OPTIONS "-XshowSettings:system -XX:+UseContainerSupport -XX:MaxRAMPercentage=90"

ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["java", "-jar", "/service/luciano-service.jar"]
