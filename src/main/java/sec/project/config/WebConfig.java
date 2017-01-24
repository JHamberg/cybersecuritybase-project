package sec.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Jonatan on 24.1.2017.
 */

@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // *** A8-Cross-Site Request Forgery ***
        // This allows foreign sites to perform requests as if they
        // we performed by the user, leading to potentially unwanted
        // records and/or data erasure

        // Fix: Remove this line and line 29 in SecurityConfiguration.java.
        registry.addMapping("/**");
    }
}
