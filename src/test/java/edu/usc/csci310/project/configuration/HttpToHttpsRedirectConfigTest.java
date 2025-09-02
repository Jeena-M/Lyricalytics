//package edu.usc.csci310.project.configuration;//package edu.usc.csci310.project.configuration;
//
//import org.apache.catalina.Context;
//import org.apache.catalina.connector.Connector;
//import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//public class HttpToHttpsRedirectConfigTest {
//    @Test
//    public void servletContainerTest() {
//        HttpToHttpsRedirectConfig config = new HttpToHttpsRedirectConfig();
//        TomcatServletWebServerFactory tomcat = mock(TomcatServletWebServerFactory.class);
//        ArgumentCaptor<Connector> factoryCaptor = ArgumentCaptor.forClass(Connector.class);
//        WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer = config.servletContainer();
//
//        tomcatCustomizer.customize(tomcat);
//        verify(tomcat).addAdditionalTomcatConnectors(factoryCaptor.capture());
//        Connector connector = factoryCaptor.getValue();
//        assertEquals(8080, connector.getPort());
//    }
//
//    @Test
//    public void addSecurityConstraintTest() {
//        HttpToHttpsRedirectConfig config = new HttpToHttpsRedirectConfig();
//        Context context = mock(Context.class);
//        ArgumentCaptor<SecurityConstraint> constraintCaptor = ArgumentCaptor.forClass(SecurityConstraint.class);
//        config.addSecurityConstraint(context);
//        verify(context).addConstraint(constraintCaptor.capture());
//        SecurityConstraint constraint = constraintCaptor.getValue();
//        assertEquals("CONFIDENTIAL", constraint.getUserConstraint());
//    }
//}
