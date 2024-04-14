package com.neep.meatlib.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockAccessor;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockSettingsAccessor;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.ItemConvertible;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class MeatlibBlockSettings extends FabricBlockSettings
{
    public static final Set<TagKey<Block>> DEFAULT_TAGS = Set.of(BlockTags.PICKAXE_MINEABLE);

    private Set<TagKey<Block>> tags = DEFAULT_TAGS;
    @Nullable private Function<Block, ItemConvertible> simpleDrop;

    protected MeatlibBlockSettings(Material material, MapColor mapColor)
    {
        super(material, mapColor);
    }

    // Copied from FabricBlockSettings
    protected MeatlibBlockSettings(AbstractBlock.Settings settings)
    {
//        this(((AbstractBlockSettingsAccessor) settings).getMaterial(), ((AbstractBlockSettingsAccessor) settings).getMapColorProvider());
        // FabricBlockSettings doesn't have a constructor with map colour function
        this(((AbstractBlockSettingsAccessor) settings).getMaterial(), ((AbstractBlockSettingsAccessor) settings).getMaterial().getColor());

        // Mostly Copied from vanilla's copy method
        AbstractBlockSettingsAccessor thisAccessor = (AbstractBlockSettingsAccessor) this;
        AbstractBlockSettingsAccessor otherAccessor = (AbstractBlockSettingsAccessor) settings;

        // Copied in vanilla: sorted by vanilla copy order
        thisAccessor.setMaterial(otherAccessor.getMaterial());
        this.hardness(otherAccessor.getHardness());
        this.resistance(otherAccessor.getResistance());
        this.collidable(otherAccessor.getCollidable());
        thisAccessor.setRandomTicks(otherAccessor.getRandomTicks());
        this.luminance(otherAccessor.getLuminance());
        thisAccessor.setMapColorProvider(otherAccessor.getMapColorProvider());
        this.sounds(otherAccessor.getSoundGroup());
        this.slipperiness(otherAccessor.getSlipperiness());
        this.velocityMultiplier(otherAccessor.getVelocityMultiplier());
        this.jumpVelocityMultiplier(otherAccessor.getJumpVelocityMultiplier());
        thisAccessor.setDynamicBounds(otherAccessor.getDynamicBounds());
        thisAccessor.setOpaque(otherAccessor.getOpaque());
        thisAccessor.setIsAir(otherAccessor.getIsAir());
        thisAccessor.setToolRequired(otherAccessor.isToolRequired());
        this.allowsSpawning(otherAccessor.getAllowsSpawningPredicate());
        this.solidBlock(otherAccessor.getSolidBlockPredicate());
        this.suffocates(otherAccessor.getSuffocationPredicate());
        this.blockVision(otherAccessor.getBlockVisionPredicate());
        this.postProcess(otherAccessor.getPostProcessPredicate());
        this.emissiveLighting(otherAccessor.getEmissiveLightingPredicate());
        this.offsetType(otherAccessor.getOffsetType());

        // Not copied in vanilla: field definition order
        this.jumpVelocityMultiplier(otherAccessor.getJumpVelocityMultiplier());
//        this.drops(otherAccessor.getLootTableId());
        this.allowsSpawning(otherAccessor.getAllowsSpawningPredicate());
        this.solidBlock(otherAccessor.getSolidBlockPredicate());
        this.suffocates(otherAccessor.getSuffocationPredicate());
        this.blockVision(otherAccessor.getBlockVisionPredicate());
        this.postProcess(otherAccessor.getPostProcessPredicate());

        if (settings instanceof MeatlibBlockSettings mbs)
        {
            this.tags = mbs.tags != null ? new HashSet<>(mbs.tags) : null;
        }
    }

    public static MeatlibBlockSettings create(Material material)
    {
        return new MeatlibBlockSettings(material, material.getColor());
    }

    public static MeatlibBlockSettings create(Material material, TagKey<Block> tagKey)
    {
        var settings = new MeatlibBlockSettings(material, material.getColor());
        settings.tags = new HashSet<>();
        settings.tags.add(tagKey);
        return settings;
    }

    public static MeatlibBlockSettings copyOf(AbstractBlock block)
    {
        return new MeatlibBlockSettings(((AbstractBlockAccessor) block).getSettings());
    }

    public static MeatlibBlockSettings copyOf(AbstractBlock.Settings settings)
    {
        return new MeatlibBlockSettings(settings);
    }

    public MeatlibBlockSettings copy()
    {
        return new MeatlibBlockSettings(this);
    }

    public Set<TagKey<Block>> getTags()
    {
        return tags;
    }

    @Nullable
    public Function<Block, ItemConvertible> getSimpleDrop()
    {
        return simpleDrop;
    }

    /**
     * For generating drops for blocks that do not implement MeatlibBlock.
     */
    public MeatlibBlockSettings simpleDrop(Function<Block, ItemConvertible> supplier)
    {
        this.simpleDrop = supplier;
        return this;
    }

    public MeatlibBlockSettings tags()
    {
        this.tags = Set.of();
        return this;
    }

    public MeatlibBlockSettings tags(TagKey<Block> tags)
    {
        this.tags = Set.of(tags);
        return this;
    }

    public MeatlibBlockSettings tags(Set<TagKey<Block>> tags)
    {
        this.tags = Set.copyOf(tags);
        return this;
    }

    // Masks a method from 1.20 for easier backports
    public MeatlibBlockSettings solid()
    {
        return this;
    }
}
