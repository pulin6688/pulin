package com.pulin.configuration.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

/**
 * Created by pulin on 2018/6/30.
 */
@Component
public class ExampleInfoContributor implements InfoContributor {
    @Autowired
    private Environment environment;
    public void contribute(Info.Builder builder) {
        String active = environment.getProperty("spring.profiles.active");
        builder.withDetail("time", Collections.singletonMap("startTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date())));
        builder.withDetail("active", Collections.singletonMap("active", active));
    }
}
