package easier.framework.starter.mq.kafka;

import cn.hutool.core.collection.CollUtil;
import easier.framework.core.plugin.cache.CacheBuilderException;
import easier.framework.core.plugin.mq.MQBuilderException;
import easier.framework.core.util.StrUtil;
import easier.framework.starter.mq.EasierMQProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.DisposableBean;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class KafkaProducers implements DisposableBean {
    private final Map<String, KafkaProducer<String, byte[]>> producers = new HashMap<>();
    private String primary;

    public boolean has(String source) {
        return this.producers.containsKey(source);
    }

    public boolean notHas(String source) {
        return !this.has(source);
    }

    @Nonnull
    public KafkaProducer<String, byte[]> get() {
        if (StrUtil.isBlank(this.primary)) {
            throw CacheBuilderException.of("未配置主kafka生产者");
        }
        KafkaProducer<String, byte[]> producer = this.producers.get(this.primary);
        if (producer == null) {
            throw CacheBuilderException.of("未找到kafka生产者:{}", this.primary);
        }
        return producer;
    }

    @Nonnull
    public KafkaProducer<String, byte[]> get(String source) {
        if (StrUtil.isBlank(source)) {
            return this.get();
        }
        KafkaProducer<String, byte[]> producer = this.producers.get(source);
        if (producer == null) {
            throw MQBuilderException.of("未找到kafka生产者:{}", source);
        }
        return producer;
    }


    @Override
    public void destroy() {
        this.producers.values().forEach(KafkaProducer::close);
    }

    public void init(EasierMQProperties properties) {
        List<String> enable = StrUtil.smartSplit(properties.getEnableKafkaProducer());
        if (CollUtil.isEmpty(enable)) {
            return;
        }
        this.primary = CollUtil.getFirst(enable);
        for (String key : enable) {
            EasierMQProperties.ProducerProperties producerProperties = properties.getKafkaProducer().get(key);
            if (producerProperties == null) {
                throw CacheBuilderException.of("无法获取kafka生产者配置:{}", key);
            }
            KafkaProducer<String, byte[]> producer = this.createProducer(producerProperties);
            this.producers.put(key, producer);
            log.info("已加载kafka生产者【{}】", key);
            for (String alias : StrUtil.smartSplit(producerProperties.getAlias())) {
                this.producers.put(alias, producer);
                log.info("已加载kafka生产者【{}】", alias);
            }
        }
    }

    private KafkaProducer<String, byte[]> createProducer(EasierMQProperties.ProducerProperties producerProperties) {
        Map<String, Object> props = producerProperties.getProperties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProperties.getBootstrapServers());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, producerProperties.getBatchSize());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        String plainLoginModuleUsername = producerProperties.getPlainLoginModuleUsername();
        String plainLoginModulePassword = producerProperties.getPlainLoginModulePassword();
        if (StrUtil.isNotBlank(plainLoginModuleUsername) && StrUtil.isNotBlank(plainLoginModulePassword)) {
            String saslJaasConf = StrUtil.format("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"{}\" password=\"{}\";", plainLoginModuleUsername, plainLoginModulePassword);
            props.putIfAbsent(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConf);
            props.putIfAbsent(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name);
            props.putIfAbsent(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }
        return new KafkaProducer<>(props);
    }
}
