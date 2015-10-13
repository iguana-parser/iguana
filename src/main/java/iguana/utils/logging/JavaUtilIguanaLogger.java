package iguana.utils.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaUtilIguanaLogger implements IguanaLogger {
	
	private final Logger log;
    private final Level level;

    public JavaUtilIguanaLogger(String name) {
        this(name, LogLevel.INFO);
    }

    public JavaUtilIguanaLogger(String name, LogLevel level) {
        this.level = getLevel(level);
        log = Logger.getLogger(name);
        log.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        log.setLevel(this.level);
        handler.setLevel(this.level);
        handler.setFormatter(new IguanaLogFormatter());
        log.addHandler(handler);
    }

    private static Level getLevel(LogLevel l) {

        switch (l) {
            case INFO:    return Level.INFO;
            case DEBUG:   return Level.FINEST;
            case WARNING: return Level.WARNING;
            case ERROR:   return Level.SEVERE;
        }

        throw new RuntimeException("Unknown level " + l);
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
