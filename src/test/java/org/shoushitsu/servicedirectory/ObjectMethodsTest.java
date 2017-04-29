package org.shoushitsu.servicedirectory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ObjectMethodsTest {

	interface Env {
	}

	static class Impl extends ServiceDirectoryBase<Env> implements Env {
	}

	private static Env mk() {
		return ServiceDirectory.build(Env.class, new Impl());
	}

	@Test
	void create() {
		assertNotNull(mk());
	}

	// todo: test toString, equals, hashCode

}