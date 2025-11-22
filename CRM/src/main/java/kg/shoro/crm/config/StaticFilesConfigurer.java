package kg.shoro.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticFilesConfigurer implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry r) {
        r.addResourceHandler("/qr/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/qr/");
    }
}
