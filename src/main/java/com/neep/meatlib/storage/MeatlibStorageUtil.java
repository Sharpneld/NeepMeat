package com.neep.meatlib.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

/**
 * Some fluid functions that are as similar as possible to StorageUtil versions but with added predicate parameters.
 */
@SuppressWarnings("UnstableApiUsage")
public class MeatlibStorageUtil
{
    @Nullable
    public static <T> ResourceAmount<T> findExtractableContent(@Nullable Storage<T> storage, BiPredicate<TransactionContext, T> filter, @Nullable TransactionContext transaction)
    {
        T extractableResource = findExtractableResource(storage, (t, r, l) -> filter.test(t, r), transaction);

        if (extractableResource != null)
        {
            long extractableAmount = MeatlibStorageUtil.simulateExtract(storage, extractableResource, Long.MAX_VALUE, transaction);

            if (extractableAmount > 0)
            {
                return new ResourceAmount<>(extractableResource, extractableAmount);
            }
        }

        return null;
    }

    // Storage::simulateExtract will be deprecated in 1.19.4. I should be able to use fnd and replace to correct this.
    public static <T> long simulateExtract(Storage<T> storage, T resource, long maxAmount, TransactionContext transaction)
    {
        return storage.simulateExtract(resource, maxAmount, transaction);
    }

    public static <T> long simulateInsert(Storage<T> storage, T resource, long amount, TransactionContext transaction)
    {
//        StorageUtil.simulateInsert()
        return storage.simulateInsert(resource, amount, transaction);
    }

//    @Nullable
//    public static <T> T findExtractableResource(@Nullable Storage<T> storage, BiPredicate<TransactionContext, T> filter, @Nullable TransactionContext transaction)
//    {
//        if (storage == null) return null;
//
//        try (Transaction nested = Transaction.openNested(transaction))
//        {
//            for (StorageView<T> view : storage.iterable(nested))
//            {
//                // Extract below could change the resource, so we have to query it before extracting.
//                T resource = view.getResource();
//
//                // Provide the predicate with the transaction
//                if (!view.isResourceBlank() && filter.test(nested, resource) && view.extract(resource, Long.MAX_VALUE, nested) > 0)
//                {
//                    // Will abort the extraction.
//                    return resource;
//                }
//            }
//        }
//        return null;
//    }

    @Nullable
    public static <T> T findExtractableResource(@Nullable Storage<T> storage, TriFunction<TransactionContext, T, Long, Boolean> filter, @Nullable TransactionContext transaction)
    {
        if (storage == null) return null;

        try (Transaction nested = Transaction.openNested(transaction))
        {
            for (StorageView<T> view : storage)
            {
                // Extract below could change the resource, so we have to query it before extracting.
                T resource = view.getResource();

                if (!view.isResourceBlank() && filter.apply(nested, resource, view.getAmount()) && view.extract(resource, Long.MAX_VALUE, nested) > 0)
                {
                    // Will abort the extraction.
                    return resource;
                }
            }
        }
        return null;
    }

    public static void scatterAmount(World world, BlockPos pos, @Nullable ResourceAmount<ItemVariant> resourceAmount)
    {
        if (resourceAmount != null)
        {
            ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ()+ 0.5, resourceAmount.resource().toStack((int) resourceAmount.amount()));
        }
    }

    public static NbtCompound amountToNbt(ResourceAmount<ItemVariant> resourceAmount)
    {
        NbtCompound nbt = resourceAmount.resource().toNbt();
        nbt.putLong("amount", resourceAmount.amount());
        return nbt;
    }

    public static ResourceAmount<ItemVariant> amountFromNbt(NbtCompound nbt)
    {
        ItemVariant variant = ItemVariant.fromNbt(nbt);
        return new ResourceAmount<>(variant, nbt.getLong("amount"));
    }

}
