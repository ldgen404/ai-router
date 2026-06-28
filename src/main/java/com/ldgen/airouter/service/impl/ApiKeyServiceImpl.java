package com.ldgen.airouter.service.impl;

import cn.hutool.core.util.IdUtil;
import com.ldgen.airouter.exception.BusinessException;
import com.ldgen.airouter.exception.ErrorCode;
import com.ldgen.airouter.mapper.ApiKeyMapper;
import com.ldgen.airouter.model.entity.ApiKey;
import com.ldgen.airouter.model.entity.User;
import com.ldgen.airouter.service.ApiKeyService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApiKeyServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKey> implements ApiKeyService {


    /**
     *
     * @param keyName
     * @param loginUser
     * @return
     */
    @Override
    public ApiKey createApiKey(String keyName, User loginUser) {
        // 生成 API Key（sk- 前缀 + 32位随机字符）
        String keyValue = "sk-" + IdUtil.simpleUUID();

        // 创建 API Key 对象
        ApiKey apiKey = ApiKey.builder()
                .userId(loginUser.getId())
                .keyValue(keyValue)
                .keyName(keyName)
                .status("active")
                .totalTokens(0L)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 保存到数据库
        this.save(apiKey);

        return apiKey;
    }

    /**
     * 获取用户的 API Key 列表
     * @param userId
     * @return
     */
    @Override
    public List<ApiKey> listUserApiKeys(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("userId", userId)
                .eq("isDelete", 0)
                .orderBy("createTime", false);

        return this.list(queryWrapper);
    }

    @Override
    public boolean revokeApiKey(Long id, Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("id", id)
                .eq("userId", userId);

        ApiKey apiKey = this.getOne(queryWrapper);
        if (apiKey == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "API Key 不存在");
        }

        // 更新状态为 revoked
        apiKey.setStatus("revoked");
        apiKey.setUpdateTime(LocalDateTime.now());
        return this.updateById(apiKey);
    }

    @Override
    public ApiKey getByKeyValue(String keyValue) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("keyValue", keyValue)
                .eq("status", "active")
                .eq("isDelete", 0);

        return this.getOne(queryWrapper);
    }

    @Override
    public void updateUsageStats(Long apiKeyId, Integer tokens) {
        ApiKey apiKey = this.getById(apiKeyId);
        if (apiKey != null) {
            apiKey.setTotalTokens(apiKey.getTotalTokens() + tokens);
            apiKey.setLastUsedTime(LocalDateTime.now());
            apiKey.setUpdateTime(LocalDateTime.now());
            this.updateById(apiKey);
        }
    }
}
