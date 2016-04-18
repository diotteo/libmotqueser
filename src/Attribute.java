package ca.dioo.java.libmotqueser;

public class Attribute<N,V> {
	private N name;
	private V value;

	public Attribute(N name, V value) {
		setName(name);
		setValue(value);
	}

	public void setName(N name) {
		this.name = name;
	}

	public N getName() {
		return name;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public V getValue() {
		return value;
	}
}
