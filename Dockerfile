FROM ghcr.io/navikt/baseimages/temurin:11
COPY build/libs/app*.jar app.jar
