package com.manbo.common.util.dto.query;

import com.manbo.common.util.enums.QueryGroupOperatorEnum;

/**
 * 將Query obj抽象化
 * @author Manbo
 */
public interface QueryObj extends QueryRuleHandler, QueryOrderHandler {

    Integer getPageNumber();

    Integer getPageSize();

    QueryGroupOperatorEnum getGroupOp();

    String getKeyword();

    boolean isIgnoreAuth();

    void setPageNumber(Integer pageNumber);

    void setPageSize(Integer pageSize);

    void setGroupOp(QueryGroupOperatorEnum groupOp);

    void setKeyword(String keyword);

    void setIgnoreAuth(boolean ignoreAuth);

}
