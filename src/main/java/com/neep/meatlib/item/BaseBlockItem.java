package com.neep.meatlib.item;

import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NMItemGroups;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class BaseBlockItem extends BlockItem implements MeatlibItem
{
    private final String name;
    private final TooltipSupplier tooltipSupplier;

    public BaseBlockItem(Block block, String registryName, ItemSettings itemSettings)
    {
        this(block, registryName, itemSettings, new MeatlibItemSettings().maxCount(itemSettings.maxCount).group(NMItemGroups.GENERAL));
    }

    public BaseBlockItem(Block block, String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(block, settings);
        this.name = registryName;
        this.tooltipSupplier = itemSettings.tooltipSupplier;
        ItemRegistry.queue(this);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        tooltipSupplier.apply(this, tooltip);
//        for (int i = 0; i < loreLines; ++i)
//        {
//            tooltip.add(new TranslatableText(getTranslationKey() + ".lore_" + i).formatted(Formatting.GRAY));
//        }
    }

    public String getRegistryName()
    {
        return name;
    }
}
