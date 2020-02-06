package com.invista.file.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class StrEX {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		FileReader fr=new FileReader("E:\\Ganesh Pinisetti\\java\\Testing.txt");    
        BufferedReader br=new BufferedReader(fr);    

        int i;   
        String line;
        
        while((line = br.readLine()) != null){  
        System.out.print(line); 
        System.out.println("\r\n");
        StringTokenizer st=new StringTokenizer(line,"	");
        int arrtCount = 0;
		String data[] = new String[st.countTokens()];
        String str="Pinisetti	";
        if(st.equals(str)) {
    		System.out.println("my code in equals method");
    	}else {
			System.out.println("error in logic");
		}
        while(st.hasMoreTokens()) {
        	data[arrtCount] = st.nextToken();
			arrtCount++;
			int a;
			String[] words=data[0].split("|");
			String w=words.toString();
        	System.out.println(w);
        	
        }
        }  
        br.close();    
        fr.close();    

	}

}
