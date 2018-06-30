package com.pulin.configuration.container;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by pulin on 2018/6/30.
 */
 //@Configuration
public class WebServerConfiguration {

    @Bean
    @ConditionalOnMissingBean(EmbeddedServletContainerCustomizer.class)
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatFactory.setPort(8081);
        tomcatFactory.addConnectorCustomizers(new MyTomcatConnectorCustomizer());
        return tomcatFactory;
    }

    @Bean
    @ConditionalOnMissingBean(EmbeddedServletContainerCustomizer.class)
    public EmbeddedServletContainerFactory createFactory() {
        return new MyEmbeddedServletContainerCustomizerFactory();
    }

    @Bean
    @ConditionalOnMissingBean(EmbeddedServletContainerCustomizer.class)
    public EmbeddedServletContainerCustomizer createEmbeddedServletContainerCustomizer() {
        return new MyEmbeddedServletContainerCustomizer();
    }


}



