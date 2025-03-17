package kyulab.postservice.common;

import lombok.Getter;

@Getter
public class BasicResponse<T> {

	private final T data;
	private final String message;

	public BasicResponse(T data) {
		this.data = data;
		this.message = null;
	}

	public BasicResponse(String message) {
		this.data = null;
		this.message = message;
	}

}
