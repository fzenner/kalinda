package com.kewebsi.util;

import com.kewebsi.errorhandling.CodingErrorException;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;

public class ListUtils {

    /**
     * SPEEDUP: Use only the fast method below
     * This is a very slow implementation. Under the assumptions that the order of elements in both lists are the same,
     * a much more efficient implementation is possible. (That implementation would have linear complexity.)
     * Compares two lists. The algorithm is customized from tracking changes on a GUI.
     * Unlike standard comparison, we assume that the elements that are in both lists are in the same
     * order.
     * @param oldList
     * @param newList
     * @param addedElms null if empty
     * @param removedElms null if empty
     * @return true, if there was a change, false if if not
     * @param <T>
     */

    public static <T> boolean getListListDiffs(ArrayList<T> oldList, ArrayList<T> newList, Out<ArrayList<T>> addedElms, Out<ArrayList<T>> removedElms, Out<ArrayList<Integer>> newPositions) {

        // SPEEDUP Activate the following lines.
//        if (areEqual(oldList, newList)) {
//            return false;
//        } else {
//          return true;
//        }


        var oldListCopy = new ArrayList<>(oldList);
        var newListCopy = new ArrayList<>(newList);
        newListCopy.removeAll(oldList);  // Leaves the added items in the list.
        oldListCopy.removeAll(newList);  // Leaves the removed items in the list.
        addedElms.set(newListCopy);
        removedElms.set(oldListCopy);

        // Check our fast implementation
        ArrayList<T> addedCheck = null;
        ArrayList<T> removedCheck = null;

        var checkLists = getListListDiffsFast(oldList, newList);
        if (checkLists != null) {
            addedCheck = checkLists.getValue0();
            removedCheck = checkLists.getValue1();
            newPositions.set(checkLists.getValue2());

        }


        if (! areEqual(addedElms.get(), addedCheck )) {
            throw new CodingErrorException("List check failed on added elements.");
        }

        if (! areEqual(removedElms.get(), removedCheck)) {
            throw new CodingErrorException("List check failed on removed elements.");
        }


        if (addedElms.isNull() && removedElms.isNull()) {
            return false;
        } else {
            return true;
        }


    }

    /**
     * Compares two lists. This function assumes, that the order of elements that are present in both lists (neiter
     * removed nor added) are in the same order in both lists. Otherwise the result is nonsensical.
     * This funciton is performance optimized for lists that are typically the same or have very few differences.
     * @param oldList
     * @param newList
     * @return Triple: Added elements, removed elements and the polsitions of the added elementes. Null if not at least one added or one removed element exists.
     * (for performance reasons).
     * @param <T>
     */
    public static <T> Triplet<ArrayList<T>, ArrayList<T>, ArrayList<Integer>> getListListDiffsFast(ArrayList<T> oldList, ArrayList<T> newList) {

        ArrayList<T> addedElms = null;
        ArrayList<T> removedElms = null;
        ArrayList<Integer> newPositions = null;


        int searchStartIdx = 0;
        for (T oldRun : oldList) {
            int foundIdx = locate(oldRun, newList, searchStartIdx);
            if (foundIdx < 0) {
                removedElms = initAdd(removedElms, oldRun);
            } else {
                searchStartIdx = foundIdx+1;
            }
        }

        //
        // Go through the new list. Each element in the new list that is not also an element in the old list is an
        // added (new) element.
        //
        searchStartIdx = 0;
        int newPos = 0;
        for (T newRun : newList) {
            int foundIdx = locate(newRun, oldList, searchStartIdx);
            if (foundIdx < 0) {
                addedElms = initAdd(addedElms, newRun);
                newPositions = initAdd(newPositions, newPos);
            } else {
                searchStartIdx = foundIdx+1;
            }
            newPos++;
        }

        // For performance reason, we return null if there are no changed elements (which is the typical case)
        if (addedElms != null || removedElms != null) {
            return new Triplet<>(addedElms, removedElms, newPositions);
        } else {
            return null;
        }
    }

    public static <T> ArrayList<T> initAdd(ArrayList<T> arrayList, T toAdd) {
        if (arrayList == null) {
            arrayList = new ArrayList<T>();
        }
        arrayList.add(toAdd);
        return arrayList;
    }


    public static <T> int locate(T tag, ArrayList<T> list, int startSearchIdx) {
        for (int idx = startSearchIdx; idx < list.size(); idx ++) {
            if (list.get(idx).equals(tag)) {
                return idx;
            }
        }
        return -1;
    }


    public static <T> boolean areEqual(ArrayList<T> listA, ArrayList<T> listB) {

        if (listA == null) {
            if (listB == null) {
                return true;
            } else {
                if (listB.size() == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            if (listA.size() == 0) {
                if (listB == null) {
                    return true;
                }
            } else {
                if (listB == null) {
                    return false;
                }
            }
        }

        // When here, we know that neither listA nor listB are null.
        if (listA.size() != listB.size()) {
            return false;
        }
        int size = listA.size();
        for (int i=0; i<size; i++) {
            if (! listA.get(i).equals(listB.get(i))) {
                return false;
            }
        }
        return true;
    }


}
