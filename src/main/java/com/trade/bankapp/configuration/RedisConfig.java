package com.trade.bankapp.configuration;

import com.trade.bankapp.subscriber.Receiver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {
    @Bean
    public JedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName("139.59.180.219");
        configuration.setPort(6379);
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public ChannelTopic exchangeOneTopic() {
        return new ChannelTopic("MarketDataFromExchangeOne");
    }

    @Bean
    public ChannelTopic exchangeTwoTopic() {
        return new ChannelTopic("MarketDataFromExchangeTwo");
    }

    @Bean("marketDataFromExOneListenerAdapter")
    MessageListenerAdapter marketDataFromExchangeOneListenerAdapter() {
        return new MessageListenerAdapter(new Receiver(),"marketDataFromExchangeOne");
    }

    @Bean("marketDataFromExTwoListenerAdapter")
    MessageListenerAdapter marketDataFromExchangeTwoListenerAdapter() {
        return new MessageListenerAdapter(new Receiver(), "marketDataFromExchangeTwo");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.addMessageListener(marketDataFromExchangeOneListenerAdapter(), exchangeOneTopic());
        container.addMessageListener(marketDataFromExchangeTwoListenerAdapter(), exchangeTwoTopic());
        return container;
    }
}
