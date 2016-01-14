package bGLOOP.log;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class MultiThreadFormatter extends SimpleFormatter {

    // format string for printing the log record

    /**
     * Format the given LogRecord.
     * <p>
     * The formatting can be customized by specifying the
     * <a href="../Formatter.html#syntax">format string</a>
     * in the <a href="#formatting">
     * {@code java.util.logging.SimpleFormatter.format}</a> property.
     * The given {@code LogRecord} will be formatted as if by calling:
     * <pre>
     *    {@link String#format String.format}(format, date, source, logger, level, message, thrown, threadName);
     * </pre>
     * For details, please see {@link SimpleFormatter#format(LogRecord)}
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record) {
    	
        return "Thread: " + Thread.currentThread().getId() + " " + super.format(record);
    }

}
