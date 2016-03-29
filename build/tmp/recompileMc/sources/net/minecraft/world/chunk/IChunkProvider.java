package net.minecraft.world.chunk;

public interface IChunkProvider
{
    Chunk getLoadedChunk(int x, int z);

    Chunk provideChunk(int x, int z);

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    boolean unloadQueuedChunks();

    /**
     * Converts the instance data to a readable string.
     */
    String makeString();
}