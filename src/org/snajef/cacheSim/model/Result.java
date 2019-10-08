package org.snajef.cacheSim.model;

public enum Result {
	HIT,
	CONFLICT_MISS,
	COLD_MISS,
	CAPACITY_MISS,
	CONFLICT_CAPACITY_MISS;
}
