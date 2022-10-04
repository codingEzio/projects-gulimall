package com.elliot.gulimall.gulimallorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elliot.common.utils.PageUtils;
import com.elliot.gulimall.gulimallorder.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author Elliot
 * @email example@example.com
 * @date 2022-10-04 14:27:59
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

