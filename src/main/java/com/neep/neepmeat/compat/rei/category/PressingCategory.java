package com.neep.neepmeat.compat.rei.category;

import com.google.common.collect.Lists;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.compat.rei.display.GrindingDisplay;
import com.neep.neepmeat.compat.rei.display.PressingDisplay;
import com.neep.neepmeat.init.NMBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import java.text.DecimalFormat;
import java.util.List;

public class PressingCategory implements DisplayCategory<PressingDisplay>
{
    @Override
    public CategoryIdentifier<? extends PressingDisplay> getCategoryIdentifier()
    {
        return NMREIPlugin.PRESSING;
    }

    @Override
    public Text getTitle()
    {
        return Text.translatable("category." + NeepMeat.NAMESPACE + ".pressing");
    }

    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(NMBlocks.HYDRAULIC_PRESS);
    }

    @Override
    public List<Widget> setupDisplay(PressingDisplay display, Rectangle bounds)
    {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.y + 10);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 9)));

//        widgets.add(Widgets.createBurningFire(new Point(startPoint.x + 1, startPoint.y + 20)).animationDurationMS(10000));
//        widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 5),
//                new TranslatableText("category.rei.campfire.time", df.format(cookingTime / 20d))).noShadow().rightAligned().color(0xFF404040, 0xFFBBBBBB));
//        widgets.add(Widgets.createArrow(new Point(startPoint.x + 24, startPoint.y + 8)).animationDurationTicks(cookingTime));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 25, startPoint.y + 9)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 9)).entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 9)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight()
    {
        return 55;
    }
}
