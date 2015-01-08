package org.reldb.rel.storage;

import org.reldb.rel.exceptions.ExceptionFatal;

import com.sleepycat.je.*;

public class RelTransaction {
	
	private int referenceCount;
	private Transaction transaction;
	private boolean aborting;
	private boolean aborted;
	
	public RelTransaction(Transaction txn) {
		transaction = txn;
		referenceCount = 1;
		aborting = false;
		aborted = false;
//		System.out.println("TRANSACTION: start: " + transaction);
	}
	
	public Transaction getTransaction() {
		return transaction;
	}
	
	void addReference() {
		referenceCount++;
	}
	
	int getReferenceCount() {
		return referenceCount;
	}
	
	void abort() {
		aborting = true;
		if (--referenceCount > 0)
			return;
		try {
			transaction.abort();
			aborted = true;
//			System.out.println("TRANSACTION: abort: " + transaction);
		} catch (DatabaseException de) {
			throw new ExceptionFatal("RS0365: abort failed: " + de);
		}
	}
	
	void commit() {
		if (--referenceCount > 0)
			return;
		try {
			if (aborting && !aborted) {
				transaction.abort();
				aborted = true;
//				System.out.println("TRANSACTION: abort: " + transaction);
			}
			else {
				transaction.commit();
//				System.out.println("TRANSACTION: commit: " + transaction);
			}
		} catch (DatabaseException de) {
			throw new ExceptionFatal("RS0366: commit failed: " + de);
		}
	}
	
	public String toString() {
		return transaction.toString();
	}
}
