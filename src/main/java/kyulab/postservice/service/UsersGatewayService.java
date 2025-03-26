package kyulab.postservice.service;

import kyulab.postservice.dto.gateway.UsersList;
import kyulab.postservice.dto.gateway.UsersResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersGatewayService {

	@Value("${gateway.url:}")
	private String gateway;

	@Value("${gateway.user-path:}")
	private String userPath;

	private final RestTemplate restTemplate;

	/**
	 * 사용자 서비스(users-serivce)에게 사용자들의 정보를 요청한 후 반환한다.
	 * @param userIds 요청할 사용자 정보 목록
	 * @return 사용자 정보 목록
	 */
	public UsersList requestGetUserInfos(Set<Long> userIds) {
		HttpEntity<Set<Long>> request = new HttpEntity<>(userIds);
		return restTemplate.exchange(
				gateway + userPath,
				HttpMethod.POST,
				request,
				new ParameterizedTypeReference<UsersList>() {}
		).getBody();
	}

	/**
	 * 사용자 서비스(users-serivce)에게 사용자의 정보를 요청한 후 반환한다.
	 * @param userId 요청할 사용자 정보
	 * @return 사용자 정보 목록
	 */
	public UsersResDto requestGetUserInfo(Long userId) {
		String userServiceUrl = gateway + userPath + "/" + userId;
		try {
			return restTemplate.getForObject(userServiceUrl, UsersResDto.class);
		} catch (HttpClientErrorException h) {
			log.error("users-service와 연결 안됨 : {}", h.getMessage());
			throw new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE, "users-service와 연결에 실패하였습니다.");
		} catch (Exception e) {
			log.error("user-service와 통신 에러 : {}", e.getMessage());
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "통신외 에러가 발생했습니다.");
		}
	}

}
