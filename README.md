# parallelBOAT
Parallel implementation of the decision tree building BOAT algorithm.

## Overview of Files

### General Package

- Article
    - Class to hold all data for an article
- Attribute
    - Enum describing each of the 61 attributes of an article
- CompareByAttribute
    - Custom class to compare articles by a given attribute
- Driver
    - Main method to run and test program
- ImpurityFunction
    - Interface to allow future extension of more measures than just Gini index
- Popularity
    - Enum describing the popularity classes

### Tree Package

- BootStrapTreeBuilder
    - Bootstrap tree algorithm class
- ConfidenceNode
    - Node used for combining trees within BOAT algorithm
- DecisionTreeBuilder
    - Decision tree class, holds classification and original build method
- InternalNode
    - Splitting node for DT
- LeafNode
    - Classification node for DT
- Node
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
