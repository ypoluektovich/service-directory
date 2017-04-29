package org.shoushitsu.servicedirectory;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OneMethodFailureTest extends FailureTestBase<OneMethodFailureTest.Iface, OneMethodFailureTest.ImplBase> {

	interface Iface {
		void method();
	}

	abstract class ImplBase extends ServiceDirectoryBase<Iface> implements Iface {
	}

	OneMethodFailureTest() {
		super(Iface.class);
	}

	@Test
	void throwsException() {
		currentImpl = new ImplBase() {
			@Override
			public void method() {
				throw new RuntimeException("derp");
			}
		};
		try {
			test();
			mustFail();
		} catch (AssertionFailedError e) {
			assertEquals("method threw an exception: method", e.getMessage());
			assertTrue(e.getCause() instanceof RuntimeException);
			assertEquals("derp", e.getCause().getMessage());
		} catch (Throwable t) {
			failedUnexpectedly(t);
		}
	}

}
