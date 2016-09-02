package log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hunterhug on 16-9-2.
 * logback测试
 */
public class LogHelp {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        Logger logger = new LogHelp().logger;
        logger.debug("输出debug级别的日志.....");
        logger.info("输出info级别的日志.....");
        logger.error("输出error级别的日志.....");
        logger.trace("dd");
    }
}


