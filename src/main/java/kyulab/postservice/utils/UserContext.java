package kyulab.postservice.utils;

import java.util.Objects;

/**
 * 프론트의 요청시 토큰값을 파싱하여 사용자 아이디를 추출한다. <br/>
 * 로그인 유저인 경우는 {@link kyulab.postservice.utils.UserContext#getUserId()}에 아이디 저장 <br/>
 * 비로그인 유저는 아이디값 저장없이 서비스 진행
 */
public class UserContext {
	private static final ThreadLocal<Long> userIdContext = new ThreadLocal<>();

	public static void setUserId(Long userId) {
		Objects.requireNonNull(userId);
		userIdContext.set(userId);
	}

	public static Long getUserId() {
		return userIdContext.get();
	}

	public static boolean isLogin() {
		return UserContext.getUserId() != null;
	}
}
