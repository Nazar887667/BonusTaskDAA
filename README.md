# BST vs Hash Table

Comparison of a Binary Search Tree and a Hash Table in Java for insert, search and delete.

## Files
- `src/BST.java` - unbalanced binary search tree
- `src/HashTable.java` - hash table with chaining, resizes when load factor > 0.75
- `src/Benchmark.java` - timing tests
- `src/Tests.java` - checks both structures against Java's TreeSet
- `results/` - output of the benchmark (`results.csv`, `structure_info.txt`)

## How to run
Run `Tests.java` first, then `Benchmark.java` (from the project root, so the `results` folder is found).

## Implementation
The BST stores integers and has no balancing. I wrote insert, search and delete without recursion. Delete has three cases: leaf, one child, and two children (the node is replaced by its in-order successor).

The hash table is an array of linked lists. The index is computed with a multiplicative hash, and when the table gets more than 75% full the array is doubled and everything is rehashed.

## Complexity

| Operation | BST best | BST worst | Hash best | Hash worst |
|-----------|----------|-----------|-----------|------------|
| Insert | O(1) | O(n) | O(1) | O(n) |
| Search | O(1) | O(n) | O(1) | O(n) |
| Delete | O(1) | O(n) | O(1) | O(n) |

Average case: BST is O(log n) and hash table is O(1).

For the BST the cost depends on the height. Random keys give a height of about log n, but sorted keys turn the tree into a linked list, so every operation is O(n). For the hash table the worst case happens when all keys end up in the same bucket. Resizing is O(n) but it is rare, so insert is O(1) amortized.

## Experiment
I tested n = 100, 1,000 and 10,000 with random keys and with sorted keys. Each case ran 300 warm-up times and then 51 timed times, and I used the median. Times below are total microseconds for all n operations.

**Random keys**

| n | Structure | Insert | Search | Delete |
|---|-----------|-------:|-------:|-------:|
| 100 | BST | 3.0 | 2.4 | 2.6 |
| 100 | Hash | 8.6 | 5.5 | 5.8 |
| 1,000 | BST | 116.1 | 32.9 | 53.4 |
| 1,000 | Hash | 48.6 | 14.9 | 15.5 |
| 10,000 | BST | 1,332.5 | 839.3 | 937.6 |
| 10,000 | Hash | 459.5 | 104.5 | 126.2 |

**Sorted keys**

| n | Structure | Insert | Search | Delete |
|---|-----------|-------:|-------:|-------:|
| 100 | BST | 15.1 | 17.2 | 8.9 |
| 100 | Hash | 2.2 | 0.5 | 0.7 |
| 1,000 | BST | 2,648.5 | 3,165.7 | 1,725.1 |
| 1,000 | Hash | 31.0 | 6.4 | 9.8 |
| 10,000 | BST | 225,816.1 | 273,314.8 | 172,371.2 |
| 10,000 | Hash | 444.7 | 101.2 | 125.5 |

**Structure shape**

| Order | n | BST height | Hash longest chain |
|-------|---|-----------:|-------------------:|
| Random | 100 | 14 | 3 |
| Random | 1,000 | 21 | 4 |
| Random | 10,000 | 34 | 6 |
| Sorted | 100 | 100 | 3 |
| Sorted | 1,000 | 1,000 | 4 |
| Sorted | 10,000 | 10,000 | 6 |

## Results
- With random keys the hash table was faster for larger n. At n = 10,000 it was about 3x faster for insert and about 7-8x faster for search and delete.
- With sorted keys the BST was very slow. At n = 10,000 the tree height was 10,000 and searching took about 270 ms, while the hash table needed about 0.1 ms. Going from 1,000 to 10,000 elements made BST insert about 85x slower, which fits O(n) per operation. The hash table did not care about the order of the keys.
- For n = 100 the results are too small and noisy to compare. The BST was even faster than the hash table with random keys, probably because of JIT warm-up and the hash table resizing from its starting size.

## Why the performance is different
- The hash table jumps directly to a bucket, the BST has to walk down the tree level by level.
- The BST depends on the insertion order, the hash table depends on the hash function.
- BST nodes are spread around in memory, so more cache misses. The bucket array is in one block.
- Hash table inserts sometimes pay for a resize, which can be seen for small n.
- The BST keeps keys sorted, so it is better for range queries and min/max, which a hash table can't do.

## Conclusion
For simple insert/search/delete the hash table was faster and more stable. An unbalanced BST is fine for random data but can become O(n) on sorted data. If I needed ordering I would use a balanced tree like Java's `TreeMap`.