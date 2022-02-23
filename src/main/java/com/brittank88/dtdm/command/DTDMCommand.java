package com.brittank88.dtdm.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.command.argument.EntityArgumentType;

import java.util.Collections;

public class DTDMCommand {

    public static void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("dtdm")
                .then(ClientCommandManager.literal("calculate")
                        .then(ClientCommandManager.argument("expression", StringArgumentType.string())
                                .executes(context -> 0) // Without selector.
                                .then(ClientCommandManager.argument("selector", EntityArgumentType.players())
                                        .executes(context -> 0) // With selector.
                                )
                        )
                ).then(ClientCommandManager.literal("function")
                        .then(ClientCommandManager.literal("list")
                                .executes(context -> 0)
                        ).then(ClientCommandManager.literal("add")
                                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                        .suggests(new UniversalSuggestionProvider<>(ignored -> Collections.singletonList("f" + 1)))
                                        .then(ClientCommandManager.argument("function", StringArgumentType.string())
                                                .executes(context -> 0)
                                        )
                                )
                        ).then(ClientCommandManager.literal("remove")
                                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                        .executes(context -> 0)
                                )
                        ).then(ClientCommandManager.literal("edit")
                                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                        .then(ClientCommandManager.argument("function", StringArgumentType.string())
                                                .executes(context -> 0)
                                        )
                                )
                        )
                ).then(ClientCommandManager.literal("constants")
                        .then(ClientCommandManager.literal("list")
                                .executes(context -> 0)
                        ).then(ClientCommandManager.literal("add")
                                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                        .suggests(new UniversalSuggestionProvider<>(ignored -> Collections.singletonList("c" + 1)))
                                        .then(ClientCommandManager.argument("constant", StringArgumentType.string())
                                                .executes(context -> 0)
                                        )
                                )
                        ).then(ClientCommandManager.literal("remove")
                                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                        .executes(context -> 0)
                                )
                        ).then(ClientCommandManager.literal("edit")
                                .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                        .then(ClientCommandManager.argument("constant", StringArgumentType.string())
                                                .executes(context -> 0)
                                        )
                                )
                        )
                )
        );
    }
}
