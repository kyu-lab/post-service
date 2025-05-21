package kyulab.postservice.entity;

import jakarta.persistence.*;
import kyulab.postservice.domain.content.ContentStatus;
import kyulab.postservice.dto.req.PostContentDto;
import kyulab.postservice.dto.req.PostSettingsDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends ContentEntity {

	@Column(
		nullable = false,
		length = 100
	)
	private String subject;

	private String summary;

	@OneToMany(
		mappedBy = "post",
		fetch = FetchType.LAZY
	)
	private List<Comments> comments = new ArrayList<>();

	@OneToMany(
		mappedBy = "post",
		fetch = FetchType.LAZY
	)
	private List<PostView> postViews = new ArrayList<>();

	@OneToMany(
		mappedBy = "post",
		fetch = FetchType.LAZY
	)
	private List<PostMark> postMarks = new ArrayList<>();

	private boolean isThumbnail;

	private String thumbnailUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	private Groups groups;

	private Post(long userId, PostContentDto contentDto, PostSettingsDto settingsDto) {
		super(userId, contentDto.content(), settingsDto.status());
		this.subject = contentDto.subject();
		this.summary = extractSummary(contentDto.content());
		this.isThumbnail = settingsDto.isThumbnail();
		this.thumbnailUrl = settingsDto.thumbnailUrl();
	}

	public static Post of(long userId, PostContentDto contentDto, PostSettingsDto settingsDto) {
		return new Post(userId, contentDto, settingsDto);
	}

	public void updateSubject(String subject) {
		if (!StringUtils.hasText(subject)) {
			throw new IllegalArgumentException("Subject cant be empty or blank");
		}

		if (subject.length() > 100) {
			throw new IllegalArgumentException("길이 100이상은 안됩니다. : " + subject.length());
		}

		this.subject = subject;
	}

	public void updateContent(String content) {
		super.updateContent(content);
	}

	public void updateStatus(ContentStatus status) {
		super.updateStatus(status);
	}

	public void updateUsehumbnail(boolean isThumbnail) {
		this.isThumbnail = isThumbnail;
	}

	public void updateThumbnail(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public void addPostView(PostView postView) {
		Objects.requireNonNull(postView);
		this.postViews.add(postView);
		postView.setPost(this);
	}

	public void addComments(Comments comments) {
		Objects.requireNonNull(comments);
		this.comments.add(comments);
		comments.setPost(this);
	}

	public void addPostMark(PostMark postMark) {
		Objects.requireNonNull(postMark);
		this.postMarks.add(postMark);
		postMark.setPost(this);
	}

	public void setGroups(Groups groups) {
		this.groups = groups;
	}

	/**
	 * 본문 글에서 글자를 추출해 짧은 요약글로 만든다. <br/>
	 * 최대 150글자에 3줄까지 가능.
	 * @param content 본문
	 * @return 요약글
	 */
	private String extractSummary(String content) {
		if (!StringUtils.hasText(content)) {
			return "";
		}

		Document document = Jsoup.parse(content);
		document.select("script").remove();

		Elements elements = document.body().children();
		StringBuilder summary = new StringBuilder();
		for (int i = 0; i < 3 && i < elements.size(); i++) {
			summary.append(elements.get(i).outerHtml());
		}

		return summary.toString().trim();
	}

}
