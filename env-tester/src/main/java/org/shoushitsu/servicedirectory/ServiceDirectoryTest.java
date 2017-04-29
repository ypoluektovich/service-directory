package org.shoushitsu.servicedirectory;

import org.mockito.Mockito;
import org.opentest4j.AssertionFailedError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.mockito.Mockito.verify;

public abstract class ServiceDirectoryTest<I> {

	private final Class<I> iface;

	private ServiceDirectoryBase<I> spy;
	private I env;

	protected ServiceDirectoryTest(Class<I> iface) {
		this.iface = iface;
	}

	protected abstract ServiceDirectoryBase<I> getImplementation();

	protected void test() {
		for (Method testedMethod : iface.getMethods()) {
			if (testedMethod.getParameterCount() != 0) {
				continue;
			}
			initialize();
			try {
				testedMethod.invoke(env);
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof ServiceDirectoryException && e.getCause().getCause() instanceof InvocationTargetException) {
					Throwable cause = e.getCause().getCause().getCause();
					while (cause != null) {
						if (cause instanceof ServiceDirectoryException && cause.getMessage().startsWith("init loop detected")) {
							throw new AssertionFailedError(
									cause.getMessage(),
									e.getCause().getCause().getCause()
							);
						}
						cause = cause.getCause();
					}
					throw new AssertionFailedError(
							"method threw an exception: " + testedMethod.getName(),
							e.getCause().getCause().getCause()
					);
				}
			} catch (IllegalAccessException e) {
				throw new AssertionFailedError("failed to invoke " + testedMethod.getName(), e);
			}

			for (Method dependencyMethod : iface.getMethods()) {
				if (dependencyMethod.getParameterCount() != 0) {
					continue;
				}
				try {
					dependencyMethod.invoke(verify(spy, Mockito.atMost(1)));
				} catch (IllegalAccessException e) {
					throw failedToCheck(testedMethod, dependencyMethod, e);
				} catch (InvocationTargetException e) {
					Throwable cause = e.getCause();
					if (cause instanceof AssertionError) {
						throw new AssertionFailedError(
								String.format(
										"method %s was invoked more than once by %s",
										dependencyMethod.getName(),
										testedMethod.getName()
								),
								cause
						);
					}
					throw failedToCheck(testedMethod, dependencyMethod, cause);
				}
			}
		}
	}

	private void initialize() {
		spy = Mockito.spy(getImplementation());
		env = ServiceDirectory.build(iface, spy);
	}

	private static AssertionFailedError failedToCheck( Method testedMethod, Method dependencyMethod, Throwable e) {
		return new AssertionFailedError(
				String.format(
						"failed to check invocations of %s by %s",
						dependencyMethod.getName(),
						testedMethod.getName()
				),
				e
		);
	}

}
