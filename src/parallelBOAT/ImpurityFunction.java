package parallelBOAT;

@FunctionalInterface
public interface ImpurityFunction {
    double computeImpurity(Article[] dataset, Attribute attribute);
}