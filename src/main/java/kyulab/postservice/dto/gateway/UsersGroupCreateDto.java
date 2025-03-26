package kyulab.postservice.dto.gateway;

public record UsersGroupCreateDto(
		Long userId,
		String name,
		String imgUrl) {
}
