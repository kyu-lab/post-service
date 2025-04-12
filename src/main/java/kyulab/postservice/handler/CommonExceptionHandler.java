package kyulab.postservice.handler;

import kyulab.postservice.handler.exception.BadRequestException;
import kyulab.postservice.handler.exception.NotFoundException;
import kyulab.postservice.handler.exception.ServerErrorExcpetion;
import kyulab.postservice.handler.exception.UnauthorizedAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<String> handleBadRequestException(BadRequestException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<String> handleBadRequestException(NotFoundException n) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(n.getMessage());
	}

	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<String> handleUnauthorizedAccessException(UnauthorizedAccessException u) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(u.getMessage());
	}

	@ExceptionHandler(ServerErrorExcpetion.class)
	public ResponseEntity<String> handleServerErrorExcpetion(ServerErrorExcpetion s) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(s.getMessage());
	}

}
