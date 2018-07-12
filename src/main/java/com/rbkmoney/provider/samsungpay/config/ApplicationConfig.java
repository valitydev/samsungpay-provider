package com.rbkmoney.provider.samsungpay.config;

import com.rbkmoney.damsel.payment_tool_provider.PaymentToolProviderSrv;
import com.rbkmoney.provider.samsungpay.iface.decrypt.ProviderHandler;
import com.rbkmoney.provider.samsungpay.service.SPayClient;
import com.rbkmoney.provider.samsungpay.service.SPayService;
import com.rbkmoney.provider.samsungpay.store.SPKeyStore;
import com.rbkmoney.woody.api.flow.WFlow;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vpankrashkin on 04.07.18.
 */
@Configuration
public class ApplicationConfig {
    @Bean
    public SPayClient transactionClient(
            @Value("${samsung.trans_url_template}") String transactionURLTemplate,
            @Value("${samsung.cred_url_template}") String credentialsURLTemplate,
            @Value("${samsung.conn_timeout_ms}") int connTimeoutMs,
            @Value("${samsung.read_timeout_ms}") int readTimeoutMs,
            @Value("${samsung.write_timeout_ms}") int writeTimeoutMs) {
        return new SPayClient(transactionURLTemplate, credentialsURLTemplate, connTimeoutMs, readTimeoutMs, writeTimeoutMs);
    }

    @Bean
    public SPKeyStore keyStore(@Value("${keys_path}") String keysPath) {
        return new SPKeyStore(keysPath);
    }

    @Bean
    public SPayService transactionService(SPayClient SPayClient, SPKeyStore spKeyStore) {
        return new SPayService(SPayClient, spKeyStore);
    }

    @Bean
    public PaymentToolProviderSrv.Iface providerHandler(SPayService sPayService) {
        return new ProviderHandler(sPayService);
    }


    @Bean
    public ServletWebServerFactory servletContainer(@Value("${server.rest_port}") int httpPort) {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        Connector connector = new Connector();
        connector.setPort(httpPort);
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }

    @Bean
    public FilterRegistrationBean externalPortRestrictingFilter(@Value("${server.rest_port}") int restPort, @Value("/${server.rest_path_prefix}/") String httpPathPrefix) {
        Filter filter = new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                if (request.getLocalPort() == restPort) {
                    if (!(request.getServletPath().startsWith(httpPathPrefix) || request.getServletPath().startsWith("/actuator/health"))) {
                        response.sendError(404, "Unknown address");
                        return;
                    }
                }
                filterChain.doFilter(request, response);
            }
        };

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(-100);
        filterRegistrationBean.setName("httpPortFilter");
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean woodyFilter(@Value("${server.rest_port}") int restPort, @Value("/${server.rest_path_prefix}/") String httpPathPrefix) {
        WFlow wFlow = new WFlow();
        Filter filter = new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                if (request.getLocalPort() == restPort) {
                    if (request.getServletPath().startsWith(httpPathPrefix)) {
                        wFlow.createServiceFork(() -> {
                            try {
                                filterChain.doFilter(request, response);
                            } catch (IOException | ServletException e) {
                                sneakyThrow(e);
                            }
                        }).run();
                        return;
                    }
                }
                filterChain.doFilter(request, response);
            }

            private <E extends Throwable, T> T sneakyThrow(Throwable t) throws E {
                throw (E) t;
            }
        };

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setOrder(-50);
        filterRegistrationBean.setName("woodyFilter");
        filterRegistrationBean.addUrlPatterns(httpPathPrefix+"*");
        return filterRegistrationBean;
    }
}
