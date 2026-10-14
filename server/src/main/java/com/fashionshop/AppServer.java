package com.fashionshop;

import com.fashionshop.config.AppConfig;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.util.logging.Logger;

/**
 * Standalone embedded server runner for Fashion E-Commerce Servlet Backend.
 * Allows running the backend on port 5000 (or custom port) without requiring an external Tomcat installation.
 */
public class AppServer {

    private static final Logger logger = Logger.getLogger(AppServer.class.getName());

    public static void main(String[] args) throws Exception {
        int port = AppConfig.SERVER_PORT;
        String portProp = System.getProperty("server.port");
        if (portProp != null && !portProp.isBlank()) {
            port = Integer.parseInt(portProp);
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // Initialize default HTTP connector

        File baseDir = new File("target/tomcat." + port);
        baseDir.mkdirs();
        tomcat.setBaseDir(baseDir.getAbsolutePath());

        File webappDir = new File("src/main/webapp");
        String docBase = webappDir.exists() ? webappDir.getAbsolutePath() : new File(".").getAbsolutePath();

        Context ctx = tomcat.addWebapp("", docBase);

        // Mount compiled classes if present
        File additionWebInfClasses = new File("target/classes");
        if (additionWebInfClasses.exists()) {
            StandardRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(
                    resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        logger.info("=================================================");
        logger.info("Fashion E-Commerce Servlet Backend Booting...");
        logger.info("Port: " + port);
        logger.info("DocBase: " + docBase);
        logger.info("Base URL: " + AppConfig.SERVER_BASE_URL);
        logger.info("Endpoints available at: http://localhost:" + port + "/api/v1/*");
        logger.info("=================================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
