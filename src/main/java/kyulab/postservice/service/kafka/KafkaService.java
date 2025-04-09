package kyulab.postservice.service.kafka;

import kyulab.postservice.handler.exception.ServerErrorExcpetion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void sendMsg(String topic, Object msg) {
		try {
			kafkaTemplate.send(topic, msg);
		} catch (Exception e) {
			log.error("카프카 전송 실패... topic={}, msg={}", topic, msg);
			log.error("에러 메시지 : {}", e.getMessage());
			throw new ServerErrorExcpetion("서비스간 통신 에러 발생");
		}
	}

}
