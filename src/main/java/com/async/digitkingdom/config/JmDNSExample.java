package com.async.digitkingdom.config;

import org.apache.ibatis.annotations.Delete;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

public class JmDNSExample {
    public static void main(String[] args) {
        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            System.out.println(InetAddress.getLocalHost().getHostName());

            ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local","NITRO5",0, String.valueOf(Inet4Address.getLocalHost()));
            // Register the service
            jmdns.registerService(serviceInfo);

            // Keep the service registered for a while (e.g., 60 seconds)
            Thread.sleep(60000);

            // Unregister all services
            jmdns.unregisterAllServices();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
