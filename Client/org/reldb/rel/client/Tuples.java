package org.reldb.rel.client;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

import org.reldb.rel.client.connection.stream.StreamReceiverClient;

public class Tuples extends Value implements Iterable<Tuple>{

	private ArrayBlockingQueue<Tuple> tuples = new ArrayBlockingQueue<Tuple>(50);
	private ArrayBlockingQueue<Heading> headingQueue = new ArrayBlockingQueue<Heading>(1);
	private Heading heading = null;
	private StreamReceiverClient client = null;
	private boolean cacheable = false;
	private LinkedList<Tuple> cache = null;

	Tuples(StreamReceiverClient client) {
		this.client = client;
	}
	
	Tuples(Heading heading) {
		this.heading = heading;
		cacheable = true;
	}
	
	protected void finalize() throws Throwable {
	    try {
	        close();
	    } finally {
	        super.finalize();
	    }
	}
	
	void setHeading(Heading heading) {
		try {
			headingQueue.put(heading);
		} catch (InterruptedException e) {
		}
	}

	// Insert special end-of-set indicator tuple.
	void insertNullTuple() {
		addValue(new NullTuple());
	}

	void addValue(Value value) {
		try {
			tuples.put((Tuple)value);
		} catch (InterruptedException e) {
		}
	}
	
	/** If you're not going to iterate Tuples to the end, you should explicitly invoke close() to close the connection
	 * to the DBMS. */
	public void close() {
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				System.out.println("Tuples: close failed: " + e);				
				e.printStackTrace();
			}
			client = null;
		}
	}
	
	public Heading getHeading() {
		if (heading != null)
			return heading;
		try {
			heading = headingQueue.take();
		} catch (InterruptedException e) {
		}
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

	public String toString() {
		String lines = "";
		for (Tuple tuple: this)
			lines += "\t" + tuple + "\n";				
		return "ARRAY " + heading + " {\n" + lines + "}"; 
	}

	private boolean done = false;

	public Iterator<Tuple> iterator() {
		if (cache != null)
			return cache.iterator();
		if (cacheable)
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
						close();
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
