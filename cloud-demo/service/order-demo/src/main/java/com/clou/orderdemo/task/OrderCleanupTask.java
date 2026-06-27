package com.clou.orderdemo.task;

import com.clou.orderdemo.mapper.OrderItemsMapper;
import com.clou.orderdemo.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单清理定时任务
 * 用于清理超过三年且双方都已逻辑删除的订单数据
 *
 * @author LENOVO
 */
@Component
@Slf4j
public class OrderCleanupTask {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    /**
     * 每天晚上2点执行订单清理任务
     * 清理创建时间超过三年，且user_delete和merchant_delete都为1的订单
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupExpiredOrders() {
        log.info("开始执行订单清理定时任务...");

        try {
            // 查询创建时间超过三年且双方都逻辑删除的订单ID列表
            List<Long> expiredOrderIds = orderMapper.selectExpiredDeletedOrders();

            if (expiredOrderIds == null || expiredOrderIds.isEmpty()) {
                log.info("没有需要清理的过期订单");
                return;
            }

            log.info("发现 {} 条需要清理的过期订单", expiredOrderIds.size());

            int successCount = 0;
            for (Long orderId : expiredOrderIds) {
                try {
                    // 先删除子表数据（订单商品项）
                    orderItemsMapper.deleteOrderItems(orderId);

                    // 再删除主表数据（订单）
                    orderMapper.physicalDeleteOrder(orderId);

                    // TODO: 归档操作（后续拓展用）
                    // 在物理删除前，可以将订单数据归档到其他存储（如历史表、ES、文件等）
                    // archiveOrderData(orderId);

                    successCount++;
                    log.info("成功清理订单ID: {}", orderId);
                } catch (Exception e) {
                    log.error("清理订单ID: {} 时发生错误: {}", orderId, e.getMessage());
                    // 继续处理下一个订单，不中断整个任务
                }
            }

            log.info("订单清理定时任务执行完成，成功清理 {} / {} 条订单", successCount, expiredOrderIds.size());

        } catch (Exception e) {
            log.error("订单清理定时任务执行失败: {}", e.getMessage(), e);
        }
    }

    /**
     * TODO: 归档操作（后续拓展用）
     * 在物理删除订单数据前，将数据归档到其他存储介质
     * 可以归档到：
     * 1. 历史订单表（orders_history, order_items_history）
     * 2. Elasticsearch（用于后续搜索和分析）
     * 3. 文件存储（如CSV、Parquet等格式）
     * 4. 对象存储（如OSS、S3等）
     *
     * @param orderId 订单ID
     */
    private void archiveOrderData(Long orderId) {
        // TODO: 实现归档逻辑
        // 示例：
        // 1. 查询订单主表和子表数据
        // OrderPO order = orderMapper.byId(orderId);
        // List<OrderItemsPO> items = orderItemsMapper.byOrderId(orderId);
        //
        // 2. 将数据插入到历史表
        // orderHistoryMapper.insert(order);
        // items.forEach(item -> orderItemsHistoryMapper.insert(item));
        //
        // 3. 或者发送到MQ进行异步归档
        // orderArchiveProducer.send(orderArchiveMessage);
        //
        // 4. 或者保存到文件/对象存储
        // fileStorageService.saveOrderArchive(order, items);
    }
}
