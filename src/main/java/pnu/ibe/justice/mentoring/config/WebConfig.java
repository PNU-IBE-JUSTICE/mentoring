package pnu.ibe.justice.mentoring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.web-dir}")
    private String webDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /web/uploads/** URL로 접근할 수 있게 설정
        registry.addResourceHandler(webDir + "/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
