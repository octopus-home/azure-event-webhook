FROM docker.io/library/openjdk:17
COPY ./target/event-webhook-1.0.jar event-webhook-1.0.jar
CMD ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005","-jar","event-webhook-1.0.jar"]