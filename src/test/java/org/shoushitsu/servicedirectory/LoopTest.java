package org.shoushitsu.servicedirectory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class LoopTest {

	interface Env {
		int self();

		int a();
		int b();
	}

	class Impl extends ServiceDirectoryBase<Env> implements Env {

		@Override
		public int self() {
			return env().self() + 1;
		}

		@Override
		public int a() {
			return env().b() + 1;
		}

		@Override
		public int b() {
			return env().a() + 1;
		}
		
	}

	Env env;

	@BeforeEach
	void beforeEach() {
		env = ServiceDirectory.build(Env.class, new Impl());
	}

	@Test
	void tightLoop() {
		try {
			env.self();
			fail("self() did not throw");
		} catch (ServiceDirectoryException e) {
			assertEquals("self", e.getMethodName());
		}
	}

	@Test
	void longLoop() {
		try {
			env.a();
			fail("a() did not throw");
		} catch (ServiceDirectoryException e) {
			assertEquals("a", e.getMethodName());
		}
	}

}
