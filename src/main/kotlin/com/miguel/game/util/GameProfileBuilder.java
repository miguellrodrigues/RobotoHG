/*
 *
 *  * Copyright (©) RobotoHG-1.8
 *  *
 *  * Projeto desenvolvido por Miguell Rodrigues
 *  * Todos os direitos reservados
 *  *
 *  * Modificado em: 23/05/2020 18:17
 *
 */

package com.miguel.game.util;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameProfileBuilder {

    private static final String SERVICE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final String JSON_SKIN = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}";
    private static final String JSON_CAPE = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"},\"CAPE\":{\"url\":\"%s\"}}}";
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).registerTypeAdapter(GameProfile.class, new GameProfileSerializer()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
    private static final HashMap<UUID, CachedProfile> cache = new HashMap<UUID, CachedProfile>();
    private static long cacheTime = -1;

    /**
     * Don't run in main thread!
     * <p>
     * Fetches the GameProfile from the Mojang servers
     *
     * @param uuid The player uuid
     * @return The GameProfile
     * @throws IOException If something wents wrong while fetching
     * @see GameProfile
     */
    public static GameProfile fetch(UUID uuid) throws IOException {
        return fetch(uuid, false);
    }

    /**
     * Don't run in main thread!
     * <p>
     * Fetches the GameProfile from the Mojang servers
     *
     * @param uuid     The player uuid
     * @param forceNew If true the cache is ignored
     * @return The GameProfile
     * @throws IOException If something wents wrong while fetching
     * @see GameProfile
     */
    public static GameProfile fetch(UUID uuid, boolean forceNew) throws IOException {
        if (!forceNew && cache.containsKey(uuid) && cache.get(uuid).isValid()) {
            return cache.get(uuid).profile;
        } else {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(SERVICE_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
            connection.setReadTimeout(5000);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String json = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                GameProfile result = gson.fromJson(json, GameProfile.class);
                cache.put(uuid, new CachedProfile(result));
                return result;
            } else {
                if (!forceNew && cache.containsKey(uuid)) {
                    return cache.get(uuid).profile;
                }
                JsonObject error = (JsonObject) new JsonParser().parse(new BufferedReader(new InputStreamReader(connection.getErrorStream())).readLine());
                throw new IOException(error.get("error").getAsString() + ": " + error.get("errorMessage").getAsString());
            }
        }
    }

    /**
     * Builds a GameProfile for the specified args
     *
     * @param uuid The uuid
     * @param name The name
     * @param skin The url from the skin image
     * @return A GameProfile built from the arguments
     * @see GameProfile
     */
    public static GameProfile getProfile(UUID uuid, String name, String skin) {
        return getProfile(uuid, name, skin, null);
    }

    /**
     * Builds a GameProfile for the specified args
     *
     * @param uuid    The uuid
     * @param name    The name
     * @param skinUrl Url from the skin image
     * @param capeUrl Url from the cape image
     * @return A GameProfile built from the arguments
     * @see GameProfile
     */
    public static GameProfile getProfile(UUID uuid, String name, String skinUrl, String capeUrl) {
        GameProfile profile = new GameProfile(uuid, name);
        boolean cape = capeUrl != null && !capeUrl.isEmpty();
        List<Object> args = new ArrayList<>();
        args.add(System.currentTimeMillis());
        args.add(UUIDTypeAdapter.fromUUID(uuid));
        args.add(name);
        args.add(skinUrl);
        if (cape) args.add(capeUrl);
        profile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString(String.format(cape ? JSON_CAPE : JSON_SKIN, args.toArray(new Object[args.size()])))));
        return profile;
    }

    /**
     * Sets the time as long as you want to keep the gameprofiles in cache (-1 = never remove it)
     *
     * @param time cache time (default = -1)
     */
    public static void setCacheTime(long time) {
        cacheTime = time;
    }

    private static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {
        public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = (JsonObject) json;
            UUID id = object.has("id") ? (UUID) context.deserialize(object.get("id"), UUID.class) : null;
            String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
            GameProfile profile = new GameProfile(id, name);
            if (object.has("properties")) {
                for (Map.Entry<String, Property> prop : ((PropertyMap) context.deserialize(object.get("properties"), PropertyMap.class)).entries()) {
                    profile.getProperties().put(prop.getKey(), prop.getValue());
                }
            }
            return profile;
        }

        public JsonElement serialize(GameProfile profile, Type type, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            if (profile.getId() != null)
                result.add("id", context.serialize(profile.getId()));
            if (profile.getName() != null)
                result.addProperty("name", profile.getName());
            if (!profile.getProperties().isEmpty())
                result.add("properties", context.serialize(profile.getProperties()));
            return result;
        }
    }

    private static class CachedProfile {
        private final long timestamp = System.currentTimeMillis();
        private final GameProfile profile;

        public CachedProfile(GameProfile profile) {
            this.profile = profile;
        }

        public boolean isValid() {
            return cacheTime < 0 || (System.currentTimeMillis() - timestamp) < cacheTime;
        }
    }

    public static class UUIDFetcher {
        /**
         * Date when name changes were introduced
         *
         * @see UUIDFetcher#getUUIDAt(String, long)
         */
        private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
        private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";
        private static final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
        private static final Map<String, UUID> uuidCache = new HashMap<>();
        private static final Map<UUID, String> nameCache = new HashMap<>();
        private static final ExecutorService pool = Executors.newCachedThreadPool();
        private String name;
        private UUID id;

        /**
         * Fetches the uuid asynchronously and passes it to the consumer
         *
         * @param name   The name
         * @param action Do what you want to do with the uuid her
         */
        public static void getUUID(final String name, Consumer<UUID> action) {
            pool.execute(new Acceptor<UUID>(action) {
                @Override
                public UUID getValue() {
                    return getUUID(name);
                }
            });
        }

        /**
         * Fetches the uuid synchronously and returns it
         *
         * @param name The name
         * @return The uuid
         */
        public static UUID getUUID(String name) {
            return getUUIDAt(name, System.currentTimeMillis());
        }

        /**
         * Fetches the uuid synchronously for a specified name and time and passes the result to the consumer
         *
         * @param name      The name
         * @param timestamp Time when the player had this name in milliseconds
         * @param action    Do what you want to do with the uuid her
         */
        public static void getUUIDAt(final String name, final long timestamp, Consumer<UUID> action) {
            pool.execute(new Acceptor<UUID>(action) {
                @Override
                public UUID getValue() {
                    return getUUIDAt(name, timestamp);
                }
            });
        }

        /**
         * Fetches the uuid synchronously for a specified name and time
         *
         * @param name      The name
         * @param timestamp Time when the player had this name in milliseconds
         */
        public static UUID getUUIDAt(String name, long timestamp) {
            name = name.toLowerCase();
            if (uuidCache.containsKey(name)) {
                return uuidCache.get(name);
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, timestamp / 1000)).openConnection();
                connection.setReadTimeout(5000);
                UUIDFetcher data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);
                uuidCache.put(name, data.id);
                nameCache.put(data.id, data.name);
                return data.id;
            } catch (Exception ignored) {
            }
            return null;
        }

        /**
         * Fetches the name asynchronously and passes it to the consumer
         *
         * @param uuid   The uuid
         * @param action Do what you want to do with the name her
         */
        public static void getName(final UUID uuid, Consumer<String> action) {
            pool.execute(new Acceptor<String>(action) {
                @Override
                public String getValue() {
                    return getName(uuid);
                }
            });
        }

        /**
         * Fetches the name synchronously and returns it
         *
         * @param uuid The uuid
         * @return The name
         */
        public static String getName(UUID uuid) {
            if (nameCache.containsKey(uuid)) {
                return nameCache.get(uuid);
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
                connection.setReadTimeout(5000);
                UUIDFetcher[] nameHistory = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher[].class);
                UUIDFetcher currentNameData = nameHistory[nameHistory.length - 1];
                uuidCache.put(currentNameData.name.toLowerCase(), uuid);
                nameCache.put(uuid, currentNameData.name);
                return currentNameData.name;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public interface Consumer<T> {
            void accept(T t);
        }

        public static abstract class Acceptor<T> implements Runnable {
            private final Consumer<T> consumer;

            public Acceptor(Consumer<T> consumer) {
                this.consumer = consumer;
            }

            public abstract T getValue();

            @Override
            public void run() {
                consumer.accept(getValue());
            }
        }
    }
}
