package org.shoushitsu.servicedirectory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;

class SingleInitializationTest {

	private static final String VAL = "val";

	interface Env {
		String val();
	}

	class Impl extends ServiceDirectoryBase<Env> implements Env {
		@Override
		public String val() {
			mock.val();
			return VAL;
		}
	}

	Env mock;
	Impl impl;
	Env env;

	@BeforeEach
	void beforeEach() {
		mock = mock(Env.class);
		impl = new Impl();
		env = ServiceDirectory.build(Env.class, impl);
	}

	@AfterEach
	void afterEach() {
		validateMockitoUsage();
	}

	@Test
	void oneInvocation() {
		assertEquals(VAL, env.val());
		verify(mock).val();
	}

	@Test
	void twoInvocations() {
		assertEquals(VAL, env.val());
		assertEquals(VAL, env.val());
		verify(mock).val();
	}

}