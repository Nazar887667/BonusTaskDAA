public class BST {

    private static class Node {
        int key;
        Node left, right;
        Node(int key) { this.key = key; }
    }

    private Node root;
    private int size;

    public int size() { return size; }

    public boolean insert(int key) {
        if (root == null) { root = new Node(key); size++; return true; }
        Node cur = root;
        while (true) {
            if (key == cur.key) return false;
            if (key < cur.key) {
                if (cur.left == null) { cur.left = new Node(key); size++; return true; }
                cur = cur.left;
            } else {
                if (cur.right == null) { cur.right = new Node(key); size++; return true; }
                cur = cur.right;
            }
        }
    }

    public boolean search(int key) {
        Node cur = root;
        while (cur != null) {
            if (key == cur.key) return true;
            cur = (key < cur.key) ? cur.left : cur.right;
        }
        return false;
    }

    public boolean delete(int key) {
        Node parent = null, cur = root;
        while (cur != null && cur.key != key) {
            parent = cur;
            cur = (key < cur.key) ? cur.left : cur.right;
        }
        if (cur == null) return false;

        if (cur.left != null && cur.right != null) {
            Node succParent = cur;
            Node succ = cur.right;
            while (succ.left != null) { succParent = succ; succ = succ.left; }
            cur.key = succ.key;
            parent = succParent;
            cur = succ;
        }

        Node child = (cur.left != null) ? cur.left : cur.right;
        if (parent == null) root = child;
        else if (parent.left == cur) parent.left = child;
        else parent.right = child;
        size--;
        return true;
    }

    public int height() {
        if (root == null) return 0;
        java.util.ArrayDeque<Node> level = new java.util.ArrayDeque<>();
        level.add(root);
        int h = 0;
        while (!level.isEmpty()) {
            h++;
            for (int i = level.size(); i > 0; i--) {
                Node n = level.poll();
                if (n.left != null) level.add(n.left);
                if (n.right != null) level.add(n.right);
            }
        }
        return h;
    }
}
