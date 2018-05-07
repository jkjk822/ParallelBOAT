# parallelBOAT
Parallel implementation of the decision tree building BOAT algorithm.

## Running Instructions

This project was developed in Intellij, so we recommend building and running it through there.

Command Line Run:

$ cd <path to project directory>/ParallelBOAT
$ javac src/parallelBOAT/*.java src/parallelBOAT/tree/java
$ java -cp src/ parallelBOAT.Driver

## Overview of Files

### General Package

- Article.java
    - Class to hold all data for an article
- Attribute.java
    - Enum describing each of the 61 attributes of an article
- CompareByAttribute.java
    - Custom class to compare articles by a given attribute
- Driver.java
    - Main method to run and test program
- ImpurityFunction.java
    - Interface to allow future extension of more measures than just Gini index
- Popularity.java
    - Enum describing the popularity classes

### Tree Package

- BootStrapTreeBuilder.java
    - Bootstrap tree algorithm class
- ConfidenceNode.java
    - Node used for combining trees within BOAT algorithm
- DecisionTreeBuilder.java
    - Decision tree class, holds classification and original build method
- InternalNode.java
    - Splitting node for DT
- LeafNode.java
    - Classification node for DT
- Node.java
    - Abstract node class (all others extend this)

### TODO:
Willie:
- Convert `data` to `ArrayList<Article>`
- Rewrite `getMajorityClass`

Johnny:
- `Double.NaN` as constant
- Implement BOAT failure recovery
- Restructure
    - `BootStrap` into a tree class
    - `DecisionTree` into class
    - Relation between two (bootstrap inherits?)
- Parallelize
