//package edu.usc.csci310.project.configuration;//package edu.usc.csci310.project.configuration;
//
//import org.apache.catalina.Context;
//import org.apache.catalina.connector.Connector;
//import org.apache.tomcat.util.descriptor.web.SecurityCollection;
//import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class HttpToHttpsRedirectConfig {
//
//        @Bean
//        public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
//            return server -> {
//                // Create an additional HTTP connector that listens on port 8080
//                Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
//                connector.setScheme("http");
//                connector.setPort(8080);
//                connector.setSecure(false);
//                connector.setRedirectPort(8443);  // Where HTTP traffic should be redirected
//                server.addAdditionalTomcatConnectors(connector);
//
//                // Configure a global security constraint that forces HTTPS
//                server.addContextCustomizers(this::addSecurityConstraint);
//            };
//        }
//
//        protected void addSecurityConstraint(Context context) {
//            SecurityConstraint securityConstraint = new SecurityConstraint();
//            securityConstraint.setUserConstraint("CONFIDENTIAL");  // This marks the connection as secure
//            SecurityCollection collection = new SecurityCollection();
//            collection.addPattern("/*");  // Apply this security constraint to all paths
//            securityConstraint.addCollection(collection);
//            context.addConstraint(securityConstraint);
//        }
//    }
