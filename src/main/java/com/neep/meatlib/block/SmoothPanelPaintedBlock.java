//package com.neep.meatlib.block;
//
//import com.neep.meatlib.item.BaseBlockItem;
//import com.neep.meatlib.item.ItemSettings;
//import com.neep.meatlib.item.MeatlibItemSettings;
//import com.neep.neepmeat.datagen.tag.NMTags;
//import net.minecraft.block.AbstractBlock;
//import net.minecraft.item.BlockItem;
//import net.minecraft.util.DyeColor;
//
//public class SmoothPanelPaintedBlock extends BasePaintedBlock
//{
//    public SmoothPanelPaintedBlock(String registryName, AbstractBlock.Settings settings)
//    {
//        super(registryName, Painted::new, settings);
//    }
//
//    static class Painted extends PaintedBlock
//    {
//        public Painted(String registryName, DyeColor col, Settings settings)
//        {
//            super(registryName, col, settings);
//        }
//
//        @Override
//        protected BlockItem makeItem()
//        {
//            return new BaseBlockItem(this, registryName, ItemSettings.block(), new MeatlibItemSettings().tag(NMTags.ROUGH_CONCRETE));
//        }
//    }
//}
