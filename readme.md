The code in this repository was created in 2021.

# Description
LcgUtil library generates random numbers between 0 and m-1 using
<a href="https://en.wikipedia.org/wiki/Linear_congruential_generator">
Linear Congruential Generator(LCG)
</a>, where the generated numbers follow a specific sequence which will start to repeat the values once exhausted(for example: 5, 19, 102, 5, 19, 102, ....).

Generating numbers using LCG helps to avoid collisions and the need to regenerate numbers, because once a 
number is repeated, all the numbers after it will be repeated.

Each generator is defined by constants which are responsible for the quality of the generator.
A good generator is a one that can consume the whole period (all m values in the range [0,m-1]).

The library provides a static factory that can create full period generators using Hull–Dobell Theorem.

The math involved in this library includes:
1. Modular arithmetic.
2. Geometric series calculated using exponentiation by squaring.
3. Prime number factorization.
