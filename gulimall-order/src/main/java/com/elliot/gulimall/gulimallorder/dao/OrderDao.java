package com.elliot.gulimall.gulimallorder.dao;

import com.elliot.gulimall.gulimallorder.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author Elliot
 * @email example@example.com
 * @date 2022-10-04 14:27:59
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
