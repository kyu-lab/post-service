package kyulab.postservice.service.gateway;

import kyulab.postservice.dto.gateway.req.UsersReqDto;
import kyulab.postservice.dto.gateway.res.UsersListDto;
import kyulab.postservice.dto.gateway.res.UsersDto;
import kyulab.postservice.utils.UserContext;
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

	@Value("${gateway.base-url:}")
	private String gateway;

	@Value("${gateway.users-path:/users}")
	private String userPath;

	private final RestTemplate restTemplate;

	/**
	 * 사용자 서비스(users-serivce)에게 사용자들의 정보를 요청한 후 반환한다.
	 * @param userIds 데이터를 받을 사용자 목록
	 * @return 사용자 정보 목록
	 */
	public UsersListDto requestUserInfos(Set<Long> userIds) {
		UsersReqDto listDto = new UsersReqDto(UserContext.getUserId(), userIds);
		HttpEntity<UsersReqDto> request = new HttpEntity<>(listDto);
		return restTemplate.exchange(
				gateway + userPath,
				HttpMethod.POST,
				request,
				new ParameterizedTypeReference<UsersListDto>() {}
		).getBody();
	}

	/**
	 * 사용자 서비스(users-serivce)에게 사용자의 정보를 요청한 후 반환한다.
	 * @param userId 요청할 사용자 정보
	 * @return 사용자 정보 목록
	 */
	public UsersDto requestUserInfo(Long userId) {
		String userServiceUrl = gateway + userPath + "/" + userId;
		try {
			return restTemplate.getForObject(userServiceUrl, UsersDto.class);
		} catch (HttpClientErrorException h) {
			log.error("users-service와 연결 안됨 : {}", h.getMessage());
			throw new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE, "users-service와 연결에 실패하였습니다.");
		} catch (Exception e) {
			log.error("user-service와 통신 에러 : {}", e.getMessage());
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "통신외 에러가 발생했습니다.");
		}
	}

}
