// Block Chain should maintain only limited block nodes to satisfy the functions
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockChain {
    public static final int CUT_OFF_AGE = 10;
    private TransactionPool transactionPool;
    private int maxHeight;
    private List<List<Block>> blocks;
    private Map<ByteArrayWrapper, Integer> heights;
    private Map<ByteArrayWrapper, UTXOPool> UTXOPools;

    /**
     * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */
    public BlockChain(Block genesisBlock) {
        this.transactionPool = new TransactionPool();

        this.maxHeight = 0;

        List<Block> rootLevelList = new ArrayList<>();
        rootLevelList.add(genesisBlock);
        this.blocks = new ArrayList<>();
        this.blocks.add(rootLevelList);

        this.heights = new HashMap<>();
        ByteArrayWrapper wrapper = new ByteArrayWrapper(genesisBlock.getHash());
        this.heights.put(wrapper, 0);

        this.UTXOPools = new HashMap<>();
        UTXOPool utxoPool = new UTXOPool();
        addCoinbaseToUTXOPool(utxoPool, genesisBlock.getCoinbase());
        this.UTXOPools.put(wrapper, utxoPool);
    }

    /** Get the maximum height block */
    public Block getMaxHeightBlock() {
        return blocks.get(maxHeight).get(0);
    }

    /** Get the UTXOPool for mining a new block on top of max height block */
    public UTXOPool getMaxHeightUTXOPool() {
        return UTXOPools.get(new ByteArrayWrapper(this.getMaxHeightBlock().getHash()));
    }

    /** Get the transaction pool to mine a new block */
    public TransactionPool getTransactionPool() {
        return transactionPool;
    }

    /**
     * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
     * 
     * <p>
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
     * at height 2.
     * 
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {
        // If a genesis block is mined or received, then reject
        if (block.getPrevBlockHash() == null) {
            return false;
        }

        ByteArrayWrapper previousBlockWrapper = new ByteArrayWrapper(block.getPrevBlockHash());
        // If the previous block doesn't exist, then reject
        if (!heights.containsKey(previousBlockWrapper)) {
            return false;
        }

        int height = heights.get(previousBlockWrapper) + 1;
        // If the height is below the cut off, then reject
        if (height <= maxHeight - CUT_OFF_AGE) {
            return false;
        }

        UTXOPool previousUTXOPool = UTXOPools.get(previousBlockWrapper);
        TxHandler txHandler = new TxHandler(previousUTXOPool);
        ArrayList<Transaction> possibleTxs = block.getTransactions();
        Transaction[] txs = txHandler.handleTxs(possibleTxs.toArray(new Transaction[0]));
        // If any transaction in the block is invalid, then reject
        if (txs.length < possibleTxs.size()) {
            return false;
        }

        // Update maxHeight, blocks, heights and UTXOPools accordingly
        if (height > maxHeight) {
            List<Block> newLevelList = new ArrayList<>();
            newLevelList.add(block);
            blocks.add(newLevelList);
            maxHeight = height;
        } else {
            blocks.get(height).add(block);
        }
        ByteArrayWrapper currentBlockWrapper = new ByteArrayWrapper(block.getHash());
        heights.put(currentBlockWrapper, height);
        UTXOPool currentUTXOPool = txHandler.getUTXOPool();
        addCoinbaseToUTXOPool(currentUTXOPool, block.getCoinbase());
        UTXOPools.put(currentBlockWrapper, currentUTXOPool);
        return true;
    }

    /** Add a transaction to the transaction pool */
    public void addTransaction(Transaction tx) {
        transactionPool.addTransaction(tx);
    }

    /**
     * Coinbase transaction is not handled in TxHandler
     * This method handles Coinbase transaction explicitly
     */
    private void addCoinbaseToUTXOPool(UTXOPool pool, Transaction tx) {
        ArrayList<Transaction.Output> outputs = tx.getOutputs();
        for (int i = 0; i < outputs.size(); i++) {
            pool.addUTXO(new UTXO(tx.getHash(), i), outputs.get(i));
        }
    }
}