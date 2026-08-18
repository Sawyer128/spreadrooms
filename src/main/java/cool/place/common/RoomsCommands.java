package cool.place.common;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

public class RoomsCommands {

    public static void register() {

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, buildContext, selection) -> {
                    dispatcher.register(
                            Commands.literal("spreadrooms").requires(source ->
                                            source.permissions().hasPermission(
                                                    Permissions.COMMANDS_OWNER
                                            )
                                    )
                                    .then(
                                            Commands.literal("start").executes(context -> {
                                                if (RoomsController.begun) { context.getSource().sendFailure(Component.literal("They have already begun.")); return 0; }
                                                context.getSource().sendSuccess(() -> Component.literal("They spread."), false);
                                                if (context.getSource().getPlayer() != null) RoomsController.start(context.getSource().getServer(), context.getSource().getPlayer());
                                                return 1;
                                            })
                                    )
                                    .then(
                                            Commands.literal("stop").executes(context -> {
                                                if (!RoomsController.begun) { context.getSource().sendFailure(Component.literal("They have not begun.")); return 0; }
                                                context.getSource().sendSuccess(() -> Component.literal("They die."), false);
                                                if (context.getSource().getPlayer() != null) RoomsController.stop(context.getSource().getServer(), context.getSource().getPlayer());
                                                return 1;
                                            })
                                    )
                                    .then(
                                            Commands.literal("rooms_count").executes(context -> {
                                                if (!RoomsController.begun) { context.getSource().sendFailure(Component.literal("They have not begun.")); return 0; }
                                                context.getSource().sendSuccess(() -> Component.literal(
                                                        "Ticking rooms: " + RoomsController.rooms.size() + ". New rooms: " + RoomsController.newRooms.size() + ". Completed rooms: " + RoomsController.completeRooms.size() + "."
                                                ), false);
                                                return 1;
                                            })
                                    )
                                    .then(
                                            Commands.literal("rooms_ticking").executes(context -> {
                                                if (!RoomsController.begun) { context.getSource().sendFailure(Component.literal("They have not begun.")); return 0; }
                                                context.getSource().sendSuccess(() -> Component.literal(
                                                        "Ticking rooms: " + RoomsController.rooms + "."
                                                ), false);
                                                return 1;
                                            })
                                    )
                    );
                }
        );
    }
}
