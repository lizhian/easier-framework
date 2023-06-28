package easier.framework.starter.mq.kafka;

import cn.hutool.core.collection.CollUtil;
import easier.framework.core.plugin.cache.CacheBuilderException;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.mq.EasierMQProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.DisposableBean;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class KafkaConsumers implements DisposableBean {
    private final Map<String, List<KafkaConsumer<String, byte[]>>> consumers = new HashMap<>();
    private final Map<String, EasierMQProperties.ConsumerProperties> consumerProperties = new HashMap<>();
    ;
    private String primary;

    @Nonnull
    public synchronized KafkaConsumer<String, byte[]> create() {
        if (StrUtil.isBlank(this.primary)) {
            throw CacheBuilderException.of("未配置主kafka消费者");
        }
        KafkaConsumer<String, byte[]> consumer = this.create(this.primary);
        if (this.consumers.containsKey(this.primary)) {
            this.consumers.get(this.primary).add(consumer);
        } else {
            this.consumers.put(this.primary, CollUtil.newArrayList(consumer));
        }
        return consumer;
    }

    @Nonnull
    public synchronized KafkaConsumer<String, byte[]> create(String source) {
        if (StrUtil.isBlank(source)) {
            return this.create();
        }
        EasierMQProperties.ConsumerProperties consumerProperties = this.consumerProperties.get(source);
        if (consumerProperties == null) {
            throw CacheBuilderException.of("无法获取kafka消费者配置:{}", source);
        }
        KafkaConsumer<String, byte[]> consumer = this.createConsumer(consumerProperties);
        if (this.consumers.containsKey(source)) {
            this.consumers.get(source).add(consumer);
        } else {
            this.consumers.put(source, CollUtil.newArrayList(consumer));
            log.info("已加载kafka消费者【{}】", source);

        }
        return consumer;
    }


    @Override
    public void destroy() {
        this.consumers.values()
                      .stream()
                      .flatMap(List::stream)
                      .forEach(KafkaConsumer::close);
    }

    public void init(EasierMQProperties properties) {
        List<String> enable = StrUtil.smartSplit(properties.getEnableKafkaConsumer());
        if (CollUtil.isEmpty(enable)) {
            return;
        }
        this.primary = CollUtil.getFirst(enable);
        for (String source : enable) {
            EasierMQProperties.ConsumerProperties consumerProperties = properties.getKafkaConsumer().get(source);
            if (consumerProperties == null) {
                throw CacheBuilderException.of("无法获取kafka消费者配置:{}", source);
            }
            this.consumerProperties.put(source, consumerProperties);
            log.info("已加载kafka消费者配置【{}】", source);
            for (String alias : StrUtil.smartSplit(consumerProperties.getAlias())) {
                log.info("已加载kafka消费者配置【{}】", alias);
            }
        }
    }

    private KafkaConsumer<String, byte[]> createConsumer(EasierMQProperties.ConsumerProperties consumerProperties) {
        Map<String, Object> props = consumerProperties.getProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getBootstrapServers());
        if (consumerProperties.getGroupId() != null) {
            props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getGroupId());
        }
        if (consumerProperties.getEnableAutoCommit() != null) {
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerProperties.getEnableAutoCommit());
        }
        if (consumerProperties.getMaxPollRecords() != null) {
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumerProperties.getMaxPollRecords());
        }
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        String plainLoginModuleUsername = consumerProperties.getPlainLoginModuleUsername();
        String plainLoginModulePassword = consumerProperties.getPlainLoginModulePassword();
        if (StrUtil.isNotBlank(plainLoginModuleUsername) && StrUtil.isNotBlank(plainLoginModulePassword)) {
            String saslJaasConf = StrUtil.format("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"{}\" password=\"{}\";", plainLoginModuleUsername, plainLoginModulePassword);
            props.putIfAbsent(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConf);
            props.putIfAbsent(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name);
            props.putIfAbsent(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }
        return new KafkaConsumer<>(props);
    }
}
