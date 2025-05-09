FROM ghcr.io/navikt/baseimages/temurin:11
COPY build/libs/*.jar app.jar
