// AVL.java
public class AVL {
    private Node root;

    private int height(Node n) { return (n == null) ? 0 : n.height; }

    private int balanceFactor(Node n) { return (n == null) ? 0 : height(n.left) - height(n.right); }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        return y;
    }

    // comparação ignore case para ordenação das chaves
    private int compareKey(String a, String b) {
        return a.compareToIgnoreCase(b);
    }

    // Inserir: se chave existir, adiciona à lista do nó (cuida de nomes iguais)
    private Node insert(Node node, String key, Reserva r) {
        if (node == null) return new Node(key, r);

        int cmp = compareKey(key, node.key);
        if (cmp < 0) node.left = insert(node.left, key, r);
        else if (cmp > 0) node.right = insert(node.right, key, r);
        else {
            // mesma chave: adiciona à lista (mantemos a ordem de inserção)
            node.records.add(r);
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = balanceFactor(node);

        // LL
        if (balance > 1 && compareKey(key, node.left.key) < 0) return rotateRight(node);
        // RR
        if (balance < -1 && compareKey(key, node.right.key) > 0) return rotateLeft(node);
        // LR
        if (balance > 1 && compareKey(key, node.left.key) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        // RL
        if (balance < -1 && compareKey(key, node.right.key) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    public void insert(String key, Reserva r) {
        if (key == null) return;
        root = insert(root, key.trim(), r);
    }

    // retorna lista de reservas para a chave (ou lista vazia)
    public java.util.List<Reserva> search(String key) {
        Node cur = root;
        while (cur != null) {
            int cmp = compareKey(key, cur.key);
            if (cmp == 0) return cur.records;
            else if (cmp < 0) cur = cur.left;
            else cur = cur.right;
        }
        return java.util.Collections.emptyList();
    }
}
