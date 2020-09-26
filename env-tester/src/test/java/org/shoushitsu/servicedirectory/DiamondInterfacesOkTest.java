package org.shoushitsu.servicedirectory;

import org.junit.jupiter.api.Test;

class DiamondInterfacesOkTest extends ServiceDirectoryTest<DiamondInterfacesOkTest.Env> {

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

	DiamondInterfacesOkTest() {
		super(Env.class);
	}

	@Override
	protected ServiceDirectoryBase<Env> getImplementation() {
		return new Impl();
	}

	@Test
	public void test() {
		super.test();
	}

}
