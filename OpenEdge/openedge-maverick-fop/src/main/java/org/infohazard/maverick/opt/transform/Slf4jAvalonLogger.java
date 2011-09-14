package org.infohazard.maverick.opt.transform;

import org.apache.avalon.framework.logger.Logger;
import org.slf4j.LoggerFactory;

/**
 * Avalon wrapper for Slf4j logging framework used by (old versions of) FOP and
 * Batik.
 */
public class Slf4jAvalonLogger implements Logger {
	private final String name;

	private final org.slf4j.Logger logger;

	public Slf4jAvalonLogger(org.slf4j.Logger logger) {
		this.logger = logger;
		this.name = logger.getName();
	}

	public Slf4jAvalonLogger(Class<?> context) {
		this.logger = LoggerFactory.getLogger(context);
		this.name = context.getName();
	}

	public Slf4jAvalonLogger(String name) {
		this.logger = LoggerFactory.getLogger(name);
		this.name = name;
	}

	@Override
	public void debug(String message) {
		logger.debug(message);
	}

	@Override
	public void debug(String message, Throwable throwable) {
		logger.debug(message, throwable);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void info(String message) {
		logger.info(message);
	}

	@Override
	public void info(String message, Throwable throwable) {
		logger.info(message, throwable);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public void warn(String message) {
		logger.warn(message);
	}

	@Override
	public void warn(String message, Throwable throwable) {
		logger.warn(message, throwable);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public void error(String message) {
		logger.error(message);
	}

	@Override
	public void error(String message, Throwable throwable) {
		logger.error(message, throwable);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public void fatalError(String message) {
		logger.error(message);
	}

	@Override
	public void fatalError(String message, Throwable throwable) {
		logger.error(message, throwable);
	}

	@Override
	public boolean isFatalErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public Logger getChildLogger(String childName) {
		if (childName == null || childName.isEmpty())
			throw new IllegalArgumentException(
					"Child name must have a non-empty value");
		return new Slf4jAvalonLogger(this.name + "." + childName);
	}
}
