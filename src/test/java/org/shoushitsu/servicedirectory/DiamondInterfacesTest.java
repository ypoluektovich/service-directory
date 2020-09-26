package org.shoushitsu.servicedirectory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiamondInterfacesTest {

	interface Base {
		String base();
	}

	interface Left extends Base {
		String left();
	}

	interface Right extends Base {
		String right();
	}

	interface Env extends Left, Right {
		String self();
	}

	static class Impl extends ServiceDirectoryBase<Env> implements Env {

		@Override
		public String base() {
			return "base";
		}

		@Override
		public String left() {
			return env().base() + " left";
		}

		@Override
		public String right() {
			return env().base() + " right";
		}

		@Override
		public String self() {
			return String.format("%s,%s,%s", env().base(), env().left(), env().right());
		}

	}

	Env env;

	@BeforeEach
	void beforeEach() {
		env = ServiceDirectory.build(Env.class, new Impl());
	}

	@Test
	void base() {
		assertEquals("base", env.base());
	}

	@Test
	void left() {
		assertEquals("base left", env.left());
	}

	@Test
	void right() {
		assertEquals("base right", env.right());
	}

	@Test
	void self() {
		assertEquals("base,base left,base right", env.self());
	}

}
