import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;

public class BlockChainSimulator {
    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(1024, random);

        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey privateKeyAlice = pair.getPrivate();
        PublicKey publicKeyAlice = pair.getPublic();

        pair = keyGen.generateKeyPair();
        PrivateKey privateKeyBob = pair.getPrivate();
        PublicKey publicKeyBob = pair.getPublic();

        // create genesis block with coinbase transaction to Alice
        Block genesisBlock = new Block(null, publicKeyAlice);
        genesisBlock.finalize();
        System.out.println("genesis done");
        BlockChain blockChain = new BlockChain(genesisBlock);
        System.out.println("blockchain down");
        BlockHandler blockHandler = new BlockHandler(blockChain);
        System.out.println("block handler done");
        System.out.println("Genesis block: " + genesisBlock);
        printBlockChainStatus(blockChain);

        // process another block mined by Bob, with a transaction from Alice to Bob
        Block block1 = new Block(genesisBlock.getHash(), publicKeyBob);
        Transaction tx = new Transaction();
        tx.addInput(genesisBlock.getCoinbase().getHash(), 0);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKeyAlice);
        signature.update(tx.getRawDataToSign(0));
        tx.addSignature(signature.sign(), 0);
        tx.finalize();
        block1.addTransaction(tx);
        block1.finalize();
        System.out.println(blockHandler.processBlock(block1));
        System.out.println("First block: " + block1);
        printBlockChainStatus(blockChain);

        // process a transaction from Bob to Alice
        tx = new Transaction();
        tx.addInput(block1.getCoinbase().getHash(), 0);
        signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKeyBob);
        signature.update(tx.getRawDataToSign(0));
        tx.addSignature(signature.sign(), 0);
        tx.finalize();
        blockHandler.processTx(tx);
        System.out.println("First tx: " + tx);
        printBlockChainStatus(blockChain);

        // create a new block
        System.out.println("Second block: " + blockHandler.createBlock(publicKeyAlice));
        printBlockChainStatus(blockChain);
    }

    private static void printBlockChainStatus(BlockChain blockChain) {
        System.out.println("====================");
        Block maxHeightBlock = blockChain.getMaxHeightBlock();
        System.out.println("Max Height Block: " + maxHeightBlock);
        UTXOPool maxHeightUXTOPool = blockChain.getMaxHeightUTXOPool();
        System.out.println("Max Height UTXO Pool: " + maxHeightUXTOPool);
        System.out.println(maxHeightUXTOPool.getAllUTXO());
        TransactionPool transactionPool = blockChain.getTransactionPool();
        System.out.println("Transaction Pool: " + transactionPool);
        System.out.println(transactionPool.getTransactions());
        System.out.println("====================");
    }
}
