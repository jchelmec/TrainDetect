package pl.jchelmec.traindetect.test;

import java.util.ArrayList;
import java.util.Arrays;
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
			
			Vector<Double[]> danelista = new Vector<>();
			
			for (int i=0;i<5;i++) {
				danelista.add(dane);
				
			}
			
			System.out.println(danelista.get(1)[2]);
	}

}
