package org.shoushitsu.servicedirectory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;

class DependencyTest {

	interface Env {
		int one();
		int two();
		int three();
		int five();
		
		int missingEnv();
	}

	class Impl extends ServiceDirectoryBase<Env> implements Env {
		@Override
		public int one() {
			mock.one();
			return 1;
		}

		@Override
		public int two() {
			mock.two();
			return env().one() * 2;
		}

		@Override
		public int three() {
			mock.three();
			return env().one() + env().two();
		}

		@Override
		public int five() {
			mock.five();
			return env().two() + env().three();
		}

		@Override
		public int missingEnv() {
			return env().one() + one();
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
	void test() {
		assertEquals(1, env.one());
		assertEquals(2, env.two());
		assertEquals(3, env.three());
		verify(mock).one();
	}

	@Test
	void testMissingEnv() {
		env.missingEnv();
		verify(mock, times(2)).one();
	}

	@Test
	void diamond() {
		assertEquals(5, env.five());
		InOrder inOrder = inOrder(mock);
		inOrder.verify(mock).five();
		inOrder.verify(mock).two();
		inOrder.verify(mock).one();
		inOrder.verify(mock).three();
		inOrder.verifyNoMoreInteractions();
	}

}
