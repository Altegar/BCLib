package ru.bclib.api.dataexchange.handler;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import ru.bclib.BCLib;
import ru.bclib.api.dataexchange.DataExchangeAPI;
import ru.bclib.api.dataexchange.DataHandler;
import ru.bclib.api.dataexchange.DataHandlerDescriptor;
import ru.bclib.api.datafixer.DataFixerAPI;
import ru.bclib.config.Configs;

import java.io.File;

/**
 * This message is sent once a player enters the world. It initiates a sequence of Messages that will sync files between both
 * client and server.
 * 	<table>
 * 	    <caption></caption>
 * 		<tr>
 * 			<th>Server</th>
 * 			<th></th>
 * 			<th>Client</th>
 * 			<th></th>
 * 		</tr>
 * 		<tr>
 * 			<td colspan="4">Player enters World</td>
 * 		</tr>
 * 		<tr>
 * 			<td></td>
 * 			<td>&lt;--</td>
 * 			<td>{@link HelloServer}</td>
 * 			<td>Sends the current BLib-Version installed on the Client</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@link HelloClient}</td>
 * 			<td>--&gt;</td>
 * 			<td></td>
 * 			<td>Sends the current BClIb-Version, the Version of all Plugins and data for all AutpoSync-Files
 * 			({@link DataExchangeAPI#addAutoSyncFile(String, File)} on the Server</td>
 * 		</tr>
 * 		<tr>
 * 			<td></td>
 * 			<td>&lt;--</td>
 * 			<td>{@link RequestFiles}</td>
 * 			<td>Request missing or out of sync Files from the Server</td>
 * 		</tr>
 * 		<tr>
 * 			<td>{@link SendFiles}</td>
 * 			<td>--&gt;</td>
 * 			<td></td>
 * 			<td>Send Files from the Server to the Client</td>
 * 		</tr>
 * 	</table>
 */
public class HelloServer extends DataHandler {
	public static DataHandlerDescriptor DESCRIPTOR = new DataHandlerDescriptor(new ResourceLocation(BCLib.MOD_ID, "hello_server"), HelloServer::new, true, false);
	
	protected String bclibVersion ="0.0.0";
	public HelloServer() {
		super(DESCRIPTOR.IDENTIFIER, false);
	}

	@Override
	protected void serializeData(FriendlyByteBuf buf) {
		BCLib.LOGGER.info("Sending hello to server.");
		buf.writeInt(DataFixerAPI.getModVersion(HelloClient.getBCLibVersion()));
	}
	
	@Override
	protected void deserializeFromIncomingData(FriendlyByteBuf buf, PacketSender responseSender, boolean fromClient) {
		bclibVersion = DataFixerAPI.getModVersion(buf.readInt());
	}
	
	@Override
	protected void runOnGameThread(Minecraft client, MinecraftServer server, boolean isClient) {
		String localBclibVersion = HelloClient.getBCLibVersion();
		BCLib.LOGGER.info("Received Hello from Client. (server="+localBclibVersion+", client="+bclibVersion+")");

		if (Configs.MAIN_CONFIG.getBoolean(Configs.MAIN_SYNC_CATEGORY, "enabled", true)) {
			reply(new HelloClient(), server);
		} else {
			BCLib.LOGGER.info("Auto-Sync was disabled on the server.");
		}
	}
}