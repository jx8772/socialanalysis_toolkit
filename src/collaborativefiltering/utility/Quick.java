package collaborativefiltering.utility;

import collaborativefiltering.utility.StdRandom;

/**
 * Created with IntelliJ IDEA.
 * User: xiangji
 * Date: 10/12/13
 * Time: 8:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Quick {
    private Quick() {}

    private static int partition(Comparable[] a, int lo, int hi) {
        int i = lo;
        int j = hi + 1;
        Comparable v = a[lo];
        while (true) {

            //find item on lo to swap
            while(less(a[++i], v))
                if(i == hi)
                    break;

            //find item on hi to swap
            while(less(v, a[--j]))
                if(j == lo)
                    break;

            //check if pointer cross
            if(i >= j)
                break;

            exch(a, i, j);
        }

        //put partitioning item v at a[j]
        exch(a, lo, j);

        // now, a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
        return j;
    }

    //added by xiang
    private static int partition(int[] a, int lo, int hi) {
        int i = lo;
        int j = hi + 1;
        int v = a[lo];
        while (true) {

            //find item on lo to swap
            while(less(a[++i], v))
                if(i == hi)
                    break;

            //find item on hi to swap
            while(less(v, a[--j]))
                if(j == lo)
                    break;

            //check if pointer cross
            if(i >= j)
                break;

            exch(a, i, j);
        }

        //put partitioning item v at a[j]
        exch(a, lo, j);

        // now, a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
        return j;
    }

    //ArrayList<Comparable> doesn't work
    /*public static Comparable select(ArrayList<Comparable> l, int k) {
        Diagnosis[] a = (Diagnosis[])l.toArray();
        return (select(a, k));
    }*/

    public static Comparable select(Comparable[] a, int k) {
        if (k < 0 || k > a.length) {
            throw new IndexOutOfBoundsException("Selected element out of bounds");
        }
        //StdRandom.shuffle(a);
        int lo = 0, hi = a.length -1;
        while(hi > lo) {
            int i = partition(a, lo, hi);
            if(i > k)
                hi = i - 1;
            else if (i < k)
                lo = i + 1;
            else return a[i];
        }
        return a[lo];
    }

    //added by Xiang
    public static int select(int[] a, int k) {
        int[] aCopy = new int[a.length];
        for (int i = 0; i < aCopy.length; i++) {
            aCopy[i] = a[i];
        }

        if (k < 0 || k > a.length) {
            throw new IndexOutOfBoundsException("Selected element out of bounds");
        }
        //StdRandom.shuffle(a);
        int lo = 0, hi = aCopy.length -1;
        while(hi > lo) {
            int i = partition(aCopy, lo, hi);
            if(i > k)
                hi = i - 1;
            else if (i < k)
                lo = i + 1;
            else return i;
        }
        return lo;
    }

    //helper sorting function
    private static boolean less (Comparable v, Comparable w) {
        return (v.compareTo(w) < 0);
    }

    //exchange a[i] and a[j]
    private static void exch(Object[] a, int i, int j) {
        Object swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    //added by xiang
    private static void exch(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }
}
