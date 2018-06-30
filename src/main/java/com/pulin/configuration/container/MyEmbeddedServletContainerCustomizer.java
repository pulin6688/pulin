package com.pulin.configuration.container;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.catalina.valves.Constants;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.concurrent.TimeUnit;

/**
 * Created by pulin on 2018/6/30.
 */
public class MyEmbeddedServletContainerCustomizer implements EmbeddedServletContainerCustomizer {

    final static Logger logger = LoggerFactory.getLogger(MyEmbeddedServletContainerCustomizer.class);

    public void customize(ConfigurableEmbeddedServletContainer container) {
        container.setSessionTimeout(1, TimeUnit.MINUTES);
        //org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory
        //说明默认是的Tomcat容器
        logger.info(container.getClass().toString());
        TomcatEmbeddedServletContainerFactory factory = (TomcatEmbeddedServletContainerFactory) container;
        //设置端口
        factory.setPort(8088);
        //设置404错误界面
        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404.html"));

        //设置Tomcat的根目录
       // factory.setBaseDirectory(new File("d:/tmp/tomcat"));

        //设置访问日志存放目录
        factory.addContextValves(getLogAccessLogValue());

        //设置Tomcat线程数和连接数
        factory.addConnectorCustomizers(new MyTomcatConnectorCustomizer());

        //初始化servletContext对象
        factory.addInitializers(new ServletContextInitializer(){

            public void onStartup(ServletContext servletContext) throws ServletException {
                logger.info("= = = = 获取服务器信息 = = :{}",servletContext.getServerInfo() );

            }
        });

//        factory.addInitializers((servletContext) -> {
//            System.out.println(" = = = = 获取服务器信息 = = " + servletContext.getServerInfo());
//        });



    }


    private AccessLogValve getLogAccessLogValue() {
        AccessLogValve accessLogValve = new AccessLogValve();
        //accessLogValve.setDirectory("d:/tmp/tomcat/logs");
        accessLogValve.setEnabled(true);
        accessLogValve.setPattern(Constants.AccessLog.COMMON_PATTERN);
        accessLogValve.setPrefix("springboot-access-log");
        accessLogValve.setSuffix(".log");
        return accessLogValve;
    }


}




class MyTomcatConnectorCustomizer implements TomcatConnectorCustomizer {

    final  Logger logger = LoggerFactory.getLogger(MyTomcatConnectorCustomizer.class);

    public void customize(Connector connector) {
        //连接协议 HTTP/1.1
        logger.info(connector.getProtocol());

        //连接协议处理器 org.apache.coyote.http11.Http11NioProtocol
        logger.info(connector.getProtocolHandler().getClass().toString());

        //Http11NioProtocol
        Http11NioProtocol protocolHandler = (Http11NioProtocol) connector.getProtocolHandler();

        // 设置最大连接数
        protocolHandler.setMaxConnections(2000);

        // 设置最大线程数
        protocolHandler.setMaxThreads(500);
    }
}
