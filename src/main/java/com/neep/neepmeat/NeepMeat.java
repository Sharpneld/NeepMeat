package com.neep.neepmeat;

import com.neep.meatlib.MeatLib;
import com.neep.neepmeat.api.processing.OreFatRegistry;
import com.neep.neepmeat.guide.GuideReloadListener;
import com.neep.neepmeat.datagen.NMRecipes;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.init.*;
import com.neep.neepmeat.machine.charnel_compactor.CharnelCompactorStorage;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.transport.data.FluidNetworkManager;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.StagedTransactions;
import com.neep.neepmeat.world.NMFeatures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

public class NeepMeat implements ModInitializer
{
	public static final String NAMESPACE = "neepmeat";
	public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);

	@Override
	public void onInitialize()
	{
		LOGGER.info("Hello from NeepMeat!");

		GeckoLib.initialize();

		// Oooh, the jank! There must be a better way.
		MeatLib.setNamespace(NAMESPACE);
		NMrecipeTypes.init();
		NMRecipes.init();
		new NMBlocks();
		new NMItems();
		NMLootTables.init();
		NMTags.init();
		NMParticles.init();

		NMFluids.initialise();
		NMBlockEntities.initialise();
		NMEntities.initialise();
		OreFatRegistry.init();

		NMFeatures.init();

		ItemStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, direction) -> CharnelCompactorStorage.getStorage(world, pos, direction), NMBlocks.CHARNEL_COMPACTOR);
		FluidStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, direction) -> blockEntity instanceof IntegratorBlockEntity be ? be.getStorage(world, pos, state, direction) : null, NMBlocks.INTEGRATOR_EGG);

		ScreenHandlerInit.registerScreenHandlers();

		FluidNodeManager.registerEvents();
//		NetworkRebuilding.init();
		FluidNetworkManager.init();
		StagedTransactions.init();

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(GuideReloadListener.getInstance());


		FluidStorage.combinedItemApiProvider(Items.MILK_BUCKET).register(context ->
				new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(NMFluids.STILL_MILK), FluidConstants.BUCKET));
	}
}
