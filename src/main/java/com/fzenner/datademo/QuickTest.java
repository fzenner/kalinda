package com.fzenner.datademo;

import com.fzenner.datademo.entity.CustomerAssistant;
import com.fzenner.datademo.entity.ReservationAssistant;
import com.kewebsi.util.ListUtils;
import com.kewebsi.util.Out;

import java.util.ArrayList;
import java.util.Arrays;

public class QuickTest {
	

	
	public static void main(String[] args) {

//		String createStatement = CustomerAssistant.getGlobalInstance().getDbCreateStatement();
//		System.out.println(createStatement);

		Integer[] arrA = {88, 1,2, 99, 3,4, 888};
		Integer[] arrB = {5,6,7};



		var x =  Arrays.asList(arrA);

		var arrlA = new ArrayList(Arrays.asList(arrA));
		var arrlB = new ArrayList(Arrays.asList(arrB));

		Out<ArrayList<Integer>> added = new Out<>();
		Out<ArrayList<Integer>> removed = new Out<>();
		Out<ArrayList<Integer>> newPositions = new Out<>();

		ListUtils.getListListDiffs(arrlA, arrlB, added, removed, newPositions);

		System.out.println("Added:");
		printList(added.get());

		System.out.println("Positions:");
		printList(newPositions.get());


		System.out.println("Removed:");
		printList(removed.get());
	}

	public static <T> void printList(ArrayList<T> list) {
		if (list == null) {
			System.out.println("null");
		} else {
			for (T run : list) {
				System.out.println(run);
			}
		}
	}
}
