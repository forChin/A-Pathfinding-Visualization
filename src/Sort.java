import java.util.*;

/* Sort.java реализует быструю сортировку 
 * и сортировку пузырьком (сортирует от 
 * наименьшего элемента к наибольшему). Это нужно для того, 
 * чтобы найти наименьший F-cost при выполнении Pathfinding algorithm. 
 * 
 * Используется, по понятным причинам, только быстрая сортировка, 
 * но можно поменять и на сортировку пузырьком.
 **/

public class Sort {
	
	public static void quickSort(ArrayList<Node> array, int low, int high) {
        if (array.size() == 0)
            return;
 
        if (low >= high)
            return;
 
        int middle = low + (high - low) / 2;
        int pivot = array.get(middle).getF();
 
        int i = low, j = high;
        while (i <= j) {
            while (array.get(i).getF() < pivot) {
                i++;
            }
 
            while (array.get(j).getF() > pivot) {
                j--;
            }
 
            if (i <= j) {
                Node temp = array.get(i);
                array.add(i, array.get(j));
                array.remove(i+1);
                array.add(j, temp); 
                array.remove(j+1);
                i++;
                j--;
            }
        }
 
        if (low < j)
            quickSort(array, low, j);
 
        if (high > i)
            quickSort(array, i, high);
    }
	
	public static void bubbleSort(ArrayList<Node> array) {
		for (int i = 0; i < array.size(); i++) 
			for (int j = array.size()-1; j > i; j--) {
				int prevF = array.get(j-1).getF();
				int currentF = array.get(j).getF();
				
				if (currentF < prevF) {
					Node temp = array.get(j-1);
					array.remove(j-1);
					array.add(j, temp);
				}
			}
	}
}
