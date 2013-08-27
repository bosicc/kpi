package com.kpi.android.spedule;

public class Sorter {
	public static void quickSort(SheduleItem Ar[]) {
        int startIndex = 0;
        int endIndex = Ar.length - 1;
        doSort(Ar, startIndex, endIndex);
    }

    private static void doSort(SheduleItem Ar[], int start, int end) {
        if (start >= end)
            return;
        int i = start, j = end;
        int cur = i - (i - j) / 2;
        while (i < j) {
            while (i < cur && (Ar[i].startDate.getTime() <= Ar[cur].startDate.getTime())) {
                i++;
            }
            while (j > cur && (Ar[cur].startDate.getTime() <= Ar[j].startDate.getTime())) {
                j--;
            }
            if (i < j) {
            	SheduleItem temp = Ar[i];
                Ar[i] = Ar[j];
                Ar[j] = temp;
                if (i == cur)
                    cur = j;
                else if (j == cur)
                    cur = i;
            }
        }
        doSort(Ar, start, cur);
        doSort(Ar, cur+1, end);
    }
}
