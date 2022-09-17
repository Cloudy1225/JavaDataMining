package main.java.core.collection;

import main.java.core.Copyable;

import java.util.*;
import java.util.function.Consumer;

/**
 * This class provides considerable memory efficiency improvements over using an <tt>ArrayList</tt> to store doubles.
 * This is a copy of {@code ArrayList<Double>} ,but null is not allowed into the list.
 *
 * @author Cloudy1225
 * @deprecated {@code ArrayList<Double>} is OK.
 */
public class DoubleList implements RandomAccess, Copyable<DoubleList>, Iterable<Double> {

    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Shared empty array instance used for empty instances.
     */
    private static final double[] EMPTY_ELEMENTDATA = {};

    /**
     * Shared empty array instance used for default sized empty instances. We
     * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
     * first element is added.
     */
    private static final double[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * The array buffer into which the elements of the DoubleList are stored.
     * The capacity of the DoubleList is the length of this array buffer. Any
     * empty DoubleList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * will be expanded to DEFAULT_CAPACITY when the first element is added.
     */
    transient double[] elementData; // non-private to simplify nested class access

    /**
     * The size of the DoubleList (the number of elements it contains).
     *
     * @serial
     */
    private int size;

    /**
     * The number of times this list has been <i>structurally modified</i>.
     * Structural modifications are those that change the size of the
     * list, or otherwise perturb it in such a fashion that iterations in
     * progress may yield incorrect results.
     */
    private transient int modCount = 0;

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param  initialCapacity  the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    public DoubleList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new double[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        }
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public DoubleList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    /**
     * Constructs a list containing the elements of the specified array.
     * And this constructor will not allocate a new array.
     *
     * @param elements the array whose elements are to be placed into this list
     * @throws NullPointerException if the specified array is null
     */
    public DoubleList(double[] elements) {
        if ((size = elements.length) == 0) {
            // replace with empty array.
            elementData = EMPTY_ELEMENTDATA;
        } else {
            elementData = elements;
        }
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public DoubleList(Collection<Double> c) {
        if ((size = c.size()) == 0) {
            // replace with empty array.
            elementData = EMPTY_ELEMENTDATA;
        } else {
            Double[] elements = new Double[size];
            c.toArray(elements);
            elementData = doublize(elements);
        }
    }

    /**
     * Transforms a Double array to a double array.
     *
     * @param arr a Double array
     * @return a double array
     * @throws NullPointerException if given arr contains <tt>null</tt>
     */
    private static double[] doublize(Double[] arr) {
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }

    /**
     * Trims the capacity of this <tt>DoubleList</tt> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an <tt>DoubleList</tt> instance.
     */
    public void trimToSize() {
        modCount++;
        if (size < elementData.length) {
            elementData = (size == 0)
                    ? EMPTY_ELEMENTDATA
                    : Arrays.copyOf(elementData, size);
        }
    }

    /**
     * Increases the capacity of this <tt>DoubleList</tt> instance, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     *
     * @param   minCapacity   the desired minimum capacity
     */
    public void ensureCapacity(int minCapacity) {
        int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
                // any size if not default element table
                ? 0
                // larger than default for default empty table. It's already
                // supposed to be at default size.
                : DEFAULT_CAPACITY;

        if (minCapacity > minExpand) {
            ensureExplicitCapacity(minCapacity);
        }
    }

    private static int calculateCapacity(double[] elementData, int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        return minCapacity;
    }

    private void ensureCapacityInternal(int minCapacity) {
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
    }

    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;

        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this list contains no elements.
     *
     * @return <tt>true</tt> if this list contains no elements
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     *
     * @param element element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     */
    public boolean contains(double element) {
        return indexOf(element) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     *
     * @param element the element you want to {@code indexOf}
     * @return the index of the first occurrence, or -1
     */
    public int indexOf(double element) {
        for (int i = 0; i < size; i++) {
            if (elementData[i] == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     *
     * @param element the element you want to {@code lastIndexOf}
     * @return the index of the last occurrence, or -1
     */
    public int lastIndexOf(double element) {
        for (int i = size-1; i >= 0; i--) {
            if (elementData[i] == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a deep copy of this <tt>DoubleList</tt> instance.
     *
     * @return a clone of this <tt>DoubleList</tt> instance
     */
    @Override
    public DoubleList copy() {
        DoubleList res = new DoubleList();
        res.elementData = new double[size];
        System.arraycopy(this.elementData, 0, res.elementData, 0, size);
        res.modCount = 0;
        res.size = this.size;
        return res;
    }

    /**
     * Returns an array containing all the elements in this list
     * in proper sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * @return an array containing all the elements in this list in
     *         proper sequence
     */
    public double[] toArray() {
        double[] res = new double[size];
        System.arraycopy(this.elementData, 0, res, 0, size);
        return res;
    }

    /**
     * Returns an array containing all the elements in this list in proper
     * sequence (from first to last element). If the list fits in the
     * specified array, it is returned therein. Otherwise, a new array is
     * allocated with the size of this list.
     *
     * <p>If the list fits in the specified array with room to spare
     * (i.e., the array has more elements than the list), the element in
     * the array immediately following the end of the list is set to
     * <tt>Double.NaN</tt>.  (This is useful in determining the length of the
     * list <i>only</i> if the caller knows that the list does not contain
     * any Double.NaN elements.)
     *
     * @param target the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array
     *          is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws NullPointerException if the specified array is null
     */
    public double[] toArray(double[] target) {
        if (target.length < size)
            return this.toArray();
        System.arraycopy(elementData, 0, target, 0, size);
        if (target.length > size)
            target[size] = Double.NaN;
        return target;
    }

    /**
     * Returns a {@code List<Double>} containing all the elements in this list
     * in proper sequence (from first to last element).
     *
     * @return a list view of this : ArrayList<Double>
     */
    public List<Double> asList() {
        ArrayList<Double> res = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            res.add(elementData[i]);
        }
        return res;
    }


    // Positional Access Operations

    /**
     * Returns the element at the specified position in this list.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index >= size
     */
    public double get(int index) {
        rangeCheck(index);

        return elementData[index];
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index >= size
     */
    public double set(int index, double element) {
        rangeCheck(index);

        double oldValue = elementData[index];
        elementData[index] = element;
        return oldValue;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return always <tt>true</tt>
     */
    public boolean add(double e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException if the index >= size
     */
    public void add(int index, double element) {
        rangeCheckForAdd(index);

        ensureCapacityInternal(size + 1);  // Increments modCount!!
        System.arraycopy(elementData, index, elementData, index + 1,
                size - index);
        elementData[index] = element;
        size++;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException if the index >= size
     */
    public double remove(int index) {
        rangeCheck(index);

        modCount++;
        double oldValue = elementData[index];

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);
        size--;
        return oldValue;
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.
     * Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param element element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
    public boolean remove(double element) {
        for (int index = 0; index < size; index++) {
            if (elementData[index] == element) {
                fastRemove(index);
                return true;
            }
        }

        return false;
    }

    /**
     * Private remove method that skips bounds checking and does not
     * return the value removed.
     */
    private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);
        size--;
    }

    /**
     * Removes all the elements from this list.  The list will
     * be empty after this call returns.
     */
    public void clear() {
        modCount++;
        elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
        size = 0;
    }

    /**
     * Appends all the elements in the specified array to the end of
     * this list, in the "index-order".
     *
     * @param elements array containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified array is null
     */
    public boolean addAll(double[] elements) {
        int numNew = elements.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount
        System.arraycopy(elements, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Appends all the elements in the specified DoubleList to the end of
     * this list, in the "index-order".  This method supports this <tt>addAll</tt> itself.
     *
     * @param list DoubleList containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified DoubleList is null
     */
    public boolean addAll(DoubleList list) {
        double[] elements = list.elementData;
        int numNew = list.size; // can't be elements.length
        ensureCapacityInternal(size + numNew);  // Increments modCount
        System.arraycopy(elements, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Appends all the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.
     *
     * @param c <tt>Double</tt> collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null or contains <tt>null</tt>
     * @throws ArrayStoreException if the runtime type of the element in the given collection is not <tt>Double</tt>
     */
    public boolean addAll(Collection<Double> c) {
        Double[] a = new Double[c.size()];
        c.toArray(a);
        double[] elements = doublize(a);
        int numNew = elements.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount
        System.arraycopy(elements, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Inserts all the elements in the specified array into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the "index-order".
     *
     * @param index index at which to insert the first element from the
     *              specified array
     * @param elements array containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException if the index >= size
     * @throws NullPointerException if the specified array is null
     */
    public boolean addAll(int index, double[] elements) {
        rangeCheckForAdd(index);

        int numNew = elements.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount

        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                    numMoved);

        System.arraycopy(elements, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Inserts all the elements in the specified DoubleList into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the "index-order".
     *
     * @param index index at which to insert the first element from the
     *              specified DoubleList
     * @param list DoubleList containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException if the index >= size
     * @throws NullPointerException if the specified DoubleList is null
     */
    public boolean addAll(int index, DoubleList list) {
        rangeCheckForAdd(index);

        double[] elements = list.elementData;
        int numNew = list.size; // can't be elements.length
        ensureCapacityInternal(size + numNew);  // Increments modCount

        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                    numMoved);

        System.arraycopy(elements, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Inserts all the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c <tt>Double</tt> collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException if the index >= size
     * @throws NullPointerException if the specified collection is null or contains <tt>null</tt>
     * @throws ArrayStoreException if the runtime type of the element in the given collection is not <tt>Doubl
     */
    public boolean addAll(int index, Collection<Double> c) {
        rangeCheckForAdd(index);

        Double[] a = new Double[c.size()];
        c.toArray(a);
        double[] elements = doublize(a);
        int numNew = elements.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount

        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                    numMoved);

        System.arraycopy(a, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     */
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * A version of rangeCheck used by add and addAll.
     */
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     *
     * @param c collection containing elements to be removed from this list
     * @return {@code true} if this list changed as a result of the call
     * @throws NullPointerException or if the specified collection is null
     */
    public boolean removeAll(Collection<Double> c) {
        return batchRemove(c, false);
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection.  In other words, removes from this list all
     * of its elements that are not contained in the specified collection.
     *
     * @param c collection containing elements to be retained in this list
     * @return {@code true} if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    public boolean retainAll(Collection<Double> c) {
        return batchRemove(c, true);
    }

    private boolean batchRemove(Collection<Double> c, boolean complement) {
        final double[] elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try {
            for (; r < size; r++)
                if (c.contains(elementData[r]) == complement)
                    elementData[w++] = elementData[r];
        } finally {
            if (r != size) {
                System.arraycopy(elementData, r,
                        elementData, w,
                        size - r);
                w += size - r;
            }
            if (w != size) {
                modCount += size - w;
                size = w;
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public Iterator<Double> iterator() {
        return new Itr();
    }

    /**
     * An optimized version of AbstractList.Itr
     */
    private class Itr implements Iterator<Double> {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such
        int expectedModCount = modCount;

        Itr() {}

        public boolean hasNext() {
            return cursor != size;
        }

        public Double next() {
            checkForComodification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            double[] elementData = DoubleList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;
            return elementData[lastRet = i];
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                DoubleList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super Double> consumer) {
            Objects.requireNonNull(consumer);
            final int size = DoubleList.this.size;
            int i = cursor;
            if (i >= size) {
                return;
            }
            final double[] elementData = DoubleList.this.elementData;
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            while (i != size && modCount == expectedModCount) {
                consumer.accept(elementData[i++]);
            }
            // update once at end of iteration to reduce heap write traffic
            cursor = i;
            lastRet = i - 1;
            checkForComodification();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    /**
     * Sorts this list according to the natural order.
     */
    public void sort() {
        final int expectedModCount = modCount;
        Arrays.sort(elementData, 0, size);
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }

    /**
     * Returns a string representation of the DoubleList.
     *
     * @return a string representation of the DoubleList
     */
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        int iMax = size - 1;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; ; i++) {
            sb.append(elementData[i]);
            if (i == iMax)
                return sb.append(']').toString();
            sb.append(", ");
        }
    }

    /**
     * Returns the hash code value for this list.
     * This implementation is like {@code Arrays.hashCode(double[])}
     * 
     * @return the hash code value for this list
     */ 
    @Override
    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < size; i++) {
            long bits = Double.doubleToLongBits(elementData[i]);
            hashCode = 31 * hashCode + (int)(bits ^ (bits >>> 32));
        }
        return hashCode;
    }

    /**
     * Compares the specified object with this list for equality.
     * Returns {@code true} if and only if the specified object is also <tt>DoubleList</tt>, 
     * both lists have the same size, and all corresponding pairs of elements in
     * the two lists are <i>equal</i>.
     * 
     * @param o the object to be compared for equality with this list
     * @return {@code true} if the specified object is equal to this list
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof DoubleList)) {
            return false;
        }
        DoubleList another = (DoubleList) o;
        if (this.size == another.size) {
            for (int i = 0; i < size; i++) {
                if (this.elementData[i] != another.elementData[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
