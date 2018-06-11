import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class MaliciousNode implements Node {
    Set<Transaction> Txs;
    Random random;
    int NumRounds;
    int CountRounds;

    public MaliciousNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.random = new Random();
        this.NumRounds = numRounds;
        this.CountRounds = 0;
    }

    public void setFollowees(boolean[] followees) {
        return;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.Txs = pendingTransactions;
    }

    public Set<Transaction> sendToFollowers() {
        CountRounds++;
        if (CountRounds < NumRounds) {
            return new HashSet<Transaction>();
        } else {
            return Txs;
        }
        /*
        HashSet<Transaction> txs = new HashSet<>();
        for (Transaction tx : Txs) {
            if (random.nextInt() % 2 == 0) {
                txs.add(tx);
            }
        }
        return txs;
        */
        /*
        if (random.nextInt() % 2 == 0) {
            return new HashSet<Transaction>();
        } else {
            return Txs;
        }
        */
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        return;
    }
}
