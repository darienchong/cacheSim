# cacheSim
Cache simulator written in Java. Work in progress.

Currently supports the following modes:
Direct Mapping (<=> 1 way Set-Associative Cache)
N-way Set-Associative Cache with LRU block replacement policy

# How to use
Compile the .java files.
Pass input in the following format to Main.class:

```
{ways}
{number of cache blocks}
{block size in bytes}
{addresses to query, separated by a newline}
```

Main has a single command-line parameter [0|1], which determines if the cache state is to be printed to the console after every query.
As such, the full input should be as follows:

```
>>> java Main [0|1] < data
```
where data has input in the format as above.
