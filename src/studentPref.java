import java.io.*;
import java.util.*;

public class studentPref{
	public String name;
	int [] preferences;
	int origRows;
	int origCols;
	
	
	
	public studentPref(String _name, int [][] _pref){
		name = _name;
		origRows = _pref.length;
		origCols = _pref[0].length;
		int size = origRows * origCols;
		preferences = new int[size];
		for(int i = 0; i < _pref.length; i++){
			for (int j = 0; j < _pref[i].length; j++){
				preferences[i*7 + j] = _pref[i][j];
			}
		}
	}
	
	
	public int [] getAllPref(){
		return this.preferences;
	}
	public int pref(int index){
		return this.preferences[index];
	}
	public String getName(){
		return this.name;
	}
	public void print(){
		//print everything for debugging purposes
		System.out.println(name);
		for (int i = 0; i < origRows; i++){
			for (int j = 0; j < origCols; j++){
				System.out.print(preferences[i*7 + j] + " ");
			}
			System.out.println();
		}
	}
	
}