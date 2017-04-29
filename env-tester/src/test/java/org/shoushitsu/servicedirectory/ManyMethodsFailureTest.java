package org.shoushitsu.servicedirectory;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManyMethodsFailureTest extends FailureTestBase<ManyMethodsFailureTest.Iface, ManyMethodsFailureTest.ImplBase> {

	interface Iface {
		void a();
		void b();
	}

	abstract class ImplBase extends ServiceDirectoryBase<Iface> implements Iface {
	}

	public ManyMethodsFailureTest() {
		super(Iface.class);
	}

	@Test
	void missingEnv() {
		currentImpl = new ImplBase() {
			@Override
			public void a() {
			}

			@Override
			public void b() {
				env().a();
				a();
			}
		};
		try {
			test();
			mustFail();
		} catch (AssertionFailedError e) {
			assertEquals("method a was invoked more than once by b", e.getMessage());
		} catch (Throwable t) {
			failedUnexpectedly(t);
		}
	}

	@Test
	void loop() {
		currentImpl = new ImplBase() {
			@Override
			public void a() {
				env().b();
			}

			@Override
			public void b() {
				env().a();
			}
		};
		try {
			test();
			mustFail();
		} catch (AssertionFailedError e) {
			assertTrue(e.getMessage().startsWith("init loop detected in method"));
		} catch (Throwable t) {
			failedUnexpectedly(t);
		}
	}

}
