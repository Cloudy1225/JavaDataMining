package main.java.core.collection;

import java.util.*;

/**
 * An unbounded priority queue based on a priority heap.
 * The elements of the priority queue are ordered according to
 * given priority which is a {@code double} value when {@link #offer(Object, double)}.
 * This is different from {@link java.util.PriorityQueue}.
 *
 * @param <E> the type of elements held in this queue
 * @author Cloudy1225
 * @deprecated
 */
public class PriorityQueue<E> implements Iterable<E> {

    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    /**
     * Priority queue represented as a balanced binary heap: the two
     * children of queue[n] are queue[2*n+1] and queue[2*(n+1)].  The
     * priority queue is ordered by comparator, or by the elements'
     * natural ordering, if comparator is null: For each node n in the
     * heap and each descendant d of n, n <= d.  The element with the
     * lowest value is in queue[0], assuming the queue is nonempty.
     */
    transient Object[] queue; // non-private to simplify nested class access

    /**
     * The corresponding priority of each element in the queue.
     */
    transient double[] priorities;

    /**
     * The number of elements in the priority queue.
     */
    private int size = 0;

    /**
     * The number of times this priority queue has been
     * <i>structurally modified</i>.  See AbstractList for gory details.
     */
    transient int modCount = 0; // non-private to simplify nested class access

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Creates a {@code PriorityQueue} with the default initial
     * capacity (11) that orders its elements according to their
     * {@linkplain Comparable natural ordering}.
     */
    public PriorityQueue() {
        this.queue = new Object[DEFAULT_INITIAL_CAPACITY];
        this.priorities = new double[DEFAULT_INITIAL_CAPACITY];
    }

    /**
     * Creates a {@code PriorityQueue} with the specified initial capacity
     * that orders its elements according to the specified comparator.
     *
     * @param  initialCapacity the initial capacity for this priority queue
     * @throws IllegalArgumentException if {@code initialCapacity} is
     *         less than 1
     */
    public PriorityQueue(int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException("The initialCapacity is less than 1");
        this.queue = new Object[initialCapacity];
        this.priorities = new double[initialCapacity];
    }

    /**
     * Increases the capacity of the array.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow(int minCapacity) {
        int oldCapacity = queue.length;
        // Double size if small; else grow by 50%
        int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                (oldCapacity + 2) :
                (oldCapacity >> 1));
        // overflow-conscious code
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        queue = Arrays.copyOf(queue, newCapacity);
        priorities = Arrays.copyOf(priorities, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    /**
     * Inserts the specified element into this priority queue.
     *
     * @param element the item to insert
     * @param priority the priority of the item
     * @return {@code true} (as specified by {@link Queue#offer})
     * @throws NullPointerException if the specified element is null
     */
    public boolean offer(E element, double priority) {
        if (element == null)
            throw new NullPointerException();
        modCount++;
        int i = size;
        if (i >= queue.length)
            grow(i + 1);
        size = i + 1;
        if (i == 0) {
            queue[0] = element;
            priorities[0] = priority;
        }

        else
            siftUp(i, element, priority);
        return true;
    }

    /**
     * Retrieves, but does not remove, the head of this queue,
     * or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    @SuppressWarnings("unchecked")
    public E peek() {
        return (size == 0) ? null : (E) queue[0];
    }

    /**
     * Returns an iterator over the elements in this queue. The iterator
     * does not return the elements in any particular order.
     *
     * @return an iterator over the elements in this queue
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            /**
             * Index (into queue array) of element to be returned by
             * subsequent call to next.
             */
            private int cursor = 0;

            /**
             * The modCount value that the iterator believes that the backing
             * Queue should have.  If this expectation is violated, the iterator
             * has detected concurrent modification.
             */
            private int expectedModCount = modCount;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                if (expectedModCount != modCount)
                    throw new ConcurrentModificationException();
                if (cursor < size)
                    return (E) queue[cursor++];
                else
                    throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Retrieves and removes the head of this queue,
     * or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    public E poll() {
        if (size == 0)
            return null;
        int s = --size;
        modCount++;
        E result = (E) queue[0];
        Object x = queue[s];
        queue[s] = null;
        if (s != 0)
            siftDown(0, x, priorities[s]);
        return result;
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * demoting x down the tree repeatedly until it is less than or
     * equal to its children or is a leaf.
     *
     * @param k the position to fill
     * @param x the item to insert
     * @param p the priority of the item
     */
    private void siftDown(int k, Object x, double p) {
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = queue[child];
            int right = child + 1;

            if (right < size &&
                    priorities[child] > priorities[right])
                c = queue[child = right];
            if (p <= priorities[child])
                break;
            queue[k] = c;
            priorities[k] = priorities[child];
            k = child;
        }
        queue[k] = x;
        priorities[k] = p;
    }

    private int indexOf(Object o) {
        if (o != null) {
            for (int i = 0; i < size; i++)
                if (o.equals(queue[i]))
                    return i;
        }
        return -1;
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * promoting x up the tree until it is greater than or equal to
     * its parent, or is the root.
     *
     * @param k the position to fill
     * @param x the item to insert
     * @param p the priority of the item
     */
    private void siftUp(int k, Object x, double p) {
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            double pp = priorities[parent]; // the priority of parent
            if (p >= pp)
                break;
            queue[k] = e;
            priorities[k] = pp;
            k = parent;
        }
        queue[k] = x;
        priorities[k] = p;
    }

    /**
     * Returns {@code true} if this queue contains the specified element.
     * More formally, returns {@code true} if and only if this queue contains
     * at least one element {@code e} such that {@code o.equals(e)}.
     *
     * @param o object to be checked for containment in this queue
     * @return {@code true} if this queue contains the specified element
     */
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * Returns a map containing all the elements and priorities in this queue.
     * The elements are in priority order.
     *
     * @return a map containing all the elements and priorities in this queue
     */
    public Map<E, Double> toMap() {
        class Pair {
            final Object element;
            final double priority;
            Pair(Object e, double p) {
                element = e;
                priority = p;
            }
        }
        ArrayList<Pair> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(new Pair(queue[i], priorities[i]));
        }
        list.sort(new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                double d = o1.priority - o2.priority;
                if (d > 0) {
                    return 1;
                } else if (d == 0) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        LinkedHashMap<E, Double> map = new LinkedHashMap<>();
        for (Pair entry: list) {
            map.put((E) entry.element, entry.priority);
        }
        return map;
    }

    /**
     * Returns an array containing all the elements in this queue.
     * The elements are in no particular order.
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this queue.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all the elements in this queue
     */
    public Object[] toArray() {
        return Arrays.copyOf(queue, size);
    }

    /**
     * Returns an array containing all of the elements in this queue; the
     * runtime type of the returned array is that of the specified array.
     * The returned array elements are in no particular order.
     * If the queue fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this queue.
     *
     * <p>If the queue fits in the specified array with room to spare
     * (i.e., the array has more elements than the queue), the element in
     * the array immediately following the end of the collection is set to
     * {@code null}.
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * <p>Suppose {@code x} is a queue known to contain only strings.
     * The following code can be used to dump the queue into a newly
     * allocated array of {@code String}:
     *
     *  <pre> {@code String[] y = x.toArray(new String[0]);}</pre>
     *
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * @param a the array into which the elements of the queue are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing all of the elements in this queue
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this queue
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        final int size = this.size;
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(queue, size, a.getClass());
        System.arraycopy(queue, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    /**
     * Removes all the elements from this priority queue.
     * The queue will be empty after this call returns.
     */
    public void clear() {
        modCount++;
        for (int i = 0; i < size; i++)
            queue[i] = null;
        size = 0;
    }

    /**
     * Returns the number of elements in this queue.
     *
     * @return number of elements
     */
    public int size() {
        return size;
    }

}
