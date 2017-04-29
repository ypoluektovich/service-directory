package org.shoushitsu.servicedirectory;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Description;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

import java.util.concurrent.atomic.AtomicInteger;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

@JCStressTest
@Description("test two racing initializations")
@Outcome(id = "1", expect = ACCEPTABLE, desc = "initialization called only once")
@Outcome(expect = FORBIDDEN, desc = "initialization called more than once")
class ConcurrentAccessTest {

	interface Env {
		int val();
	}

	static class Impl extends ServiceDirectoryBase<Env> implements Env {

		private final AtomicInteger counter;

		Impl(AtomicInteger counter) {
			this.counter = counter;
		}

		@Override
		public int val() {
			return counter.incrementAndGet();
		}
	}

	@State
	public static class TestState {
		final AtomicInteger counter = new AtomicInteger();
		final Env env = ServiceDirectory.build(Env.class, new Impl(counter));
	}

	@Actor
	public void actor1(TestState state) {
		state.env.val();
	}

	@Actor
	public void actor2(TestState state) {
		state.env.val();
	}

	@Arbiter
	public void arbiter(TestState state, I_Result result) {
		result.r1 = state.counter.get();
	}

}