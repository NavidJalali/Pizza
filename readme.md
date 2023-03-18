## Problem Statement

Tieu owns a pizza restaurant and he manages it in his own way. While in a normal restaurant,
a customer is served by following the “first-come-first-served” rule, Tieu simply minimizes the
average waiting time of his customers. He gets to decide who is served first, regardless of how
soon or late a person comes in.
Different kinds of pizzas take different amounts of time to cook. Also, once he starts cooking a
pizza, he cannot cook another pizza until the first pizza is completely cooked. Let's say we have
three customers who come at time t=0, t=1, & t=2 respectively, and the time needed to cook
their pizzas is 3, 9, & 6min respectively. If Tieu applies first-come, first-served rule, then the
waiting time of three customers is 3, 11, & 16 respectively. The average waiting time in this
case is (3 + 11 + 16) / 3 = 10. This is not an optimized solution. After serving the first customer
at time t=3, Tieu can choose to serve the third customer. In that case, the waiting time will be
3, 7, & 17 respectively. Hence, the average waiting time is (3 + 7 + 17) / 3 = 9.

Help Tieu achieve the minimum average waiting time.
For the sake of simplicity, find the integer part of the minimum average waiting time.

### Input Format
- The first line contains an integer N, which is the number of customers.
- In the next N lines, the i-th line contains two space separated numbers Ti and Li
- Ti is the time when i-th customer order a pizza, and Li is the time required to cook it.

### Return Value
- Either a Right with the integer part of the minimum average waiting time or a Left with a
syntax error description.

### Constraints
Constraints
- 0 ≤ N ≤ 10^5
- 0 ≤ Ti ≤ 10^9
- 1 ≤ Li ≤ 10^9

### Problem Analysis
- N is small enough to fit inside a 32-bit integer.
- Ti and Li can only fit inside a 64-bit integer.
- In the problem state no guarantee was given that input will be sorted by Ti so no such assumption will be made.
- Tieu can only start cooking a pizza after the order for it has been placed.
- Tieu can only start cooking a pizza after the previous pizza has been cooked.

### Solution
This at heart is a scheduling problem. Since the cooking of a pizza cannot be stopped we are solving a scheduling
problem with non-preemptive jobs. This is a similar issue to what CPUs have to deal with when scheduling non-preemptive
tasks.
The most optimal solution is take a Shortest Job First (also known as Shortest Job Next) approach. This means exactly
what it sounds like, we want to schedule the job that takes the least amount of time to complete first.
This is the optimal when minimizing the average waiting time.
A proof by contradiction of this fact can be found in the following link:
https://courses.engr.illinois.edu/cs374/fa2020/lec_prerec/19/19_3_0_0.pdf
