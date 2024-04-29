package com.neep.neepmeat.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public class MeatSteelHoeItem extends HoeItem
{
//    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public MeatSteelHoeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings)
    {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state)
    {
        if (state.isOf(Blocks.COBWEB))
        {
            return 15.0f;
        }
        else if (state.isIn(BlockTags.SWORD_EFFICIENT))
        {
            return 1.5f;
        }
        return super.getMiningSpeedMultiplier(stack, state);
    }

    @Override
    public boolean isSuitableFor(BlockState state)
    {
        return super.isSuitableFor(state) || state.isOf(Blocks.COBWEB);
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return super.isEnchantable(stack);
    }
}
