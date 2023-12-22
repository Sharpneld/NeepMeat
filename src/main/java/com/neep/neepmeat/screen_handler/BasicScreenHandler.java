package com.neep.neepmeat.screen_handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

public abstract class BasicScreenHandler extends ScreenHandler
{
    protected Inventory inventory;
    protected PlayerInventory playerInventory;
    @Nullable protected PropertyDelegate propertyDelegate;

    protected BasicScreenHandler(@Nullable ScreenHandlerType<?> type, PlayerInventory playerInventory, @Nullable Inventory inventory, int syncId, @Nullable PropertyDelegate delegate)
    {
        super(type, syncId);
        this.propertyDelegate = delegate;
        this.inventory = inventory;
        this.playerInventory = playerInventory;

        if (inventory != null) inventory.onOpen(playerInventory.player);

        if (propertyDelegate != null)
            this.addProperties(delegate);
    }

    public int getProperty(int i)
    {
        if (propertyDelegate != null) return propertyDelegate.get(i);
        return 0;
    }

    public void setProperty(int i, int value)
    {
        if (propertyDelegate != null) propertyDelegate.set(i, value);
    }

    protected void createPlayerSlots(int startX, int startY, PlayerInventory playerInventory)
    {
        int m;
        int l;
        for (m = 0; m < 3; ++m)
        {
            for (l = 0; l < 9; ++l)
            {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, startX + l * 18, startY + m * 18));
            }
        }

        for (m = 0; m < 9; ++m)
        {
            this.addSlot(new Slot(playerInventory, m, startX + m * 18, startY + 58));
        }
    }

    protected void createSlotBlock(int startX, int startY, int nx, int ny, Inventory inventory, int startIndex, SlotConstructor constructor)
    {
        int m, l;
        for (m = 0; m < ny; ++m)
        {
            for (l = 0; l < nx; ++l)
            {
                this.addSlot(constructor.construct(inventory, startIndex + l + m * nx, startX + l * 18, startY + m * 18));
            }
        }
    }

    // For some reason, not implementing this causes the game to freeze when shift-clicking.
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot)
    {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack())
        {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size())
            {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(originalStack, 0, this.inventory.size(), false))
            {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty())
            {
                slot.setStack(ItemStack.EMPTY);
            }
            else
            {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @FunctionalInterface
    public interface SlotConstructor
    {
        Slot construct(Inventory inventory, int index, int x, int y);
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return this.inventory.canPlayerUse(player);
    }
}