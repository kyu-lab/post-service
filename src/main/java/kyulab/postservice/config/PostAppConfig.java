package kyulab.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class PostAppConfig {

	@Value("${gateway.key:}")
	private String gatewayKey;

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
			HttpHeaders headers = request.getHeaders();
			headers.add("X-GATE-WAY-KEY", gatewayKey);
			headers.setContentType(MediaType.APPLICATION_JSON);
			return execution.execute(request, body);
		}));
		return restTemplate;
	}

}
