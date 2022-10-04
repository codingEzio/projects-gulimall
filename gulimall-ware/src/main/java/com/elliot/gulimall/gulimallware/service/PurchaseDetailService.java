package com.elliot.gulimall.gulimallware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elliot.common.utils.PageUtils;
import com.elliot.gulimall.gulimallware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author Elliot
 * @email example@example.com
 * @date 2022-10-04 14:29:57
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

