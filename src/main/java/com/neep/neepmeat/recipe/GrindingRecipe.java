package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.ItemIngredient;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.grinder.GrinderStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class GrindingRecipe implements Recipe<GrinderStorage>
{
    protected Identifier id;
    protected ItemIngredient itemInput;
    protected ItemIngredient itemOutput;
    protected int processTime;

    public GrindingRecipe(Identifier id, ItemIngredient itemInput, ItemIngredient itemOutput, int processTime)
    {
        this.itemInput = itemInput;
        this.itemOutput = itemOutput;
        this.processTime = processTime;
        this.id = id;
    }

    @Override
    public boolean matches(GrinderStorage inventory, World world)
    {
        return itemInput.test(inventory.getInputStorage());
    }

    @Override
    public ItemStack craft(GrinderStorage inventory)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height)
    {
        return false;
    }

    @Override
    public ItemStack getOutput()
    {
        throw new UnsupportedOperationException("use getItemOutput instead");
    }

    public ItemIngredient getItemOutput()
    {
        return itemOutput;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return NMrecipeTypes.GRINDING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.GRINDING;
    }

    public int getTime()
    {
        return processTime;
    }

    public boolean takeInputs(GrinderStorage storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            long extracted = storage.getInputStorage().extract(itemInput.resource(), itemInput.amount(), transaction);
            if (extracted == itemInput.amount())
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    public boolean ejectOutput(GrinderStorage storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            long inserted = storage.getOutputStorage().insert(itemOutput.resource(), itemOutput.amount(), transaction);
            if (inserted == itemOutput.amount())
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    public static class Serializer implements RecipeSerializer<GrindingRecipe>
    {
        RecipeFactory<GrindingRecipe> factory;
        int processTIme;

        public Serializer(RecipeFactory<GrindingRecipe> recipeFactory, int processTime)
        {
            this.factory = recipeFactory;
            this.processTIme = processTime;
        }

        @Override
        public GrindingRecipe read(Identifier id, JsonObject json)
        {
            JsonObject inputElement = JsonHelper.getObject(json, "input");
            ItemIngredient itemInput = ItemIngredient.fromJson(inputElement);

            JsonObject outputElement = JsonHelper.getObject(json, "output");
            ItemIngredient itemOutput = ItemIngredient.fromJson(outputElement);

            int time = JsonHelper.getInt(json, "processtime", this.processTIme);
            return this.factory.create(id, itemInput, itemOutput, time);
        }

        @Override
        public GrindingRecipe read(Identifier id, PacketByteBuf buf)
        {
            ItemIngredient itemInput = ItemIngredient.fromBuffer(buf);
            ItemIngredient itemOutput = ItemIngredient.fromBuffer(buf);
            int time = buf.readVarInt();

            return this.factory.create(id, itemInput, itemOutput, time);
        }

        @Override
        public void write(PacketByteBuf buf, GrindingRecipe recipe)
        {
            recipe.itemInput.write(buf);
            recipe.itemOutput.write(buf);
            buf.writeVarInt(recipe.processTime);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends GrindingRecipe>
        {
            T create(Identifier var1, ItemIngredient in, ItemIngredient out, int time);
        }
    }
}
