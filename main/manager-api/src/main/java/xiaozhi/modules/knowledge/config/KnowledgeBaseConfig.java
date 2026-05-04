package xiaozhi.modules.knowledge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xiaozhi.modules.knowledge.rag.KnowledgeBaseAdapterFactory;

/**
 * Knowledge base configuration class
 * Configure knowledge base related beans
 */
@Configuration
public class KnowledgeBaseConfig {

    /**
     * Provide a Bean instance of KnowledgeBaseAdapterFactory
     * @return KnowledgeBaseAdapterFactory instance
     */
    @Bean
    public KnowledgeBaseAdapterFactory knowledgeBaseAdapterFactory() {
        return new KnowledgeBaseAdapterFactory();
    }
}
 