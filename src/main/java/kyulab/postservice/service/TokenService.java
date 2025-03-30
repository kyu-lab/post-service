package kyulab.postservice.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import kyulab.postservice.handler.exception.UnauthorizedAccessException;
import kyulab.postservice.utils.SecretUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

	private final SecretUtil secretUtil;

	/**
	 * 토큰으로 부터 사용자 아이디를 가져온다.
	 * @param token 토큰
	 * @return 사용자 아이디
	 */
	public String getUserId(String token) {
		try {
			if (!token.startsWith("Bearer")) {
				throw new UnauthorizedAccessException("Invalid Token");
			}

			token = token.substring(7);
			return Jwts.parserBuilder()
					.setSigningKey(secretUtil.getAccessKey())
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
		} catch (ExpiredJwtException e) {
			return "";
		} catch (JwtException j) {
			log.error("잘못된 토큰 : {}", j.getMessage());
			throw new IllegalArgumentException("Ivalid Token!");
		} catch (Exception e) {
			log.error("Jwt Error : " + e.getMessage());
			throw new RuntimeException("Jwt Error!");
		}
	}

}
