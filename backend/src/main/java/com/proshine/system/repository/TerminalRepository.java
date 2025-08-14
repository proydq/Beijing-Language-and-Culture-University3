package com.proshine.system.repository;


import com.proshine.system.entity.Terminal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 终端数据访问接口
 */
public interface TerminalRepository extends JpaRepository<Terminal, Terminal.TerminalId>,
        JpaSpecificationExecutor<Terminal> {
    
    /**
     * 根据客户域ID和终端ID查询终端
     */
    @Query("SELECT t FROM Terminal t WHERE t.cstmId = :cstmId AND t.terminalId = :terminalId")
    Optional<Terminal> findByCstmIdAndTerminalId(@Param("cstmId") String cstmId, 
                                                 @Param("terminalId") String terminalId);
    
    /**
     * 根据客户域ID查询所有终端
     */
    List<Terminal> findByCstmIdAndExtraId2(String cstmId, String extraId2);
    
    /**
     * 根据客户域ID分页查询终端
     */
    Page<Terminal> findByCstmId(String cstmId, Pageable pageable);

    List<Terminal> findByCstmId(String cstmId);
    
    /**
     * 根据客户域ID和终端名称模糊查询
     */
    @Query("SELECT t FROM Terminal t WHERE t.cstmId = :cstmId AND t.terminalName LIKE %:terminalName%")
    List<Terminal> findByCstmIdAndTerminalNameLike(@Param("cstmId") String cstmId, 
                                                   @Param("terminalName") String terminalName);
    
    /**
     * 根据客户域ID和在线状态查询
     */
    List<Terminal> findByCstmIdAndOnlineState(String cstmId, Integer onlineState);
    
    /**
     * 根据客户域ID和教室名称查询
     */
    List<Terminal> findByCstmIdAndClassroomName(String cstmId, String classroomName);
    
    /**
     * 根据客户域ID查询在线终端数量
     */
    @Query("SELECT COUNT(t) FROM Terminal t WHERE t.cstmId = :cstmId AND t.onlineState = 1")
    Long countOnlineTerminalsByCstmId(@Param("cstmId") String cstmId);
    
    /**
     * 更新终端在线状态
     */
    @Modifying
    @Query("UPDATE Terminal t SET t.onlineState = :onlineState, t.lastAliveTime = :lastAliveTime " +
           "WHERE t.id = :id AND t.cstmId = :cstmId")
    int updateOnlineState(@Param("id") String id, 
                         @Param("cstmId") String cstmId,
                         @Param("onlineState") Integer onlineState, 
                         @Param("lastAliveTime") Date lastAliveTime);
    
    /**
     * 更新终端登录时间
     */
    @Modifying
    @Query("UPDATE Terminal t SET t.onlineState = 1, t.logonTime = :logonTime, t.lastAliveTime = :logonTime " +
           "WHERE t.id = :id AND t.cstmId = :cstmId")
    int updateLogonTime(@Param("id") String id, 
                       @Param("cstmId") String cstmId,
                       @Param("logonTime") Date logonTime);
    
    /**
     * 更新终端登出时间
     */
    @Modifying
    @Query("UPDATE Terminal t SET t.onlineState = 0, t.logoffTime = :logoffTime " +
           "WHERE t.id = :id AND t.cstmId = :cstmId")
    int updateLogoffTime(@Param("id") String id, 
                        @Param("cstmId") String cstmId,
                        @Param("logoffTime") Date logoffTime);
    
    /**
     * 根据IP地址查询终端
     */
    Optional<Terminal> findByIpaddr(String ipaddr);
    
    /**
     * 根据设备型号ID查询终端列表
     */
    List<Terminal> findByCstmIdAndDeviceModelId(String cstmId, String deviceModelId);
    
    /**
     * 判断终端ID是否存在
     */
    boolean existsByCstmIdAndTerminalId(String cstmId, String terminalId);
}