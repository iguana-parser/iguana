![example workflow](https://github.com/YaccConstructor/iguana/actions/workflows/main.yml/badge.svg)

# Iguana

This is the implementation of the GLL-based context-free path querying (CFPQ) algorithm. It is based on a high-performance GLL parsing algorithm implemented in Iguana project. Then, it was modificated to support graph-structured input data. Proposed solution solves both reachability and all paths problems for all pairs and multiple sources cases.

## Performance

The proposed solution has been evaluated on several real-world graphs for both the all pairs and the multiple sources scenarios. The evaluation shows that the proposed solution is more than 25 times faster than the previous solution for Neo4j and is comparable, in some cases, with the linear algebra based solution for RedisGraph.

**Machine configuration**: PC with Ubuntu 20.04, Intel Core i7-6700 3.40GHz CPU, DDR4 64Gb RAM, GeForce GTX 1070 GPU with 8Gb VRAM.

**Enviroment configuration**: 
* Java HotSpot(TM) 64-Bit server virtual machine (build 15.0.2+7-27, mixed mode, sharing).
* JVM heap configuration: 55Gb both xms and xmx.
* Neo4j 4.0.3 is used. Almost all default configurations are default except: total off-heap transaction memory (tx_state_max_off_heap_memory parameter) is 24Gb, and pagecache_warmup_enabled is set to true.


### Graphs

The graph data is selected from [CFPQ_Data dataset](https://github.com/JetBrains-Research/CFPQ_Data). There are two types of graph:
* Graphs related to RDF analysis problems
* Graphs related to static code analysis problems

A detailed description of the graphs is listed bellow.

**RDF analysis** 

| Graph name   |  \|*V*\|   |  \|*E*\|   | #subClassOf |   #type   | #broaderTransitive |
|:-------------|:----------:|:----------:|:-----------:|:---------:|:------------------:|
| Go hierarchy |   45 007   |  490 109   |   490 109   |     0     |         0          |
| Enzyme       |   48 815   |   86 543   |    8 163    |  14 989   |       8 156        | 
| Eclass_514en |  239 111   |  360 248   |   90 962    |  72 517   |         0          | 
| Geospecies   |  450 609   | 2 201 532  |      0      |  89 065   |       20 867       | 
| Go           |  582 929   | 1 437 437  |   94 514    |  226 481  |         0          | 
| Taxonomy     | 5 728 398  | 14 922 125 |  2 112 637  | 2 508 635 |         0          |

**Static code analysis**

| Graph name   |  \|*V*\|   |  \|*E*\|   |    #a     |    #d     |
|:-------------|:----------:|:----------:|:---------:|:---------:|
| Init         | 2 446 224  | 2 112 809  |  481 994  | 1 630 815 |
| Drivers      | 4 273 803  | 3 707 769  |  858 568  | 2 849 201 |
| Kernel       | 11 254 434 | 9 484 213  | 1 981 258 | 7 502 955 |

### Grammars

All queries used in evaluation are variants of same-generation query. The inverse of an ```x``` relation and the respective edge is denoted as ```x_r```.

<br/>

Grammars used for RDF graphs:

**G<sub>1</sub>**
```
S -> subClassOf_r S subClassOf | subClassOf_r subClassOf 
     | type_r S type | type_r type
```

**G<sub>2</sub>**
```
S -> subClassOf_r S subClassOf | subClassOf
```

  **Geo**
```
S -> broaderTransitive S broaderTransitive_r
     | broaderTransitive broaderTransitive_r 
```

<br/>

Grammar used for static code analysis graphs:
  
**PointsTo**
  ```
  M -> d_r V d
  V -> (M? a_r)* M? (a M?)* 
  ```
  
### Results
  
The results of the **all pairs reachability** queries evaluation are presented in the table below.

| Graph name   |  G<sub>1</sub>  |         | G<sub>2</sub>    |           | Geo        |             |          PointsTo        |            |
|:-------------|:---------------:|:-------:|:---------------:|:---------:|:----------:|:-----------:|:------------------------:|:----------:|
|              |   time (sec)    | #answer |   time (sec)    |  #answer  | time (sec) |   #answer   |        time (sec)        |  #answer   |
| Go hierarchy |     564,72      | 588 976 |     2813,50     |  738 937  |     –      |      –      |            –             |     –      |
| Enzyme       |      0,19       |   396   |      0,17       |   8163    |    8,54    | 14 267 542  |            –             |     –      |
| Eclass_514en |     295,06      | 90 994  |     279,80      |  96 163   |     –      |      –      |            –             |     –      |
| Geospecies   |      2,64       |   85    |      2,00       |     0     |   256,86   | 226 669 749 |            –             |     –      |
| Go           |      11,18      | 640 316 |      10,00      |  659 501  |     –      |      –      |            –             |     –      |
| Taxonomy     |      43,72      | 151 706 |      29,58      | 2 112 637 |     –      |      –      |            –             |     –      |
| Init         |        –        |    –    |        –        |     –     |     –      |      –      |          113,35          | 3 783 769  |
| Drivers      |        –        |    –    |        –        |     –     |     –      |      –      |          736,81          | 18 825 025 |
| Kernel       |        –        |    –    |        –        |     –     |     –      |      –      |          850,46          | 16 747 731 |


<br/>

The evaluation results for **multiple source** CFPQ for **Geospecies** graph and **Geo** grammar in reachability and all path scenarious are listed bellow.

![time](https://github.com/YaccConstructor/iguana/blob/GLL-for-graph/docs/pictures/geospecies_chunks.svg?raw=true&sanitize=true)

## Download and build

The project is build with Maven.

```
git clone https://github.com/YaccConstructor/iguana
cd iguana
mvn compile
```
## Usage

To replicate experiments:

* make the directory to save results
```
mkdir results
```
* run the following command with arguments
```
mvn exec:java -Dexec.mainClass="benchmark.Neo4jBenchmark" -Dexec.args="arg1 arg2 arg3 arg4 arg5 agr6 agr7 arg8"
```
Argument | Description
:--- | :---
arg1 | Relationship types of the edges in graph to use. Currently this argument can take one of three possible values: <br> - **st** is used for G<sub>1</sub> and G<sub>2</sub> grammars  <br> - **bt** is used for Geo grammar <br> - **ad** is used for PointsTo grammar
arg2 | The number of vertices in graph
arg3 | The number of warm up iterations
arg4 | The total number of iterations
arg5 | Path to dataset
arg6 | Path to grammar
arg7 | The name of a file with result
arg8 | Grammar name (g1/g2/geo/pointsTo)

### Example
To run experiments on Geospecies graph on Geo grammar use the following command:
```
mvn exec:java -Dexec.mainClass="benchmark.Neo4jBenchmark" -Dexec.args="bt 450609 2 5 home/user/data/graphs/geospecies/ test/resources/grammars/graph/g1/grammar.json geospecies g1"
```

## License

This project is licensed under OpenBSD License. License text can be found in the 
[license file](https://github.com/YaccConstructor/iguana/blob/GLL-for-graph/LICENSE.md).
