package org.shoushitsu.servicedirectory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public final class ServiceDirectory implements InvocationHandler {

	@SuppressWarnings("unchecked")
	public static <I> I build(Class<I> iface, ServiceDirectoryBase<I> impl) {
		I proxy = (I) Proxy.newProxyInstance(
				impl.getClass().getClassLoader(),
				new Class[]{iface},
				new ServiceDirectory(impl)
		);
		impl.setEnv(proxy);
		return proxy;
	}


	private final Object impl;

	private final ConcurrentMap<String, Container> containerByMethodName = new ConcurrentHashMap<>();
	private final ConcurrentMap<Thread, Thread> blockerByWaiter = new ConcurrentHashMap<>();

	private static class Container {
		final CompletableFuture<Object> future = new CompletableFuture<>();
		Thread thread;

		Container() {
			thread = Thread.currentThread();
		}
	}

	private ServiceDirectory(Object impl) {
		this.impl = impl;
	}

	@Override
	public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getParameterCount() == 0) {
			String name = method.getName();
			AtomicReference<Container> newContainer = new AtomicReference<>();
			Container container = containerByMethodName.computeIfAbsent(name, __ -> {
				Container c = new Container();
				newContainer.set(c);
				return c;
			});
			if (container == newContainer.get()) {
				try {
					Object value = method.invoke(impl, args);
					container.future.complete(value);
					return value;
				} catch (IllegalAccessException | InvocationTargetException | ExceptionInInitializerError e) {
					ServiceDirectoryException sde = new ServiceDirectoryException(name, e);
					container.future.completeExceptionally(sde);
					throw sde;
				} finally {
					synchronized (container) {
						container.thread = null;
						container.notifyAll();
					}
				}
			} else {
				Thread currentThread = Thread.currentThread();
				if (!container.future.isDone()) {
					synchronized (container) {
						if (currentThread.equals(container.thread)) {
							throw new ServiceDirectoryException(name);
						} else if (container.thread != null) {
							Thread waiter = container.thread;
							while ((waiter = blockerByWaiter.get(waiter)) != null) {
								if (currentThread.equals(waiter)) {
									throw new ServiceDirectoryException(name);
								}
							}
							blockerByWaiter.put(currentThread, container.thread);
							try {
								do {
									container.wait();
								} while (container.thread != null);
							} finally {
								blockerByWaiter.remove(currentThread);
							}
						}
					}
				}
				return container.future.get();
			}
		} else {
			return method.invoke(impl, args);
		}
	}

}
