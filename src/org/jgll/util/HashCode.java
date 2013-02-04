package org.jgll.util;

public class HashCode {

//	public static int hashCode(int...keys) {
//		int hash = 17;
//		for(int key : keys) {
//			hash += hash * 31 + key ;
//		}
//		return hash;
//	}
	
	public static int hashCode(Object...keys) {
		int hash = 17;
		for(Object key : keys) {
			hash += hash * 31 + key.hashCode() ;
		}
		return hash;		
	}
	
	public static int hashCode(int...keys) {
		int hash = 17;
		for(int key : keys) {
			hash += hash * 31 + key ;
		}
		return hash;
//		return (int) (hash * 2654435769L);
	}
	
	
	
}
