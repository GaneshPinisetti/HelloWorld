package com.invista.file.util;

import java.util.StringTokenizer;

public class TestingSpaces {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String space="091234567891234			|/Trash Bin/Sairam   Rapolu/Ganesh";
		StringTokenizer st = new StringTokenizer(space, "|");
		String[] arr=new String[10];
		int i=0;
		while (st.hasMoreTokens()) {
			arr[i]=st.nextToken().trim();
			System.out.println("Mydata : "+st.nextToken());
			i++;
	
		}
		for(int j=0;j<arr.length;j++)
		{
			System.out.println("is data "+arr[j]);
		}

	}

}
