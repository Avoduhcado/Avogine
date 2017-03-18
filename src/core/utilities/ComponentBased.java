package core.utilities;

import java.util.HashMap;

public interface ComponentBased<T> {

	public HashMap<Class<?>, T> getComponents();
	public void setComponents(HashMap<Class<?>, T> components);
	/**
	 * Access a component through it's class. Only one object of a given super class can be contained as a component, as such you
	 * should be passing in the component's parent class that directly descends from <code>T</code> to retrieve it. Passing in a direct
	 * subclass or a component that isn't contained in this object will cause it to return null.
	 * @param clazz
	 * @return
	 */
	public <U extends T> U getComponent(Class<U> clazz);
	public boolean hasComponent(Class<? extends T> clazz);
	public <U extends T> U addComponent(U component);
	public <U extends T> U removeComponent(Class<U> clazz);
	
}
