Bitcoin and Cryptocurrency Technologies
====

Coursera course [Bitcoin and Cryptocurrency Technologies](https://www.coursera.org/learn/cryptocurrency)

## Assignment #1 Scrooge Coin
[Instruction Link](https://www.coursera.org/learn/cryptocurrency/programming/KOo3V/scrooge-coin)

Solution:
* Implemented validations according to the instruction
* Took a greedy algorithm approach on handling transactions by keeping finding a valid transaction until no more can be added

## Assignment #2 Consensus Network
[Instruction Link]( https://www.coursera.org/learn/cryptocurrency/programming/Bn6DI/consensus-from-trust)

Solution:
* Accept all the transactions
* In the assignment setup, connected nodes should be able to reach to consensus in a few rounds
* Trick: stop accepting new transactions in the last two rounds, as some malicious nodes could start sending new transactions at the end to disrupt compliant nodes


## Assignment #3 Blockchain
[Instruction Link](https://www.coursera.org/learn/cryptocurrency/programming/1fi2s/blockchain)

Solution:
* Maintain a list of blocks by block height, so that we can cut off some old blocks over time
* Maintain a hash map between block hash and height to look up block height quickly
* Maintain a hash map between block and UTXO pool, because the blocks essentially form a tree, and each block has a different UTXO pool
* Maintain a transaction pool
* Implement the functions accordingly to maintain the data structures mentioned above
