package iguana.utils.logging;

public interface IguanaLogger {

	void log(String s);

    default void log(Object obj) {
        log(String.valueOf(obj));
    }
	
	void log(String s, Object arg);
	
	void log(String s, Object arg1, Object arg2);
	
	void log(String s, Object arg1, Object arg2, Object arg3);
	
	void log(String s, Object arg1, Object arg2, Object arg3, Object arg4);

    void log(String s, Object... args);

    IguanaLogger DEFAULT = new IguanaLogger() {
		
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
