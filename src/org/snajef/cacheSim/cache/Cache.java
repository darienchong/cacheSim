package org.snajef.cacheSim.cache;

import java.util.HashMap;
import java.util.HashSet;

import org.snajef.cacheSim.model.CacheBlock;
import org.snajef.cacheSim.model.Result;

public abstract class Cache {
	protected CacheBlock[][] cacheBlocks;
	protected int ways;
	protected int nCacheBlocks;
	protected int blockSize;
	protected int nSets;
	protected HashMap<Integer,HashSet<Integer>> previouslyStoredTags;
	
	// For statistics
	protected int nBlocksUsed = 0;
	protected boolean isFull = false;
	
	protected int nColdMisses = 0;
	protected int nConflictMisses = 0;
	protected int nCapacityMisses = 0;
	protected int nTotalMisses = 0;
	
	protected int nHits = 0;
	protected int nAccesses = 0;
	
	
	// M s.t. nSets = 2^M
	protected int M;
	
	// N s.t. blockSize = 2^N
	protected int N;
	
	public Cache(int w, int nCacheBlocks, int blockSz) {
		ways = w;
		blockSize = blockSz;
		this.nCacheBlocks = nCacheBlocks;
		nSets = nCacheBlocks / ways;
		previouslyStoredTags = new HashMap<>();
		
		cacheBlocks = new CacheBlock[nSets][ways];
		for (int i = 0; i < nSets; i++) {
			for (int j = 0; j < ways; j++) {
				cacheBlocks[i][j] = new CacheBlock(-1, false);
				previouslyStoredTags.put(i, new HashSet<>());
			}
		}
		
		M =	(int) (Math.log(nSets) / Math.log(2));
		N = (int) (Math.log(blockSize) / Math.log(2));
	}
	
	public CacheBlock[][] getCacheBlocks() {
		return cacheBlocks;
	}
	
	// Addresses need to be stored as `long` because 32-bit hex numbers exceed
	// the storage capacity of `int` in Java. Everything else can still safely be stored as `int`.
	public Result accessCache(long address) {
		// Tag = addr[31:N+M]
		// Set idx = addr[N+M-1:N]
		// Offset = addr[N-1:0]
		// where M s.t. 2^M = nSets, N s.t. 2^N = blockSize
		
		int tag = getTag(address);
		int setIdx = getSetIdx(address);
		
		CacheBlock[] set = cacheBlocks[setIdx];
		
		for (int i = 0; i < ways; i++) {
			int currentTag = set[i].getTag();
			boolean currentValid = set[i].isValid();
			
			if (currentValid) {
				if (currentTag == tag) {
					// Cache hit
					updateCacheAfterHit(address, i);
					return Result.HIT;
				}
			}
		}
		
		// For the current set, none of the blocks match
		// Need to check what kind of miss it is (conflict/cold/capacity)
		Result result;
		if (isFull) {
			result = Result.CAPACITY_MISS;
		} else if (!isFull && previouslyStoredTags.get(setIdx).contains(tag)) {
			result = Result.CONFLICT_MISS;
		} else {
			result = Result.COLD_MISS;
		}
		
		// Update the cache and return the result.
		updateCacheAfterMiss(address);
		return result;
	}
	
	// Use this for tracking and verbose output.
	public Result accessCacheVerbose(long address) {
		nAccesses++;
		
		System.out.printf(
				"Address[" + String.format("0x%08x", address) 
				+ "] = [Tag:" + String.format("0x%x", getTag(address))
				+ "|Idx:" + String.format("0x%x", getSetIdx(address))
				+ "|Offset:" + String.format("0x%x", getOffset(address))
				+ "] => ");
		
		Result result = accessCache(address);
		switch (result) {
			case HIT:
				nHits++;
				System.out.printf("Hit!\n");
				break;
			case CONFLICT_MISS:
				nConflictMisses++;
				nTotalMisses++;
				System.out.printf("Miss! (Conflict)\n");
				break;
			case CAPACITY_MISS:
				nCapacityMisses++;
				nTotalMisses++;
				System.out.printf("Miss! (Capacity)\n");
				break;
			case COLD_MISS:
				nColdMisses++;
				nTotalMisses++;
				System.out.printf("Miss! (Cold)\n");
				break;
			default:
				break;
		}
		
		return result;
	}
	
	public void getStatistics() {
		double hitRate = ((double) nHits) / ((double) nAccesses);
		
		System.out.println("============== STATISTICS ==============");
		System.out.println("================ MISSES ================");
		System.out.println("     MISS TYPE      ||       TALLY");
		System.out.println("--------------------||------------------");
		System.out.println("Cold                || " + nColdMisses);
		System.out.println("Conflict            || " + nConflictMisses);
		System.out.println("Capacity            || " + nCapacityMisses);
		System.out.println("========================================");
		System.out.println("");
		System.out.println("========== HITS AND HIT RATE ===========");
		System.out.println("--------------------||------------------");
		System.out.println("Hits                || " + nHits);
		System.out.println("Accesses            || " + nAccesses);
		System.out.println("Hit rate            || " + hitRate * 100 + "%");
		System.out.println("========================================");
	}
	
	protected int getTag(long address) {
		return (int) (address >> (N + M));
	}
	
	protected int getSetIdx(long address) {
		return (int) ((address >> N) % (int) Math.pow(2, M));
	}
	
	protected int getOffset(long address) {
		return (int) (address % (int) Math.pow(2, N));
	}
	
	public abstract void updateCacheAfterHit(long address, int blockIdx);
	
	public abstract void updateCacheAfterMiss(long address);
}
