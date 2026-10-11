import java.io.PrintWriter;
import java.util.*;

public class Benchmark {

    static final int[] SIZES = {100, 1_000, 10_000};
    static final int RUNS = 51;
    static final int WARMUP = 300;
    static volatile boolean sink;

    interface Dict {
        boolean insert(int k);
        boolean search(int k);
        boolean delete(int k);
    }

    static Dict bst()  { BST t = new BST();       return new Dict() {
        public boolean insert(int k) { return t.insert(k); }
        public boolean search(int k) { return t.search(k); }
        public boolean delete(int k) { return t.delete(k); } }; }

    static Dict hash() { HashTable t = new HashTable(); return new Dict() {
        public boolean insert(int k) { return t.insert(k); }
        public boolean search(int k) { return t.search(k); }
        public boolean delete(int k) { return t.delete(k); } }; }

    static long[] runOnce(boolean useBst, int[] insertKeys, int[] searchKeys, int[] deleteKeys) {
        Dict d = useBst ? bst() : hash();
        boolean r = false;

        long t0 = System.nanoTime();
        for (int k : insertKeys) r ^= d.insert(k);
        long t1 = System.nanoTime();
        for (int k : searchKeys) r ^= d.search(k);
        long t2 = System.nanoTime();
        for (int k : deleteKeys) r ^= d.delete(k);
        long t3 = System.nanoTime();

        sink = r;
        return new long[]{t1 - t0, t2 - t1, t3 - t2};
    }

    static long median(long[] a) {
        long[] c = a.clone();
        Arrays.sort(c);
        return c[c.length / 2];
    }

    public static void main(String[] args) throws Exception {
        Random rnd = new Random(42);
        StringBuilder csv = new StringBuilder("structure,order,n,insert_us,search_us,delete_us,insert_ns_per_op,search_ns_per_op,delete_ns_per_op\n");
        StringBuilder extra = new StringBuilder();

        for (String order : new String[]{"RANDOM", "SORTED"}) {
            for (int n : SIZES) {
                int[] keys = new int[n];
                for (int i = 0; i < n; i++) keys[i] = i;
                if (order.equals("RANDOM")) {
                    for (int i = n - 1; i > 0; i--) {
                        int j = rnd.nextInt(i + 1);
                        int tmp = keys[i]; keys[i] = keys[j]; keys[j] = tmp;
                    }
                }

                int[] searchKeys = keys.clone();
                for (int i = n - 1; i > 0; i--) {
                    int j = rnd.nextInt(i + 1);
                    int tmp = searchKeys[i]; searchKeys[i] = searchKeys[j]; searchKeys[j] = tmp;
                }

                for (boolean useBst : new boolean[]{true, false}) {
                    for (int i = 0; i < WARMUP; i++) runOnce(useBst, keys, searchKeys, searchKeys);

                    long[] ins = new long[RUNS], sea = new long[RUNS], del = new long[RUNS];
                    for (int i = 0; i < RUNS; i++) {
                        long[] r = runOnce(useBst, keys, searchKeys, searchKeys);
                        ins[i] = r[0]; sea[i] = r[1]; del[i] = r[2];
                    }
                    long mi = median(ins), ms = median(sea), md = median(del);
                    String name = useBst ? "BST" : "HashTable";
                    csv.append(String.format(Locale.ROOT, "%s,%s,%d,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f%n",
                            name, order, n, mi / 1e3, ms / 1e3, md / 1e3,
                            (double) mi / n, (double) ms / n, (double) md / n));
                }

                BST b = new BST(); HashTable h = new HashTable();
                for (int k : keys) { b.insert(k); h.insert(k); }
                extra.append(String.format("%s n=%d : BST height=%d, HashTable longest chain=%d%n",
                        order, n, b.height(), h.longestChain()));
            }
        }

        System.out.println(csv);
        System.out.println(extra);
        try (PrintWriter w = new PrintWriter("results/results.csv")) { w.print(csv); }
        try (PrintWriter w = new PrintWriter("results/structure_info.txt")) { w.print(extra); }
    }
}
