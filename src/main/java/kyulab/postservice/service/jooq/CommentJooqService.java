package kyulab.postservice.service.jooq;

import kyulab.postservice.domain.content.ContentOrder;
import kyulab.postservice.dto.res.CommentInfoDto;
import kyulab.postservice.handler.exception.BadRequestException;
import kyulab.postservice.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kyulab.jooq.tables.Comments.*;
import static kyulab.jooq.tables.CommentVote.*;
import static org.jooq.impl.DSL.*;

@Service
@RequiredArgsConstructor
public class CommentJooqService {

	private final DSLContext dslContext;

	@Transactional(readOnly = true)
	public List<CommentInfoDto> getCommentsByOrder(long postId, Long cursor, ContentOrder order, int limit) {
		SelectQuery<?> baseQuery = dslContext
				.select(
					COMMENTS.ID, COMMENTS.USER_ID, COMMENTS.PARENT_ID,
					COMMENTS.CONTENT, COMMENTS.STATUS, COMMENTS.CREATED_AT,
					field(name("r", "child_count"), Long.class),
					field(name("cvc", "like_count"), Long.class),
					UserContext.isLogin() ? field(name("cvl", "is_like"), Boolean.class) : val(false).as("is_like")
				)
				.from(COMMENTS)
				.leftJoin(
					select(
						COMMENTS.PARENT_ID,
						count().as("child_count")
					)
					.from(COMMENTS)
					.where(COMMENTS.POST_ID.eq(postId))
					.groupBy(COMMENTS.PARENT_ID)
					.asTable("r")
				).on(DSL.field(name("r", "parent_id")).eq(COMMENTS.ID))
				.leftJoin(
					select(
						COMMENT_VOTE.COMMENT_ID,
						count().as("like_count")
					)
					.from(COMMENT_VOTE)
					.where(COMMENT_VOTE.IS_LIKE.isTrue())
					.groupBy(COMMENT_VOTE.COMMENT_ID)
					.asTable("cvc")
				).on(field(name("cvc", "comment_id")).eq(COMMENTS.ID))
				.getQuery();

		if (UserContext.isLogin()) {
			baseQuery.addJoin(
					select(
						COMMENT_VOTE.COMMENT_ID,
						COMMENT_VOTE.IS_LIKE
					)
					.from(COMMENT_VOTE)
					.where(COMMENT_VOTE.USER_ID.eq(UserContext.getUserId()))
					.asTable("cvl"),
					JoinType.LEFT_OUTER_JOIN,
					field(name("cvl", "comment_id")).eq(COMMENTS.ID)
			);
		}

		// 조건절
		Condition condition = COMMENTS.POST_ID.eq(postId)
							.and(COMMENTS.PARENT_ID.isNull());
		if (cursor != null) {
			condition = condition.and(COMMENTS.ID.lt(cursor));
		}

		switch (order) {
			case N -> {
				baseQuery.addConditions(condition);
				baseQuery.addOrderBy(COMMENTS.CREATED_AT.desc());
			}
			case O -> {
				baseQuery.addConditions(condition);
				baseQuery.addOrderBy(COMMENTS.CREATED_AT.asc());
			}
			case L -> {
				baseQuery.addConditions(condition);
				baseQuery.addOrderBy(
					coalesce(field(name("cvc", "like_count"), Long.class), 0).desc(),
					COMMENTS.CREATED_AT.desc()
				);
			}
			case C -> {
				baseQuery.addJoin(
						select(
							COMMENTS.ROOT_ID,
							count().as("reply_count")
						)
						.from(COMMENTS)
						.where(COMMENTS.POST_ID.eq(postId))
						.and(COMMENTS.ID.ne(COMMENTS.ROOT_ID))
						.groupBy(COMMENTS.ROOT_ID)
						.asTable("rc"),
						JoinType.LEFT_OUTER_JOIN,
						field(name("rc", "root_id")).eq(COMMENTS.ID)
				);
				baseQuery.addConditions(condition);
				baseQuery.addOrderBy(
						coalesce(field(name("rc", "reply_count"), Long.class), 0).desc(),
						COMMENTS.CREATED_AT.desc()
				);
			}
			default -> throw new BadRequestException("Invalid order type: " + order);
		}

		baseQuery.addLimit(limit + 1);
		return baseQuery.fetchInto(CommentInfoDto.class);
	}

}
