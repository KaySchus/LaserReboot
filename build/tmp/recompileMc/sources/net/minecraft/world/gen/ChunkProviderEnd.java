package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockChorusFlower;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenEndIsland;
import net.minecraft.world.gen.structure.MapGenEndCity;

public class ChunkProviderEnd implements IChunkGenerator
{
    /** RNG. */
    private Random rand;
    protected static final IBlockState END_STONE = Blocks.end_stone.getDefaultState();
    protected static final IBlockState AIR = Blocks.air.getDefaultState();
    private NoiseGeneratorOctaves field_185969_i;
    private NoiseGeneratorOctaves field_185970_j;
    private NoiseGeneratorOctaves field_185971_k;
    /** A NoiseGeneratorOctaves used in generating terrain */
    public NoiseGeneratorOctaves noiseGen5;
    /** A NoiseGeneratorOctaves used in generating terrain */
    public NoiseGeneratorOctaves noiseGen6;
    /** Reference to the World object. */
    private final World worldObj;
    /** are map structures going to be generated (e.g. strongholds) */
    private final boolean mapFeaturesEnabled;
    private final MapGenEndCity field_185972_n = new MapGenEndCity(this);
    private NoiseGeneratorSimplex islandNoise;
    private double[] field_185974_p;
    /** The biomes that are used to generate the chunk */
    private BiomeGenBase[] biomesForGeneration;
    double[] field_185966_e;
    double[] field_185967_f;
    double[] field_185968_g;
    private final WorldGenEndIsland field_185975_r = new WorldGenEndIsland();
    // temporary variables used during event handling
    private int chunkX = 0;
    private int chunkZ = 0;

    public ChunkProviderEnd(World worldObjIn, boolean mapFeaturesEnabledIn, long seed)
    {
        this.worldObj = worldObjIn;
        this.mapFeaturesEnabled = mapFeaturesEnabledIn;
        this.rand = new Random(seed);
        this.field_185969_i = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_185970_j = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_185971_k = new NoiseGeneratorOctaves(this.rand, 8);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.islandNoise = new NoiseGeneratorSimplex(this.rand);

        net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextEnd ctx =
                new net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextEnd(field_185969_i, field_185970_j, field_185971_k, noiseGen5, noiseGen6, islandNoise);
        ctx = net.minecraftforge.event.terraingen.TerrainGen.getModdedNoiseGenerators(worldObjIn, this.rand, ctx);
        this.field_185969_i = ctx.getLPerlin1();
        this.field_185970_j = ctx.getLPerlin2();
        this.field_185971_k = ctx.getPerlin();
        this.noiseGen5 = ctx.getDepth();
        this.noiseGen6 = ctx.getScale();
        this.islandNoise = ctx.getIsland();
    }

    /**
     * Generates a bare-bones chunk of nothing but stone or ocean blocks, formed, but featureless.
     */
    public void setBlocksInChunk(int x, int z, ChunkPrimer primer)
    {
        int i = 2;
        int j = i + 1;
        int k = 33;
        int l = i + 1;
        this.field_185974_p = this.func_185963_a(this.field_185974_p, x * i, 0, z * i, j, k, l);

        for (int i1 = 0; i1 < i; ++i1)
        {
            for (int j1 = 0; j1 < i; ++j1)
            {
                for (int k1 = 0; k1 < 32; ++k1)
                {
                    double d0 = 0.25D;
                    double d1 = this.field_185974_p[((i1 + 0) * l + j1 + 0) * k + k1 + 0];
                    double d2 = this.field_185974_p[((i1 + 0) * l + j1 + 1) * k + k1 + 0];
                    double d3 = this.field_185974_p[((i1 + 1) * l + j1 + 0) * k + k1 + 0];
                    double d4 = this.field_185974_p[((i1 + 1) * l + j1 + 1) * k + k1 + 0];
                    double d5 = (this.field_185974_p[((i1 + 0) * l + j1 + 0) * k + k1 + 1] - d1) * d0;
                    double d6 = (this.field_185974_p[((i1 + 0) * l + j1 + 1) * k + k1 + 1] - d2) * d0;
                    double d7 = (this.field_185974_p[((i1 + 1) * l + j1 + 0) * k + k1 + 1] - d3) * d0;
                    double d8 = (this.field_185974_p[((i1 + 1) * l + j1 + 1) * k + k1 + 1] - d4) * d0;

                    for (int l1 = 0; l1 < 4; ++l1)
                    {
                        double d9 = 0.125D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for (int i2 = 0; i2 < 8; ++i2)
                        {
                            double d14 = 0.125D;
                            double d15 = d10;
                            double d16 = (d11 - d10) * d14;

                            for (int j2 = 0; j2 < 8; ++j2)
                            {
                                IBlockState iblockstate = AIR;

                                if (d15 > 0.0D)
                                {
                                    iblockstate = END_STONE;
                                }

                                int k2 = i2 + i1 * 8;
                                int l2 = l1 + k1 * 4;
                                int i3 = j2 + j1 * 8;
                                primer.setBlockState(k2, l2, i3, iblockstate);
                                d15 += d16;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    public void func_185962_a(ChunkPrimer primer)
    {
        if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, this.chunkX, this.chunkZ, primer, this.worldObj)) return;
        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
                int k = 1;
                int l = -1;
                IBlockState iblockstate = END_STONE;
                IBlockState iblockstate1 = END_STONE;

                for (int i1 = 127; i1 >= 0; --i1)
                {
                    IBlockState iblockstate2 = primer.getBlockState(i, i1, j);

                    if (iblockstate2.getMaterial() == Material.air)
                    {
                        l = -1;
                    }
                    else if (iblockstate2.getBlock() == Blocks.stone)
                    {
                        if (l == -1)
                        {
                            if (k <= 0)
                            {
                                iblockstate = AIR;
                                iblockstate1 = END_STONE;
                            }

                            l = k;

                            if (i1 >= 0)
                            {
                                primer.setBlockState(i, i1, j, iblockstate);
                            }
                            else
                            {
                                primer.setBlockState(i, i1, j, iblockstate1);
                            }
                        }
                        else if (l > 0)
                        {
                            --l;
                            primer.setBlockState(i, i1, j, iblockstate1);
                        }
                    }
                }
            }
        }
    }

    public Chunk provideChunk(int x, int z)
    {
        this.chunkX = x; this.chunkZ = z;
        this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        this.biomesForGeneration = this.worldObj.getBiomeProvider().loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        this.setBlocksInChunk(x, z, chunkprimer);
        this.func_185962_a(chunkprimer);

        if (this.mapFeaturesEnabled)
        {
            this.field_185972_n.generate(this.worldObj, x, z, chunkprimer);
        }

        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        byte[] abyte = chunk.getBiomeArray();

        for (int i = 0; i < abyte.length; ++i)
        {
            abyte[i] = (byte)BiomeGenBase.getIdForBiome(this.biomesForGeneration[i]);
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    private float func_185960_a(int p_185960_1_, int p_185960_2_, int p_185960_3_, int p_185960_4_)
    {
        float f = (float)(p_185960_1_ * 2 + p_185960_3_);
        float f1 = (float)(p_185960_2_ * 2 + p_185960_4_);
        float f2 = 100.0F - MathHelper.sqrt_float(f * f + f1 * f1) * 8.0F;

        if (f2 > 80.0F)
        {
            f2 = 80.0F;
        }

        if (f2 < -100.0F)
        {
            f2 = -100.0F;
        }

        for (int i = -12; i <= 12; ++i)
        {
            for (int j = -12; j <= 12; ++j)
            {
                long k = (long)(p_185960_1_ + i);
                long l = (long)(p_185960_2_ + j);

                if (k * k + l * l > 4096L && this.islandNoise.func_151605_a((double)k, (double)l) < -0.8999999761581421D)
                {
                    float f3 = (MathHelper.abs((float)k) * 3439.0F + MathHelper.abs((float)l) * 147.0F) % 13.0F + 9.0F;
                    f = (float)(p_185960_3_ - i * 2);
                    f1 = (float)(p_185960_4_ - j * 2);
                    float f4 = 100.0F - MathHelper.sqrt_float(f * f + f1 * f1) * f3;

                    if (f4 > 80.0F)
                    {
                        f4 = 80.0F;
                    }

                    if (f4 < -100.0F)
                    {
                        f4 = -100.0F;
                    }

                    if (f4 > f2)
                    {
                        f2 = f4;
                    }
                }
            }
        }

        return f2;
    }

    public boolean func_185961_c(int p_185961_1_, int p_185961_2_)
    {
        return (long)p_185961_1_ * (long)p_185961_1_ + (long)p_185961_2_ * (long)p_185961_2_ > 4096L && this.func_185960_a(p_185961_1_, p_185961_2_, 1, 1) >= 0.0F;
    }

    private double[] func_185963_a(double[] p_185963_1_, int p_185963_2_, int p_185963_3_, int p_185963_4_, int p_185963_5_, int p_185963_6_, int p_185963_7_)
    {
        net.minecraftforge.event.terraingen.ChunkGeneratorEvent.InitNoiseField event = new net.minecraftforge.event.terraingen.ChunkGeneratorEvent.InitNoiseField(this, p_185963_1_, p_185963_2_, p_185963_3_, p_185963_4_, p_185963_5_, p_185963_6_, p_185963_7_);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == net.minecraftforge.fml.common.eventhandler.Event.Result.DENY) return event.getNoisefield();

        if (p_185963_1_ == null)
        {
            p_185963_1_ = new double[p_185963_5_ * p_185963_6_ * p_185963_7_];
        }

        double d0 = 684.412D;
        double d1 = 684.412D;
        d0 = d0 * 2.0D;
        this.field_185966_e = this.field_185971_k.generateNoiseOctaves(this.field_185966_e, p_185963_2_, p_185963_3_, p_185963_4_, p_185963_5_, p_185963_6_, p_185963_7_, d0 / 80.0D, d1 / 160.0D, d0 / 80.0D);
        this.field_185967_f = this.field_185969_i.generateNoiseOctaves(this.field_185967_f, p_185963_2_, p_185963_3_, p_185963_4_, p_185963_5_, p_185963_6_, p_185963_7_, d0, d1, d0);
        this.field_185968_g = this.field_185970_j.generateNoiseOctaves(this.field_185968_g, p_185963_2_, p_185963_3_, p_185963_4_, p_185963_5_, p_185963_6_, p_185963_7_, d0, d1, d0);
        int i = p_185963_2_ / 2;
        int j = p_185963_4_ / 2;
        int k = 0;

        for (int l = 0; l < p_185963_5_; ++l)
        {
            for (int i1 = 0; i1 < p_185963_7_; ++i1)
            {
                float f = this.func_185960_a(i, j, l, i1);

                for (int j1 = 0; j1 < p_185963_6_; ++j1)
                {
                    double d2 = 0.0D;
                    double d3 = this.field_185967_f[k] / 512.0D;
                    double d4 = this.field_185968_g[k] / 512.0D;
                    double d5 = (this.field_185966_e[k] / 10.0D + 1.0D) / 2.0D;

                    if (d5 < 0.0D)
                    {
                        d2 = d3;
                    }
                    else if (d5 > 1.0D)
                    {
                        d2 = d4;
                    }
                    else
                    {
                        d2 = d3 + (d4 - d3) * d5;
                    }

                    d2 = d2 - 8.0D;
                    d2 = d2 + (double)f;
                    int k1 = 2;

                    if (j1 > p_185963_6_ / 2 - k1)
                    {
                        double d6 = (double)((float)(j1 - (p_185963_6_ / 2 - k1)) / 64.0F);
                        d6 = MathHelper.clamp_double(d6, 0.0D, 1.0D);
                        d2 = d2 * (1.0D - d6) + -3000.0D * d6;
                    }

                    k1 = 8;

                    if (j1 < k1)
                    {
                        double d7 = (double)((float)(k1 - j1) / ((float)k1 - 1.0F));
                        d2 = d2 * (1.0D - d7) + -30.0D * d7;
                    }

                    p_185963_1_[k] = d2;
                    ++k;
                }
            }
        }

        return p_185963_1_;
    }

    public void populate(int x, int z)
    {
        BlockFalling.fallInstantly = true;
        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.worldObj, x, z, false);
        BlockPos blockpos = new BlockPos(x * 16, 0, z * 16);

        if (this.mapFeaturesEnabled)
        {
            this.field_185972_n.generateStructure(this.worldObj, this.rand, new ChunkCoordIntPair(x, z));
        }

        this.worldObj.getBiomeGenForCoords(blockpos.add(16, 0, 16)).decorate(this.worldObj, this.worldObj.rand, blockpos);
        long i = (long)x * (long)x + (long)z * (long)z;

        if (i > 4096L)
        {
            float f = this.func_185960_a(x, z, 1, 1);

            if (f < -20.0F && this.rand.nextInt(14) == 0)
            {
                this.field_185975_r.generate(this.worldObj, this.rand, blockpos.add(this.rand.nextInt(16) + 8, 55 + this.rand.nextInt(16), this.rand.nextInt(16) + 8));

                if (this.rand.nextInt(4) == 0)
                {
                    this.field_185975_r.generate(this.worldObj, this.rand, blockpos.add(this.rand.nextInt(16) + 8, 55 + this.rand.nextInt(16), this.rand.nextInt(16) + 8));
                }
            }

            if (this.func_185960_a(x, z, 1, 1) > 40.0F)
            {
                int j = this.rand.nextInt(5);

                for (int k = 0; k < j; ++k)
                {
                    int l = this.rand.nextInt(16) + 8;
                    int i1 = this.rand.nextInt(16) + 8;
                    int j1 = this.worldObj.getHeight(blockpos.add(l, 0, i1)).getY();

                    if (j1 > 0)
                    {
                        int k1 = j1 - 1;

                        if (this.worldObj.isAirBlock(blockpos.add(l, k1 + 1, i1)) && this.worldObj.getBlockState(blockpos.add(l, k1, i1)).getBlock() == Blocks.end_stone)
                        {
                            BlockChorusFlower.func_185603_a(this.worldObj, blockpos.add(l, k1 + 1, i1), this.rand, 8);
                        }
                    }
                }
            }
        }

        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.worldObj, x, z, false);
        BlockFalling.fallInstantly = false;
    }

    public boolean generateStructures(Chunk chunkIn, int x, int z)
    {
        return false;
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        return this.worldObj.getBiomeGenForCoords(pos).getSpawnableList(creatureType);
    }

    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position)
    {
        return null;
    }

    public void recreateStructures(Chunk chunkIn, int x, int z)
    {
    }
}