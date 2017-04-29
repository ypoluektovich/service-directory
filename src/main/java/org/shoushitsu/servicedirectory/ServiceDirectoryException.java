package org.shoushitsu.servicedirectory;

public final class ServiceDirectoryException extends RuntimeException {

	private final String methodName;

	ServiceDirectoryException(String methodName) {
		super("init loop detected in method " + methodName);
		this.methodName = methodName;
	}

	ServiceDirectoryException(String methodName, Throwable cause) {
		super(cause);
		this.methodName = methodName;
	}

	public final String getMethodName() {
		return methodName;
	}
	
}
