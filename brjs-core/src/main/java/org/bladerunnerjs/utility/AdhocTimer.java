package org.bladerunnerjs.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
/*
 * Just a little utilty to make it easier to time methods
 */
public class AdhocTimer {

	public static long initTime;
	public static StringBuffer stacks = new StringBuffer();
	
	private static Map<String, FuncDetail> funcDetails = null;
	
	public static void init(){
		initTime = System.nanoTime();
		funcDetails = new HashMap<String, FuncDetail>();
		stacks.setLength(0);
	}
	
	public static void enter(String name){
		enter(name, true);
	}
	
	public static void enter(String name, boolean display){
		if(funcDetails == null){
			init();
		}
		
		
		if(funcDetails.get(name) == null){
			funcDetails.put(name, new FuncDetail(initTime, name, display));
		}else{
			funcDetails.get(name).start(display);
		}
	}
	
	
	public static void dump(){
		System.out.println("==== Dump START ====");
		
		Set<Entry<String, FuncDetail>> entrySet = funcDetails.entrySet();
		for(Entry<String, FuncDetail> entry : entrySet){
			FuncDetail value = entry.getValue();
			System.out.println(value.name + ":" + value.total/1000000 + ":" + value.count);
		}
		
		System.out.println("==== Dump END =====");
	}
	
	public static String deStack(){
		return stacks.toString();
	}
	
	public static void stack(){
		try{
			throw new Exception();
		}catch(Exception e){
			
			append("======== Start Dump =========\n");
			StackTraceElement[] stackTrace = e.getStackTrace();
			for(int i = 1; i < (stackTrace.length - 33); i++){
				StackTraceElement el = stackTrace[i];
				append(el.toString());
				append("\n");
			}
		}
	}
	
	public static void exit(String name){
		funcDetails.get(name).finish(true);
	}
	
	public static void exit(String name, boolean display){
		funcDetails.get(name).finish(display);
	}

	public static void append(String stuff) {
		//System.out.print(stuff);
		stacks.append(stuff);
		
	}
}

class FuncDetail{
		long count = 0;
		long total = 0;
		long start = System.nanoTime();
		String name = "";
		private static long million = 1000000;
		
		FuncDetail(long initTime, String name){
			this.name = name;
			this.start(false);
		}
		
		FuncDetail(long initTime, String name, boolean display){
			this.name = name;
			this.start(display);
		}
		
		public void start(boolean display){
			start =  System.nanoTime();
			long delta = start - AdhocTimer.initTime;
			if(display == true){
				System.out.println(name  + " START: " + delta/million);
			}
		}
		
		public long finish(boolean display){
			long now = System.nanoTime();
			long delta = now - AdhocTimer.initTime;
			count++;
			long duration = now - start;
			total += duration;
			if(display == true){
				System.out.println(name  + " FINISH: " + delta/million + " DURATION: " + duration/million);
			}
			return duration;
		}
}
	
	
