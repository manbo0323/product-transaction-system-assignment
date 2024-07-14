package com.manbo.user.dao;

import com.manbo.common.service.dao.BaseDAO;
import com.manbo.user.dto.UserAccountDTO;
import com.manbo.user.model.QUserAccountDO;
import com.manbo.user.model.UserAccountDO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

/**
 * @author manboyu
 */
public interface UserAccountDAO extends BaseDAO<UserAccountDO, Long> {

    default List<UserAccountDTO> findByUserIds(List<Long> userIds) {
        final QUserAccountDO userAccountDO = QUserAccountDO.userAccountDO;
        return getJPAQueryFactory()
            .select(Projections.fields(UserAccountDTO.class, entityExpressions(userAccountDO).toArray(new Expression[0])))
            .from(userAccountDO)
            .where(userAccountDO.userId.in(userIds))
            .fetch();
    }

    default Optional<UserAccountDO> lockByUserIdAndCurrency(Long userId, String currency) {
        final QUserAccountDO userAccountDO = QUserAccountDO.userAccountDO;
        return Optional.ofNullable(
            getJPAQueryFactory()
                .selectFrom(userAccountDO)
                .where(
                    userAccountDO.userId.eq(userId),
                    userAccountDO.currency.eq(currency)
                )
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne()
        );
    }
}
