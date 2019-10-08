package org.snajef.cacheSim;

import java.util.Scanner;

import org.snajef.cacheSim.model.LRUCache;

public class Main {
	public static void main (String[] args) {
		Scanner sc = new Scanner(System.in);
		
		boolean printCacheState = (Integer.parseInt(args[0]) == 0 ? false : true);
		int ways = sc.nextInt();
		int nCacheBlocks = sc.nextInt();
		int blockSize = sc.nextInt();
		
		String cacheType = (ways > 1 ? ways + "-way Set-Associative (LRU)" : "Direct Mapped");
		System.out.println(cacheType + " Cache [" + nCacheBlocks * blockSize + " total bytes | " + blockSize + " byte block size]");
		
		LRUCache cache = new LRUCache(ways, nCacheBlocks, blockSize);
		
		while (sc.hasNextInt(16)) {
			int address = sc.nextInt(16);
			cache.accessCacheVerbose(address);
			if (printCacheState) {
				cache.printCacheState();
				System.out.println("");
			}
		}
		
		cache.getStatistics();
	}
}
