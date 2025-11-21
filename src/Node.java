// Node.java
import java.util.ArrayList;
import java.util.List;

public class Node {
    String key;            // nome (chave da árvore)
    List<Reserva> records; // todas as reservas desse nome
    Node left, right;
    int height;

    public Node(String key, Reserva r) {
        this.key = key;
        this.records = new ArrayList<>();
        this.records.add(r);
        this.left = null;
        this.right = null;
        this.height = 1;
    }
}
