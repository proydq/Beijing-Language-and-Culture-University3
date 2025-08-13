package com.proshine.system.service;

import com.proshine.system.entity.Terminal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 终端设备服务接口
 * 
 * @author system
 * @date 2024-01-01
 */
public interface TerminalService {

    /**
     * 保存或更新终端
     */
    Terminal saveOrUpdate(Terminal terminal);

    /**
     * 根据ID查询终端
     */
    Optional<Terminal> findById(String id);

    /**
     * 根据终端ID查询终端
     */
    Optional<Terminal> findByTerminalId(String terminalId, String cstmId);

    /**
     * 查询客户域下所有终端
     */
    List<Terminal> findAllByCustomerId(String cstmId);

    /**
     * 分页查询客户域下的终端
     */
    Page<Terminal> findByCustomerId(String cstmId, Pageable pageable);

    /**
     * 根据房间ID查询绑定的终端列表
     */
    List<Terminal> findByRoomId(String roomId, String cstmId);

    /**
     * 根据房间ID查询绑定的终端（单个）
     */
    Optional<Terminal> findFirstByRoomId(String roomId, String cstmId);

    /**
     * 查询在线终端列表
     */
    List<Terminal> findOnlineTerminals(String cstmId);

    /**
     * 查询离线终端列表
     */
    List<Terminal> findOfflineTerminals(String cstmId);

    /**
     * 根据终端名称模糊查询
     */
    List<Terminal> findByNameContaining(String name, String cstmId);

    /**
     * 复合条件查询
     */
    Page<Terminal> findByConditions(String terminalName, Integer onlineState, 
                                   String roomId, String cstmId, Pageable pageable);

    /**
     * 删除终端
     */
    void deleteById(String id);

    /**
     * 批量删除终端
     */
    void deleteByIds(List<String> ids);

    /**
     * 绑定终端到房间
     */
    void bindToRoom(String terminalId, String roomId, String cstmId);

    /**
     * 解绑终端和房间
     */
    void unbindFromRoom(String terminalId, String cstmId);

    /**
     * 更新终端在线状态
     */
    void updateOnlineState(String terminalId, Terminal.OnlineState onlineState, String cstmId);

    /**
     * 终端上线
     */
    void terminalOnline(String terminalId, String ipaddr, String cstmId);

    /**
     * 终端下线
     */
    void terminalOffline(String terminalId, String cstmId);

    /**
     * 更新最后存活时间
     */
    void updateLastAliveTime(String terminalId, String cstmId);

    /**
     * 获取终端统计信息
     */
    TerminalStatistics getStatistics(String cstmId);

    /**
     * 查询未绑定房间的终端
     */
    List<Terminal> findUnboundTerminals(String cstmId);

    /**
     * 查询已绑定房间的终端
     */
    List<Terminal> findBoundTerminals(String cstmId);

    /**
     * 检查终端ID是否存在
     */
    boolean existsByTerminalId(String terminalId, String cstmId);

    /**
     * 检查IP地址是否被占用
     */
    boolean existsByIpAddress(String ipaddr, String cstmId);

    /**
     * 终端统计信息内部类
     */
    class TerminalStatistics {
        private long totalCount;
        private long onlineCount;
        private long offlineCount;
        private long boundCount;
        private long unboundCount;

        public TerminalStatistics(long totalCount, long onlineCount, long offlineCount, 
                                long boundCount, long unboundCount) {
            this.totalCount = totalCount;
            this.onlineCount = onlineCount;
            this.offlineCount = offlineCount;
            this.boundCount = boundCount;
            this.unboundCount = unboundCount;
        }

        // Getters
        public long getTotalCount() { return totalCount; }
        public long getOnlineCount() { return onlineCount; }
        public long getOfflineCount() { return offlineCount; }
        public long getBoundCount() { return boundCount; }
        public long getUnboundCount() { return unboundCount; }
        
        public double getOnlineRate() {
            return totalCount > 0 ? (double) onlineCount / totalCount * 100 : 0.0;
        }
        
        public double getBoundRate() {
            return totalCount > 0 ? (double) boundCount / totalCount * 100 : 0.0;
        }
    }
}