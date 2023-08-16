package de.nekosarekawaii.foxglove.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;

import java.io.File;

public class CustomServerList extends ServerList {

    private final String name;

    public CustomServerList(final String name) {
        super(MinecraftClient.getInstance());
        this.name = name;
    }

    @Override
    public void loadFile() {
        try {
            this.servers.clear();
            this.hiddenServers.clear();

            final NbtCompound nbtCompound = NbtIo.read(new File(this.client.runDirectory, name + "_servers.dat"));
            if (nbtCompound == null)
                return;

            final NbtList nbtList = nbtCompound.getList("servers", NbtElement.COMPOUND_TYPE);

            for (int i = 0; i < nbtList.size(); ++i) {
                final NbtCompound nbtCompound2 = nbtList.getCompound(i);
                final ServerInfo serverInfo = ServerInfo.fromNbt(nbtCompound2);

                if (nbtCompound2.getBoolean("hidden")) {
                    this.hiddenServers.add(serverInfo);
                } else {
                    this.servers.add(serverInfo);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Couldn't load server list", e);
        }
    }

    @Override
    public void saveFile() {
        try {
            final NbtList nbtList = new NbtList();

            for (final ServerInfo serverInfo : this.servers) {
                final NbtCompound nbtCompound = serverInfo.toNbt();
                nbtCompound.putBoolean("hidden", false);
                nbtList.add(nbtCompound);
            }

            for (final ServerInfo serverInfo : this.hiddenServers) {
                final NbtCompound nbtCompound = serverInfo.toNbt();
                nbtCompound.putBoolean("hidden", true);
                nbtList.add(nbtCompound);
            }

            final NbtCompound nbtCompound2 = new NbtCompound();
            nbtCompound2.put("servers", nbtList);

            final File file = File.createTempFile(name + "_servers", ".dat", this.client.runDirectory);
            NbtIo.write(nbtCompound2, file);

            System.out.println("Saving server list to " + file.getAbsolutePath());

            final File file2 = new File(this.client.runDirectory, name + "_servers.dat_old");
            final File file3 = new File(this.client.runDirectory, name + "_servers.dat");
            Util.backupAndReplace(file3, file, file2);
        } catch (final Exception e) {
            LOGGER.error("Couldn't save server list", e);
        }
    }

    public String getName() {
        return this.name;
    }

}
