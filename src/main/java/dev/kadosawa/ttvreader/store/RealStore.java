package dev.kadosawa.ttvreader.store;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import dev.kadosawa.ttvreader.config.ModConfig;
import dev.kadosawa.ttvreader.listeners.TwitchChatListener;
import me.shedaniel.autoconfig.AutoConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class RealStore {
    private static final ExecutorService executor = Executors.newFixedThreadPool(1);
    public final static Logger LOGGER = LogManager.getLogger();

    @Nullable
    private static String channelName = null;

    @Nullable
    private static Twirk twirk = null;

    public static @Nullable String getChannelName() {
        return channelName;
    }

    public static void setChannelName(@NotNull String value) {
        channelName = value;
    }

    public static @Nullable Twirk getTwirk() {
        return twirk;
    }

    public static void initialState() {
        channelName = null;

        if (twirk != null)
            twirk.close();

        twirk = null;
    }

    public static CompletableFuture<Boolean> createTwirkConnection() {
        return CompletableFuture
                .supplyAsync(() -> {
                    LOGGER.debug("Trying to connect to Twitch...");

                    ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

                    twirk = new TwirkBuilder(channelName, config.username, config.OAuthToken).build();
                    twirk.addIrcListener(new TwitchChatListener());

                    try {
                        return twirk.connect();
                    } catch (IOException | InterruptedException e) {
                        initialState();
                        return false;
                    }
                }, executor);
    }
}
