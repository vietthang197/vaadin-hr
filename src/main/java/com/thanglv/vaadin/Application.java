package com.thanglv.vaadin;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.LoadingIndicatorConfiguration;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "vaadin-hr")
@Push(PushMode.AUTOMATIC)
public class Application implements AppShellConfigurator, VaadinServiceInitListener {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        serviceInitEvent.getSource().addUIInitListener(uiInitEvent -> {
            LoadingIndicatorConfiguration conf = uiInitEvent.getUI().getLoadingIndicatorConfiguration();

            // disable default theme -> loading indicator isn't shown
            conf.setApplyDefaultTheme(false);
        });
    }
}
