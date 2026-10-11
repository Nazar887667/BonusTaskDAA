import java.util.*;

public class Tests {
    public static void main(String[] args) {
        Random rnd = new Random(7);
        BST bst = new BST();
        HashTable ht = new HashTable();
        TreeSet<Integer> ref = new TreeSet<>();

        for (int i = 0; i < 200_000; i++) {
            int k = rnd.nextInt(2000);
            int op = rnd.nextInt(3);
            if (op == 0) {
                boolean e = ref.add(k);
                check(bst.insert(k) == e, "BST insert " + k);
                check(ht.insert(k) == e, "Hash insert " + k);
            } else if (op == 1) {
                boolean e = ref.contains(k);
                check(bst.search(k) == e, "BST search " + k);
                check(ht.search(k) == e, "Hash search " + k);
            } else {
                boolean e = ref.remove(k);
                check(bst.delete(k) == e, "BST delete " + k);
                check(ht.delete(k) == e, "Hash delete " + k);
            }
            check(bst.size() == ref.size() && ht.size() == ref.size(), "size mismatch");
        }
        System.out.println("All tests passed.");
    }

    static void check(boolean ok, String msg) {
        if (!ok) throw new AssertionError(msg);
    }
}
