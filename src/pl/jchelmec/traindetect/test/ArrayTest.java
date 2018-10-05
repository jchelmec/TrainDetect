package pl.jchelmec.traindetect.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ArrayTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			Double[] dane = new Double[5];
			dane[0]=5.6;
			dane[1]=8.4;
			dane[2]=3.7;
			dane[3]=2.8;
			dane[4]=4.9;
			
			List<Double[]> danelista = new ArrayList<>();
			
			for (int i=0;i<5;i++) {
				for (int n=0;n<dane.length-1;n++) {
					dane[n] = dane[n] * i;
				}
				
				danelista.add(dane.clone());
				
			}
			
			Iterator<Double[]> it = danelista.iterator();
			
			Double[] a= it.next();
			Double[] b = it.next();
			System.out.println(it.next()[2]);
			System.out.println(it.next()[3]);
	}

}
