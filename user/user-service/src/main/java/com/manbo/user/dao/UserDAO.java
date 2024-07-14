package com.manbo.user.dao;

import com.manbo.common.service.dao.BaseDAO;
import com.manbo.common.util.dto.query.QueryObj;
import com.manbo.user.dto.UserListDTO;
import com.manbo.user.model.QUserDO;
import com.manbo.user.model.UserDO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import java.util.Optional;

import static com.manbo.common.util.constant.StringConst.EMPTY;
import static com.manbo.common.util.constant.StringConst.PERCENT;

/**
 * @author manboyu
 */
public interface UserDAO extends BaseDAO<UserDO, Long> {

    default Page<UserListDTO> findUsers(QueryObj queryDTO) {
        final QUserDO userDO = QUserDO.userDO;
        final BooleanBuilder predicate = new BooleanBuilder();

        final String keyword = Optional.ofNullable(queryDTO.getKeyword())
            .filter(StringUtils::isNotBlank)
            .map(k -> StringUtils.wrapIfMissing(k, PERCENT))
            .orElse(EMPTY);

        if (StringUtils.isNotBlank(keyword)) {
            predicate.and(userDO.name.like(keyword));
        }

        predicate.and(buildPredicate(queryDTO));

        final JPAQuery<UserListDTO> jpaQuery = getJPAQueryFactory()
            .select(Projections.fields(UserListDTO.class,
                userDO.id, userDO.name, userDO.email,
                userDO.status, userDO.createdAt, userDO.updatedAt))
            .from(userDO)
            .where(predicate);
        return buildPage(jpaQuery, queryDTO);
    }
}
