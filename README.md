[![Build Status](https://travis-ci.com/YaccConstructor/iguana.svg?branch=GLL-for-graph)](https://travis-ci.com/YaccConstructor/iguana)

### Iguana
---
This is the implementation of the GLL-based context-free path querying (CFPQ) algorithm. It is based on a high-performance GLL parsing algorithm implemented in Iguana project. Then, it was modificated to support graph-structured input data. Proposed solution solves both reachability and all paths problems for all pairs and multiple sources cases.

### How to get and build
---
The project is build with Maven.

```
git clone https://github.com/YaccConstructor/iguana
cd iguana
mvn compile
```
### Usage
---
Run the following command with arguments.

```
mvn exec:java -Dexec.mainClass="benchmark.Neo4jBenchmark" -Dexec.args="arg1 arg2 arg3 arg4 arg5 agr6 agr7 arg8"
```
Argument | Description
:--- | :---
arg1 | Relationship type of the edges in graph to use (st/bt/ad)
arg2 | The number of vertices in graph
arg3 | The number of warm up iterations
arg4 | The total number of iterations
arg5 | Path to dataset
arg6 | Path to grammar
arg7 | The name of a file with result
arg8 | Grammar name (g1/g2/geo/anderson)
