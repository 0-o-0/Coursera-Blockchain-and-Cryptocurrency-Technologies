import java.util.ArrayList;

public class TxHandler {

    private UTXOPool U;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        U = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        UTXOPool pool = new UTXOPool(U);
        int numInputs = tx.numInputs();
        ArrayList<Transaction.Input> inputs = tx.getInputs();
        double sumInputs = 0.0;
        for (int i = 0; i < numInputs; i++) {
            Transaction.Input input = inputs.get(i);
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            if (!pool.contains(utxo)) {
                return false;
            }
            Transaction.Output output = pool.getTxOutput(utxo);
            byte[] rawData = tx.getRawDataToSign(i);
            if (!Crypto.verifySignature(output.address, rawData, input.signature)) {
                return false;
            }
            sumInputs += output.value;
            pool.removeUTXO(utxo);
        }
        double sumOutput = 0.0;
        ArrayList<Transaction.Output> outputs = tx.getOutputs();
        for (Transaction.Output output : outputs) {
            if (output.value < 0.0) {
                return false;
            }
            sumOutput += output.value;
        }
        if (sumInputs < sumOutput) {
            return false;
        }
        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     * A greedy method is adopted where it keeps seeking a valid transaction in {@code possibleTxs}
     * until no more can be added.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        ArrayList<Transaction> rawTxs = new ArrayList<Transaction>();
        while(true) {
            boolean found = false;
            for (Transaction tx : possibleTxs) {
                if (!rawTxs.contains(tx) && isValidTx(tx)) {
                    updateUTXOPool(tx);
                    rawTxs.add(tx);
                    found = true;
                    break;
                }
            }
            if (!found) {
                break;
            }
        }
        Transaction[] txs = new Transaction[rawTxs.size()];
        int i = 0;
        for (Transaction rawTx : rawTxs) {
            txs[i++] = rawTx;
        }
        return txs;
    }

    /**
     * Update UTXO pool to remove the spent ones in the transactions' input and
     * add new ones in the transactions' output
     */
    private void updateUTXOPool(Transaction tx) {
        ArrayList<Transaction.Input> inputs = tx.getInputs();
        for (Transaction.Input input : inputs) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            U.removeUTXO(utxo);
        }
        ArrayList<Transaction.Output> outputs = tx.getOutputs();
        int numOutputs = outputs.size();
        for (int i = 0; i < numOutputs; i++) {
            Transaction.Output output = outputs.get(i);
            UTXO utxo = new UTXO(tx.getHash(), i);
            U.addUTXO(utxo, output);
        }
    }

    public UTXOPool getUTXOPool() {
        return U;
    }

}
