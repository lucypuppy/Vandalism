/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

// TODO: Fix

/*
public class ItemStackLoggerModule extends AbstractModule implements PlayerUpdateListener {

    private final ButtonValue openDirectoryButton = new ButtonValue(
            this,
            "Open Directory",
            "Opens the directory where the logged items are stored.",
            buttonValue -> {
                LOGGED_ITEMS_DIR.mkdirs();
                if (!LOGGED_ITEMS_DIR.exists()) return;
                Util.getOperatingSystem().open(LOGGED_ITEMS_DIR);
            }
    );

    private final BooleanValue notifyInChat = new BooleanValue(
            this,
            "Notify in Chat",
            "Sends a notification into chat to inform you about a newly found item.",
            true
    );

    private static final File LOGGED_ITEMS_DIR = new File(Vandalism.getInstance().getRunDirectory(), "logged-items");

    private final DateFormat formatter = new SimpleDateFormat("hh:mm:ss a, dd/MM/yyyy");

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ItemStackLoggerModule() {
        super(
                "Item Stack Logger",
                "Logs item stacks with their NBT data into a file.",
                Category.MISC
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this, Priorities.HIGH);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        for (final Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (entity instanceof final ItemEntity itemEntity) {
                this.logStack(entity, itemEntity.getStack());
            } else {
                for (final ItemStack stack : entity.getItemsEquipped()) {
                    this.logStack(entity, stack);
                }
            }
        }
    }

    private void logStack(final Entity entity, final ItemStack stack) {
        final Item item = stack.getItem();
        if (item.equals(Items.AIR)) {
            return;
        }

        final String rawItemName = item.toString().replace("_", " ");
        final StringBuilder itemName = new StringBuilder(rawItemName);

        final boolean isItem = Block.getBlockFromItem(item) == Blocks.AIR;

        if (!rawItemName.contains("block") && !rawItemName.contains("item")) {
            if (isItem) itemName.append(" item");
            else itemName.append(" block");
        }

        final NbtCompound nbt = stack.getOrCreateNbt();
        final int damage = stack.getDamage(), nbtCount = nbt.getKeys().size(), count = stack.getCount();
        if ((nbtCount == 1 && nbt.contains("Damage")) || (damage == 0 && nbtCount == 0)) {
            return;
        }

        final String itemNameString = itemName.toString();
        final boolean entityIsPlayer = entity instanceof PlayerEntity;
        final String entityName = (entityIsPlayer ? ((PlayerEntity) entity).getGameProfile().getName() : entity.getName().getString());
        final String entityType = (entityIsPlayer ? "Player" : entity.getType().getName().getString());
        final String itemFromText = itemNameString + " from " + entityType + " " + entityName;
        final String position = entity.getBlockPos().toShortString();

        this.executorService.submit(() -> {
            LOGGED_ITEMS_DIR.mkdirs();
            if (!LOGGED_ITEMS_DIR.exists()) return;

            final File serverDir = new File(LOGGED_ITEMS_DIR, ServerConnectionUtil.lastServerExists() && !mc.isInSingleplayer() && !mc.isIntegratedServerRunning() ? ServerConnectionUtil.getLastServerInfo().address : "single player");
            serverDir.mkdirs();
            if (!serverDir.exists()) return;

            final File playerOrEntityDir = new File(serverDir, entityType);
            playerOrEntityDir.mkdirs();
            if (!playerOrEntityDir.exists()) return;

            final File playerOrEntityNameDir = new File(playerOrEntityDir, entityName);
            playerOrEntityNameDir.mkdirs();
            if (!playerOrEntityNameDir.exists()) return;

            final File itemOrBlockDir = new File(playerOrEntityNameDir, isItem ? "item" : "block");
            itemOrBlockDir.mkdirs();
            if (!itemOrBlockDir.exists()) return;

            final File itemNameDir = new File(itemOrBlockDir, itemNameString.replace(" item", "").replace(" block", ""));
            itemNameDir.mkdirs();
            if (!itemNameDir.exists()) return;

            final File itemNbtFile = new File(itemNameDir, nbtCount + "-[" + String.valueOf(damage).hashCode() + nbt.hashCode() + "].nbt");
            if (itemNbtFile.exists()) return;
            try {
                itemNbtFile.createNewFile();
                final NbtCompound itemNbt = new NbtCompound();
                itemNbt.putString("At Date", this.formatter.format(new Date()));
                itemNbt.putString("At Position", position);
                itemNbt.putInt("Damage", damage);
                itemNbt.putInt("Count", count);
                itemNbt.putInt("NBT Count", nbtCount);
                itemNbt.put("NBT", nbt);
                NbtIo.write(itemNbt, itemNbtFile.toPath());
                final String normalWithoutNBT = "Position: " + position + " | Damage: " + damage + " | Count: " + count + " | NBT Count: " + nbtCount;
                if (this.notifyInChat.getValue()) {
                    final MutableText text = Text.literal("Options:");
                    final MutableText copyButton = Text.literal(" (Copy Data)");
                    copyButton.setStyle(copyButton.getStyle().withFormatting(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, normalWithoutNBT + " | [NBT] " + nbt)));
                    final MutableText copyGiveCommandButton = Text.literal(" (Copy Give Command)");
                    copyGiveCommandButton.setStyle(copyGiveCommandButton.getStyle().withFormatting(Formatting.YELLOW).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, Vandalism.getInstance().getClientSettings().getChatSettings().commandPrefix.getValue() + "give " + item + nbt + " " + count)));
                    final MutableText openDirectoryButton = Text.literal(" (Open Directory)");
                    openDirectoryButton.setStyle(openDirectoryButton.getStyle().withFormatting(Formatting.GOLD).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, itemNbtFile.getParent())));
                    final MutableText openFileButton = Text.literal(" (Open File)");
                    openFileButton.setStyle(openFileButton.getStyle().withFormatting(Formatting.RED).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, itemNbtFile.getAbsolutePath())));
                    text.append(copyButton).append(copyGiveCommandButton).append(openDirectoryButton).append(openFileButton);
                    final MutableText displayNBTButton = Text.literal(" (Display NBT)");
                    Style style = displayNBTButton.getStyle();
                    style = style.withFormatting(Formatting.DARK_RED);
                    final NbtCompound copy = nbt.copy();
                    copy.putString(NbtCommand.DISPLAY_TITLE_NBT_KEY, itemFromText);
                    final String command = "nbt displaynbt " + NbtHelper.toPrettyPrintedText(copy).getString();
                    final ClickEvent clickEvent = Vandalism.getInstance().getCommandManager().generateClickEvent(command);
                    style = style.withClickEvent(clickEvent);
                    displayNBTButton.setStyle(style);
                    text.append(displayNBTButton);
                    ChatUtil.infoChatMessage(Text.literal("Item Stack Logger").formatted(Formatting.AQUA));
                    ChatUtil.chatMessage(Text.literal("Found a " + itemFromText + ".").formatted(Formatting.DARK_AQUA), false);
                    ChatUtil.chatMessage(Text.literal(normalWithoutNBT).formatted(Formatting.LIGHT_PURPLE), false);
                    ChatUtil.chatMessage(text.formatted(Formatting.DARK_GREEN), false);
                }

            } catch (Throwable throwable) {
                Vandalism.getInstance().getLogger().error("Failed to log stack!", throwable);
            }
        });
    }

}
*/
