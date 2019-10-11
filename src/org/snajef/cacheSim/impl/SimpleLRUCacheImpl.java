package org.snajef.cacheSim.impl;

import java.time.LocalDateTime;

import org.snajef.cacheSim.model.CacheBlock;
import org.snajef.cacheSim.model.Result;

/**
 * Simpler version, considered all non-cold misses to be capacity misses.
 * @author froze
 *
 */
public class SimpleLRUCacheImpl extends LRUCacheImpl {	
	public SimpleLRUCacheImpl(int w, int cacheSize, int blockSz) {
		super(w, cacheSize, blockSz);
	}
	
	@Override
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
		if (previouslyStoredTags.contains(tag)) {
			result = Result.CONFLICT_MISS;
		} else {
			result = Result.COLD_MISS;
		}
		
		// Update the cache and return the result.
		updateCacheAfterMiss(address);
		return result;
	}
}
