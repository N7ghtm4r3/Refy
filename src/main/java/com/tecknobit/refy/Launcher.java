package com.tecknobit.refy;

import com.tecknobit.equinox.environment.controllers.EquinoxController;
import com.tecknobit.equinox.resourcesutils.ResourcesProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

import static com.tecknobit.equinox.resourcesutils.ResourcesManager.RESOURCES_KEY;
import static com.tecknobit.equinox.resourcesutils.ResourcesProvider.CUSTOM_CONFIGURATION_FILE_PATH;
import static com.tecknobit.equinox.resourcesutils.ResourcesProvider.DEFAULT_CONFIGURATION_FILE_PATH;
import static com.tecknobit.refy.helpers.resources.RefyResourcesManager.LOGOS_DIRECTORY;

@EnableAutoConfiguration
@PropertySources({
        @PropertySource(value = "classpath:" + DEFAULT_CONFIGURATION_FILE_PATH),
        @PropertySource(value = "file:" + CUSTOM_CONFIGURATION_FILE_PATH, ignoreResourceNotFound = true)
})
@EnableJpaRepositories(
        value = {"com.tecknobit.*"}
)
@EntityScan(
        value = {"com.tecknobit.*"}
)
@SpringBootApplication
public class Launcher {

    public static void main(String[] args) {
        EquinoxController.initEquinoxEnvironment(
                "tecknobit/refy/backend",
                " to correctly register a new user in the Refy system ",
                Launcher.class,
                args);
        ResourcesProvider resourcesProvider = new ResourcesProvider(RESOURCES_KEY, List.of(LOGOS_DIRECTORY));
        resourcesProvider.createSubDirectories();
        SpringApplication.run(Launcher.class, args);
    }

}
