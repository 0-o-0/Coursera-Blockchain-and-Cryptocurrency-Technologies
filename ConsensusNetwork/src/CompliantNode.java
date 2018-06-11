import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {
    private Set<Transaction> Txs;
    int NumRounds;
    int CountRounds;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.NumRounds = numRounds;
        this.CountRounds = 0;
    }

    public void setFollowees(boolean[] followees) {
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.Txs = pendingTransactions;
    }

    public Set<Transaction> sendToFollowers() {
        return Txs;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        CountRounds++;
        if (CountRounds + 2 >= NumRounds) {
            return;
        }
        for (Candidate c : candidates) {
            Txs.add(c.tx);
        }
    }
}
