package org.shoushitsu.servicedirectory;

import static org.junit.jupiter.api.Assertions.fail;

abstract class FailureTestBase<I, T extends ServiceDirectoryBase<I>> extends ServiceDirectoryTest<I> {

	T currentImpl;

	FailureTestBase(Class<I> iface) {
		super(iface);
	}

	@Override
	protected final T getImplementation() {
		return currentImpl;
	}


	static void mustFail() {
		fail("test must have failed");
	}

	static void failedUnexpectedly(Throwable t) {
		fail("test failed in an unexpected way", t);
	}

}
