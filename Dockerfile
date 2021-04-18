FROM maven:3.6.3-openjdk-15
RUN mkdir /app
WORKDIR /app
COPY . /app
RUN mvn clean install -DskipTests
#CMD "mvn" "exec:java"