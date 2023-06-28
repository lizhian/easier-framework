package easier.framework.starter.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 多消息队列源配置
 */
@Data
@ConfigurationProperties(prefix = EasierMQProperties.prefix)
public class EasierMQProperties {
    public static final String prefix = "easier.mq";
    private String enableKafkaProducer;
    private Map<String, ProducerProperties> kafkaProducer = new HashMap<>();

    private String enableKafkaConsumer;
    private Map<String, ConsumerProperties> kafkaConsumer = new HashMap<>();

    @Data
    public static class ProducerProperties {
        private String alias;
        private String bootstrapServers;
        private int batchSize = 1000;
        private String plainLoginModuleUsername;
        private String plainLoginModulePassword;
        private Map<String, Object> properties = new HashMap<>();
    }

    @Data
    public static class ConsumerProperties {
        private String alias;
        private String bootstrapServers;
        private String groupId = "default";
        private Boolean enableAutoCommit = true;
        private Integer maxPollRecords = 500;
        private String plainLoginModuleUsername;
        private String plainLoginModulePassword;
        private Map<String, Object> properties = new HashMap<>();
    }
}
