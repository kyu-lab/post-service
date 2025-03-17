package kyulab.postservice.handler;

import kyulab.postservice.common.BasicResponse;
import kyulab.postservice.handler.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<BasicResponse<Object>> handleBadRequestException(BadRequestException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BasicResponse<>(e.getMessage()));
	}

}
