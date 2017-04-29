package org.shoushitsu.servicedirectory;

import org.junit.jupiter.api.Test;

class OkTest extends ServiceDirectoryTest<OkTest.Ok> {

	interface Ok {
		void a();
		void b();
		void c();
		void d();
	}

	static class OkImpl extends ServiceDirectoryBase<Ok> implements Ok {
		@Override public void a() { }
		@Override public void b() { env().a(); }
		@Override public void c() { env().a(); }
		@Override public void d() { env().b(); env().c(); }
	}

	OkTest() {
		super(Ok.class);
	}

	@Override
	protected ServiceDirectoryBase<Ok> getImplementation() {
		return new OkImpl();
	}

	@Test
	public void test() {
		super.test();
	}

}