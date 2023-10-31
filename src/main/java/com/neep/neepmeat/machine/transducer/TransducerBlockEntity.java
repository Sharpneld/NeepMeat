package com.neep.neepmeat.machine.transducer;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.Burner;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TransducerBlockEntity extends SyncableBlockEntity
{
    public static final FluidVariant INPUT_VARIANT = FluidVariant.of(Fluids.WATER);

    protected WritableSingleFluidStorage storage = new WritableSingleFluidStorage(FluidConstants.BUCKET, this::markDirty)
    {
        @Override
        public boolean supportsInsertion()
        {
            return false;
        }
    };

    protected final List<Burner> burners = new ArrayList<>();

    protected BlockApiCache<Storage<FluidVariant>, Direction> inputCache;

    protected long outputPower;
    protected boolean needsUpdate = true;

    public TransducerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public TransducerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.TRANSDUCER, pos, state);
    }

    public SingleVariantStorage<FluidVariant> getStorage(Direction direction)
    {
        return storage;
    }

    public FluidPump getPump(Direction direction)
    {
        return FluidPump.of(-0.5f, true);
    }

    public void updateBurners()
    {
        burners.clear();
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Direction direction : Direction.values())
        {
            if (direction.getAxis().isVertical()) continue;
            mutable.set(pos, direction);

            Burner burner = Burner.LOOKUP.find(world, mutable, null);
            if (burner != null) burners.add(burner);
        }

        mutable.set(pos, Direction.DOWN);
        inputCache = BlockApiCache.create(FluidStorage.SIDED, (ServerWorld) world, mutable);
        needsUpdate = false;
    }

    public void tick()
    {
        if (needsUpdate)
        {
            updateBurners();
        }

        outputPower = 0;
        for (Burner burner : burners)
        {
            burner.tickPowerConsumption();
            outputPower += burner.getOutputPower();
        }

        Storage<FluidVariant> inputStorage = inputCache.find(Direction.UP);
        if (inputStorage != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                // Calculate amount of fluid to be produced from current power output
                long produceAmount = PowerUtils.absToAmount(NMFluids.STILL_ETHEREAL_FUEL, outputPower);

                // Extract from water tank
                long extracted = inputStorage.extract(INPUT_VARIANT, produceAmount, transaction);

                // Insert output fluid into internal buffer
                long inserted = storage.insert(FluidVariant.of(NMFluids.STILL_ETHEREAL_FUEL), Math.min(produceAmount, extracted), transaction);

                if (inserted == produceAmount)
                {
                    transaction.commit();
                }
                else
                {
                    transaction.abort();
                }
            }
        }
    }

    public void update()
    {
        this.needsUpdate = true;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, TransducerBlockEntity be)
    {
        be.tick();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt1(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
    }

    BloodAcceptor bloodAcceptor = new BloodAcceptor()
    {
        @Override
        public float getRate()
        {
            return 0.1f;
        }

        @Override
        public void updateInflux(float influx)
        {

        }

        @Override
        public Mode getMode()
        {
            return Mode.OUT;
        }
    };

    public BloodAcceptor getBloodAcceptor(Direction direction)
    {
        return bloodAcceptor;
    }
}
