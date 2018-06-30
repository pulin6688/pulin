package com.pulin.configuration.container;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.http.HttpStatus;

import java.util.concurrent.TimeUnit;

/**
 * Created by pulin on 2018/6/30.
 */
public class MyEmbeddedServletContainerCustomizerFactory extends TomcatEmbeddedServletContainerFactory {

    final static Logger logger = LoggerFactory.getLogger(MyEmbeddedServletContainerCustomizerFactory.class);

    public EmbeddedServletContainer getEmbeddedServletContainer(ServletContextInitializer... initializers) {
        //设置端口
        this.setSessionTimeout(1, TimeUnit.MINUTES);
        this.setPort(8088);
        //设置404错误界面
        this.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404.html"));
        return super.getEmbeddedServletContainer(initializers);
    }

    protected void customizeConnector(Connector connector) {
        super.customizeConnector(connector);
        Http11NioProtocol protocol = (Http11NioProtocol)connector.getProtocolHandler();
        //设置最大连接数
        protocol.setMaxConnections(2000);
        //设置最大线程数
        protocol.setMaxThreads(2000);
        protocol.setConnectionTimeout(30000);
    }



}
