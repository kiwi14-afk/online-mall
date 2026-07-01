package com.mall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.order.entity.OrderToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderTokenMapper extends BaseMapper<OrderToken> {

    /**
     * 原子操作：仅当令牌未使用时标记为已使用
     */
    @Update("UPDATE t_order_token SET used = 1, order_no = #{orderNo} WHERE token = #{token} AND used = 0 AND expire_time > NOW()")
    int useToken(@Param("token") String token, @Param("orderNo") String orderNo);
}
