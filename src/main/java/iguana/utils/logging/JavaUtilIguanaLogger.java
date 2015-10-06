package iguana.utils.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaUtilIguanaLogger implements IguanaLogger {
	
	private static Logger log = Logger.getLogger(JavaUtilIguanaLogger.class.getName());
    private Level level = Level.SEVERE;

    public JavaUtilIguanaLogger() {
        log.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        log.setLevel(level);
        handler.setLevel(level);
        handler.setFormatter(new IguanaLogFormatter());
        log.addHandler(handler);
    }

    @Override
    public void setLevel(LogLevel l) {

        switch (l) {

            case INFO:
                level = Level.INFO;
                break;

            case DEBUG:
                level = Level.FINEST;
                break;

            case WARNING:
                level = Level.WARNING;
                break;

            case ERROR:
                level = Level.SEVERE;
                break;
        }
    }

    @Override
	public void log(String s) {
		log.log(level, s);
	}

    @Override
	public void log(String s, Object... args) {
		log.log(level, String.format(s, args));
	}

	@Override
	public void log(String s, Object arg) {
		log.log(level, String.format(s, arg));
	}

	@Override
	public void log(String s, Object arg1, Object arg2) {
		log.log(level, String.format(s, arg1, arg2));
	}

	@Override
	public void log(String s, Object arg1, Object arg2, Object arg3) {
		log.log(level, String.format(s, arg1, arg2, arg3));
	}

	@Override
	public void log(String s, Object arg1, Object arg2, Object arg3, Object arg4) {
		log.log(level, String.format(s, arg1, arg2, arg3, arg4));
	}

}
