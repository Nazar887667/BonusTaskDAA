public class HashTable {

    private static class Entry {
        int key;
        Entry next;
        Entry(int key, Entry next) { this.key = key; this.next = next; }
    }

    private Entry[] buckets;
    private int size;
    private static final double MAX_LOAD = 0.75;

    public HashTable() { this(16); }

    public HashTable(int capacity) { buckets = new Entry[capacity]; }

    public int size() { return size; }

    private int indexFor(int key, int length) {
        int h = key * 0x9E3779B9;
        h ^= (h >>> 16);
        return (h & 0x7fffffff) % length;
    }

    public boolean insert(int key) {
        int i = indexFor(key, buckets.length);
        for (Entry e = buckets[i]; e != null; e = e.next)
            if (e.key == key) return false;
        buckets[i] = new Entry(key, buckets[i]);
        size++;
        if ((double) size / buckets.length > MAX_LOAD) resize();
        return true;
    }

    public boolean search(int key) {
        for (Entry e = buckets[indexFor(key, buckets.length)]; e != null; e = e.next)
            if (e.key == key) return true;
        return false;
    }

    public boolean delete(int key) {
        int i = indexFor(key, buckets.length);
        Entry prev = null;
        for (Entry e = buckets[i]; e != null; prev = e, e = e.next) {
            if (e.key == key) {
                if (prev == null) buckets[i] = e.next; else prev.next = e.next;
                size--;
                return true;
            }
        }
        return false;
    }

    private void resize() {
        Entry[] old = buckets;
        Entry[] fresh = new Entry[old.length * 2];
        for (Entry head : old) {
            for (Entry e = head; e != null; ) {
                Entry next = e.next;
                int i = indexFor(e.key, fresh.length);
                e.next = fresh[i];
                fresh[i] = e;
                e = next;
            }
        }
        buckets = fresh;
    }

    public int longestChain() {
        int max = 0;
        for (Entry head : buckets) {
            int len = 0;
            for (Entry e = head; e != null; e = e.next) len++;
            max = Math.max(max, len);
        }
        return max;
    }
}
