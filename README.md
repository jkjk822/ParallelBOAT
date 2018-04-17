# parallelBOAT
Parallel implementation of the decision tree building BOAT algorithm.


### TODO:
Willie:
- Debug decision tree alg
- Test decision tree alg
- Convert `data` to `ArrayList<Article>`
- Rewrite `getMajorityClass`
- Implement custom `Attribute, Double` pair?

Johnny:
- `Double.NaN` as constant
- Implement BOAT failure recovery
- Restructure
    - `BootStrap` into a tree class
    - `DecisionTree` into class
    - Relation between two (bootstrap inherits?)
- Parallelize
