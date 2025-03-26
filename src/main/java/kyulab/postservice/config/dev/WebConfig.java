package kyulab.postservice.config.dev;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	/**
	 * 개발 전용 설정입니다.
	 * 게이트웨이 사용시 해당 설정을 주석해주세요
	 */
//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/api/**")
//				.allowedOrigins("*")
//				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//				.allowedHeaders("*");
//	}

}
