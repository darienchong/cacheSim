package org.snajef.cacheSim;

import java.util.Scanner;

import org.snajef.cacheSim.impl.LRUCacheImpl;
import org.snajef.cacheSim.impl.SimpleLRUCacheImpl;

public class Main {
	// TODO:
	// 1. Implement other block replacement policy types
	// 2. Implement selection for block replacement policy
	// 3. Implement a better way to set parameters?
	public static void main (String[] args) {
		Scanner sc = new Scanner(System.in);
		
		boolean printCacheState = (Integer.parseInt(args[0]) == 0 ? false : true);
		int ways = sc.nextInt();
		int nCacheBlocks = sc.nextInt();
		int blockSize = sc.nextInt();
		// Eat the empty string after reading ints.
		sc.nextLine();
		
		String cacheType = (ways > 1 ? ways + "-way Set-Associative (LRU)" : "Direct Mapped");
		System.out.println(cacheType + " Cache [" + nCacheBlocks * blockSize + " total bytes | " + blockSize + " byte block size]");
		
		LRUCacheImpl cache = new SimpleLRUCacheImpl(ways, nCacheBlocks, blockSize);
		
		while (sc.hasNextLine()) {
			long address = Long.parseLong(sc.nextLine(), 16);
			cache.accessCacheVerbose(address);
			if (printCacheState) {
				cache.printCacheState();
				System.out.println("");
			}
			cache.printPreviouslyStoredTags();
		}
		
		System.out.println("");
		cache.getStatistics();
	}
}
