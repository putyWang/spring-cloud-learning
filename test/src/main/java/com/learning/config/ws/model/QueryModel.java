package com.learning.config.ws.model;

import cn.hutool.core.collection.CollUtil;

import java.util.Map;

/**
 * @author wangwei
 * @version 1.0
 * @date 2025/5/25 下午3:41
 */
public class QueryModel {

    private static final String DATA_ID_KEY = "dataId";
    private static final String TAG_KEY = "tag";

    private QueryModel(){}

    private String dataId;

    private String tag;

    public String getKey(){
        return String.format("%s#%s", dataId, tag);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder{

        private Builder(){}

        private Map<String, String> queryParam;

        private String dataId;

        private String tag;

        public Builder queryParam(Map<String, String> queryParam) {
            this.queryParam = queryParam;
            return this;
        }

        public Builder dataId(String dataId) {
            this.dataId = dataId;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public QueryModel build(){
            QueryModel queryModel = new QueryModel();
            if(CollUtil.isNotEmpty(queryParam)) {
                queryModel.dataId = queryParam.get(DATA_ID_KEY);
                queryModel.tag = queryParam.get(TAG_KEY);
            } else {
                queryModel.dataId = this.dataId;
                queryModel.tag = this.tag;
            }
            return queryModel;
        }
    }

}
