package org.iguana.util.logging;

public interface ParserLogger {

	public void log(String s);
	
	public void log(String s, Object...args);
	
	public void log(String s, Object arg);
	
	public void log(String s, Object arg1, Object arg2);
	
	public void log(String s, Object arg1, Object arg2, Object arg3);
	
	public void log(String s, Object arg1, Object arg2, Object arg3, Object arg4);

	public static ParserLogger DEFAULT = new ParserLogger() {
		
		@Override
		public void log(String s, Object arg1, Object arg2, Object arg3, Object arg4) {}
		
		@Override
		public void log(String s, Object arg1, Object arg2, Object arg3) {}
		
		@Override
		public void log(String s, Object arg1, Object arg2) {}
		
		@Override
		public void log(String s, Object arg) {}
		
		@Override
		public void log(String s, Object... args) {}
		
		@Override
		public void log(String s) {}		
	};
	
}
