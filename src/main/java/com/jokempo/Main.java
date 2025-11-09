package com.jokempo;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import com.jokempo.controller.GameServlet;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting Jokempo Server...");

        Server server = new Server(8080);

        // Create servlet context handler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add GameServlet
        context.addServlet(new ServletHolder(GameServlet.class), "/api/game/*");

        // Serve static files
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newClassPathResource("static"));
        resourceHandler.setDirectoriesListed(true);
        context.insertHandler(resourceHandler);

        server.start();
        System.out.println("Server started on http://localhost:8080");
        server.join();
    }
}
