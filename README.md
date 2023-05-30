# Project-6-VRPTW

Polytech Lyon Student - IT - Julian DEGUT

## Introduction

The Vehicle Routing Problem with Time Windows (VRPTW) is a well-known combinatorial optimization problem that involves routing a fleet of vehicles to serve a set of customers, while respecting time window constraints and minimizing the total distance travelled. This project aims to implement various meta-heuristics to solve this problem.

## Implemented Algorithms

The following algorithms have been implemented to solve the VRPTW:

### Randomizer

The `Randomizer` algorithm applies a randomly chosen neighborhood operator from a large list to the current solution and apply it to the current solution if its cost is lesser, a specified number of times. The algorithm can quickly explore a large portion of the search space, but it may not always converge to the optimal solution.

### Hill Climbing

The `HillClimbing` algorithm is a simple method that starts with an initial solution and repeatedly moves to the best neighboring solution until no better solution can be found. This process corresponds to climbing a hill until a local optimum is reached. The algorithm may get stuck in a local optimum, so it is not guaranteed to find the global optimum.

### Simulated Annealing

The `SimulatedAnnealing` algorithm is based on the physical annealing process. It starts with a high temperature that allows the algorithm to accept worse solutions, and gradually reduces the temperature, which decreases the acceptance probability of worse solutions. This allows the algorithm to explore the search space and potentially escape from local optima. The algorithm can converge to the global optimum if given enough time and proper parameter tuning.

### Tabu Search

The `Tabu` algorithm is a local search method that uses a short-term memory to escape from local optima. It maintains a list of recently visited solutions and prohibits revisiting them for a certain number of iterations. The algorithm can effectively explore the search space and converge to the global optimum, but it requires careful parameter tuning to balance the exploration and exploitation.

## Neighborhood Operators

In order to explore the search space efficiently, various neighborhood operators are applied to the current solution to produce new neighboring solutions. The following operators are implemented:

- `InterRelocate` selects one customer and relocates him to a new position in another route.

- `InterExchange` selects two routes and exchanges a customer on both routes between them.

- `CrossExchange` selects two routes and exchanges a sequence of customers on both routes between them.

- `IntraRelocate` selects a customer and relocates it to a new position within the same route.

- `IntraExchange` selects two customers within the same route and exchanges their positions.

- `Reverse` selects a route and reverses the customers' order.

- `TwoOpt` selects two customers within a route and reverses the subsequence between them, then reconnects the two endpoints to correctly update the route.

Each of these operators generates a neighbor that is close to the current solution in terms of distance traveled.

## Packages & classes

The program is divided into several packages:

- `algorithms` contains classes related to the different algorithms and methods required.
- `data` contains classes for data retrieval from a file and data generation.
- `entity` contains classes for the entities modeled in the VRPTW.
- `graph` contains classes related to graph creation.


### Package algorithms

- `operators` package : contains classes for each neighborhood operator (`Operator` is the abstract superclass for each operator).
- `VRPTW` contains methods useful to algorithms and solver execution.
- `Randomizer` contains the algorithm for randomly applying a defined number of neighbors.
- `HillClimber` contains the descent algorithm.
- `SimulatedAnnealing` contains the simulated annealing algorithm.
- `Tabu` contains the tabu search algorithm.

### Package data

- `Data` contains methods for retrieving data from a .txt file.
- `DataGenerated` contains methods for generating random data based on a desired number of clients.

### Package entity

- `Client` contains all the information of a client and inherits from the `Vertex` class.
- `Depot` contains all the information of a depot and inherits from the `Vertex` class.
- `Neighbor` contains useful information for a created neighbor.
- `Vehicle` contains all the information of a vehicle.

### Package graph

- `Edge` represents an edge of the graph.
- `Route` represents a list of edges (and therefore a path).
- `Solution` represents the total graph and therefore a set of routes.
- `Vertex` represents a vertex of the graph.

Finally, the `src` package contains the `Main` class, which allows the solver to be launched.

## External libraries

The program uses some external libraries for graph display called "GraphStream" & "JavaFX".
