package org.snajef.cacheSim.impl;

import java.time.LocalDateTime;

import org.snajef.cacheSim.cache.Cache;
import org.snajef.cacheSim.model.CacheBlock;

public class LRUCacheImpl extends Cache {
	protected LocalDateTime[][] lastAccessed;
	
	public LRUCacheImpl(int w, int cacheSize, int blockSz) {
		super(w, cacheSize, blockSz);
		lastAccessed = new LocalDateTime[nSets][ways];
	}
	
	@Override
	public void updateCacheAfterHit(long address, int blockIdx) {
		int setIdx = getSetIdx(address);
		
		lastAccessed[setIdx][blockIdx] = LocalDateTime.now();
	}
	
	@Override
	public void updateCacheAfterMiss(long address) {
		// Update cache according to LRU block replacement policy
		int tag = getTag(address);
		int setIdx = getSetIdx(address);
		
		// We need to find an empty block.
		// If no empty block exists, we need to evict the least recently accessed block.
		for (int i = 0; i < ways; i++) {
			CacheBlock currBlock = cacheBlocks[setIdx][i];
			if (!currBlock.isValid()) {
				currBlock.setValid(true);
				currBlock.setTag(tag);
				lastAccessed[setIdx][i] = LocalDateTime.now();
				nBlocksUsed++;
				if (nBlocksUsed == nCacheBlocks) {
					isFull = true;
				}
				return;
			}
		}
		
		// We can't find an empty block.
		// Find the least recently updated block in the set and replace (evict) it.
		int leastRecentlyUpdatedBlockIdx = 0;
		LocalDateTime timestamp = lastAccessed[setIdx][0];
		for (int i = 0; i < ways; i++) {
			if (lastAccessed[setIdx][i].isBefore(timestamp)) {
				leastRecentlyUpdatedBlockIdx = i;
				timestamp = lastAccessed[setIdx][i];
			}
		}
		
		CacheBlock blockToReplace = cacheBlocks[setIdx][leastRecentlyUpdatedBlockIdx];
		blockToReplace.setTag(tag);
		lastAccessed[setIdx][leastRecentlyUpdatedBlockIdx] = LocalDateTime.now();
		previouslyStoredTags.add(tag);
	}
	
	public void printCacheState() {
		System.out.println("----------------------------------------");
		for (int i = 0; i < nSets; i++) {
			String setName = "[Set " + i + "]";
			System.out.println(setName);
			
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < setName.length(); j++) {
				sb.append(" "); // Padding
			}
			
			String spacePad = sb.toString();
			
			for (int k = 0; k < ways; k++) {
				System.out.println(spacePad + "[$idx: " + k + "]" + cacheBlocks[i][k].toString() + "[lastAccessed: " + (lastAccessed[i][k] == null ? "Never" : lastAccessed[i][k]) + "]");
			}
		}
		System.out.println("----------------------------------------");
	}
}
