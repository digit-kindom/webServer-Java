package com.async.digitkingdom.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Configuration
public class WebServerConfig implements ServletContextInitializer {
    
    @Bean
    public ServerEndpointExporter ServerEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

    }
}
