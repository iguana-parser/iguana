FROM maven:3.6.3-openjdk-15
RUN mkdir /app
WORKDIR /app
COPY . /app
RUN mvn clean install -DskipTests
<<<<<<< HEAD
RUN mvn exec:java -Dexec.mainClass="benchmark.Neo4jBenchmark" -Dexec.args="bt 450609 2 5 /Users/vladapogozhelskaya/Downloads/neo4j-enterprise-4.0.12 test/resources/grammars/graph/Test1/grammar.json geospecies" -Dexec.cleanupDaemonThreads=false
=======
#CMD "mvn" "exec:java"
>>>>>>> 2be07003ca2d9005d3a6b427446088648678d947
