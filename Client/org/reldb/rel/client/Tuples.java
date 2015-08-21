package org.reldb.rel.client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

public class Tuples extends Value implements Iterable<Tuple>{

	private ArrayBlockingQueue<Tuple> tuples = new ArrayBlockingQueue<Tuple>(250);
	private Heading heading = null;
	private String typeName = null;
	private LinkedList<Tuple> cache = null;
	
	public Tuples(String typeName) {
		this.typeName = typeName;
	}
	
	public Tuples(Heading heading) {
		this.heading = heading;
	}

	// Insert special end-of-set indicator tuple.
	void insertNullTuple() {
		addValue(new NullTuple(), false);
	}

	void addValue(Value value, boolean b) {
		try {
			tuples.put((Tuple)value);
		} catch (InterruptedException e) {
			System.out.println("Tuples: tuple write interrupted.");
		}
	}
	
	public Heading getHeading() {
		return heading;
	}

	public int toInt() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Tuples can't be cast to int.");
	}

	public long toLong() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Tuples can't be cast to long.");
	}

	public double toDouble() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Tuples can't be cast to double.");
	}

	public float toFloat() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Tuples can't be cast to float.");
	}

	public boolean toBoolean() throws InvalidValueException {
		throw new InvalidValueException("Tuples can't be cast to boolean.");
	}

	public String toString(int depth) {
		String lines = "";
		for (Tuple tuple: this)
			lines += ((lines.length() > 0) ? ",\n" : "") + "\t" + tuple.toString(depth + 1);
		return ((heading != null) ? heading : typeName) + " {\n" + lines + "}"; 
	}

	public String toString() {
		return toString(0);
	}
	
	private boolean done = false;

	public Iterator<Tuple> iterator() {
		if (cache != null)
			return cache.iterator();
		cache = new LinkedList<Tuple>();
		return new Iterator<Tuple>() {
			Tuple tuple = null;
			public boolean hasNext() {
				if (done)
					return false;
				if (tuple == null) {
					do
						try {
							tuple = tuples.take();
						} catch (InterruptedException e) {
							return false;
						}
					while (tuple == null);
					if (tuple.isNull()) {
						done = true;
						return false;
					}
				}
				return true;
			}
			public Tuple next() {
				if (!hasNext())
					return null;
				try {
					if (cache != null)
						cache.add(tuple);
					return tuple;
				} finally {
					tuple = null;
				}
			}
			public void remove() {
			}			
		};
	}
	
}
