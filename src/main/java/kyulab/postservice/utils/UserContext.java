package kyulab.postservice.utils;

// 사용자 아이디 관리용
public class UserContext {
	private static final ThreadLocal<Long> userIdContext = new ThreadLocal<>();

	public static void setUserId(Long userId) {
		userIdContext.set(userId);
	}

	public static Long getUserId() {
		return userIdContext.get();
	}

	public static boolean isLogin() {
		return UserContext.getUserId() != null;
	}

}
