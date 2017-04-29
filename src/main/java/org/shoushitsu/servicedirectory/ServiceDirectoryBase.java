package org.shoushitsu.servicedirectory;

public abstract class ServiceDirectoryBase<I> {

	private I env;

	protected ServiceDirectoryBase() {
	}

	final void setEnv(I env) {
		this.env = env;
	}

	public final I env() {
		return env;
	}

}
