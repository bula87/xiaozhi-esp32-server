package xiaozhi.modules.knowledge.rag;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import xiaozhi.common.exception.ErrorCode;
import xiaozhi.common.exception.RenException;

/**
 * Knowledge base adapter factory class
 * Responsible for creating and managing different types of knowledge base API adapters
 */
@Slf4j
public class KnowledgeBaseAdapterFactory {

    // Registered adapter type mapping
    private static final Map<String, Class<? extends KnowledgeBaseAdapter>> adapterRegistry = new HashMap<>();

    // Adapter instance cache
    private static final Map<String, KnowledgeBaseAdapter> adapterCache = new ConcurrentHashMap<>();

    // Maximum number of cached instances to prevent memory leaks (Issue 9)
    private static final int MAX_CACHE_SIZE = 50;

    static {
        // Register built-in adapter types
        registerAdapter("ragflow", xiaozhi.modules.knowledge.rag.impl.RAGFlowAdapter.class);
        // More adapter types can be registered here
    }

    /**
     * Register a new adapter type
     * 
     * @param adapterType  Adapter type identifier
     * @param adapterClass Adapter class
     */
    public static void registerAdapter(String adapterType, Class<? extends KnowledgeBaseAdapter> adapterClass) {
        if (adapterRegistry.containsKey(adapterType)) {
            log.warn("Adapter type '{}' already exists and will be overwritten", adapterType);
        }
        adapterRegistry.put(adapterType, adapterClass);
        log.info("Register adapter type: {} -> {}", adapterType, adapterClass.getSimpleName());
    }

    /**
     * Get adapter instance
     * 
     * @param adapterType Adapter type
     * @param config      Configuration parameters
     * @return Adapter instance
     */
    public static KnowledgeBaseAdapter getAdapter(String adapterType, Map<String, Object> config) {
        String cacheKey = buildCacheKey(adapterType, config);

        // Check if an instance already exists in the cache
        if (adapterCache.containsKey(cacheKey)) {
            log.debug("Get adapter instance from cache: {}", cacheKey);
            return adapterCache.get(cacheKey);
        }

        // Create a new adapter instance
        KnowledgeBaseAdapter adapter = createAdapter(adapterType, config);

        // Cache the adapter instance (with capacity limit check)
        if (adapterCache.size() >= MAX_CACHE_SIZE) {
            log.warn("Adapter cache has reached its limit ({}), executing memory protection clear", MAX_CACHE_SIZE);
            // Simple handling: Clear all directly, recommended to use LRU in production environment
            adapterCache.clear();
        }

        adapterCache.put(cacheKey, adapter);
        log.info("Create and cache adapter instance: {}", cacheKey);

        return adapter;
    }

    /**
     * Get adapter instance (no configuration)
     * 
     * @param adapterType Adapter type
     * @return Adapter instance
     */
    public static KnowledgeBaseAdapter getAdapter(String adapterType) {
        return getAdapter(adapterType, null);
    }

    /**
     * Get all registered adapter types
     * 
     * @return Set of adapter types
     */
    public static Set<String> getRegisteredAdapterTypes() {
        return adapterRegistry.keySet();
    }

    /**
     * Check if an adapter type is registered
     * 
     * @param adapterType Adapter type
     * @return Whether it is registered
     */
    public static boolean isAdapterTypeRegistered(String adapterType) {
        return adapterRegistry.containsKey(adapterType);
    }

    /**
     * Clear adapter cache
     */
    public static void clearCache() {
        int cacheSize = adapterCache.size();
        adapterCache.clear();
        log.info("Clear adapter cache, cleared {} instances", cacheSize);
    }

    /**
     * Remove cache for a specific adapter type
     * 
     * @param adapterType Adapter type
     */
    public static void removeCacheByType(String adapterType) {
        int removedCount = 0;
        for (String cacheKey : adapterCache.keySet()) {
            if (cacheKey.startsWith(adapterType + "@")) {
                adapterCache.remove(cacheKey);
                removedCount++;
            }
        }
        log.info("Remove cache for adapter type '{}', removed {} instances", adapterType, removedCount);
    }

    /**
     * Get adapter factory status information
     * 
     * @return Status information
     */
    public static Map<String, Object> getFactoryStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("registeredAdapterTypes", adapterRegistry.keySet());
        status.put("cachedAdapterCount", adapterCache.size());
        status.put("cacheKeys", adapterCache.keySet());
        return status;
    }

    /**
     * Create an adapter instance
     * 
     * @param adapterType Adapter type
     * @param config      Configuration parameters
     * @return Adapter instance
     */
    private static KnowledgeBaseAdapter createAdapter(String adapterType, Map<String, Object> config) {
        if (!adapterRegistry.containsKey(adapterType)) {
            throw new RenException(ErrorCode.RAG_ADAPTER_TYPE_NOT_SUPPORTED,
                    "Unsupported adapter type: " + adapterType);
        }

        try {
            Class<? extends KnowledgeBaseAdapter> adapterClass = adapterRegistry.get(adapterType);
            KnowledgeBaseAdapter adapter = adapterClass.getDeclaredConstructor().newInstance();

            // Initialize the adapter
            if (config != null) {
                adapter.initialize(config);

                // Validate configuration
                if (!adapter.validateConfig(config)) {
                    throw new RenException(ErrorCode.RAG_CONFIG_VALIDATION_FAILED,
                            "Adapter configuration validation failed: " + adapterType);
                }
            }

            log.info("Successfully created adapter instance: {}", adapterType);
            return adapter;

        } catch (Exception e) {
            log.error("Failed to create adapter instance: {}", adapterType, e);
            throw new RenException(ErrorCode.RAG_ADAPTER_CREATION_FAILED,
                    "Adapter creation failed: " + adapterType + ", error: " + e.getMessage());
        }
    }

    /**
     * Build cache key
     * 
     * @param adapterType Adapter type
     * @param config      Configuration parameters
     * @return Cache key
     */
    private static String buildCacheKey(String adapterType, Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            return adapterType + "@default";
        }

        // Generate cache key based on configuration parameters
        StringBuilder keyBuilder = new StringBuilder(adapterType + "@");

        // Use the hash of the configuration as part of the cache key
        int configHash = config.hashCode();
        keyBuilder.append(configHash);

        return keyBuilder.toString();
    }
}
 