package com.proshine.system.repository;

import com.proshine.system.entity.Terminal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 终端设备数据访问层
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface TerminalRepository extends JpaRepository<Terminal, String> {

    /**
     * 根据客户域ID查询终端列表
     */
    List<Terminal> findByCstmIdOrderByCreateTimeDesc(String cstmId);

    /**
     * 根据客户域ID分页查询终端列表
     */
    Page<Terminal> findByCstmIdOrderByCreateTimeDesc(String cstmId, Pageable pageable);

    /**
     * 根据终端ID查询终端
     */
    Optional<Terminal> findByTerminalIdAndCstmId(String terminalId, String cstmId);

    /**
     * 根据房间ID查询绑定的终端列表
     */
    @Query("SELECT t FROM Terminal t WHERE t.extraId2 = :roomId AND t.cstmId = :cstmId")
    List<Terminal> findByRoomId(@Param("roomId") String roomId, @Param("cstmId") String cstmId);

    /**
     * 根据房间ID查询绑定的终端（单个）
     */
    @Query("SELECT t FROM Terminal t WHERE t.extraId2 = :roomId AND t.cstmId = :cstmId")
    Optional<Terminal> findFirstByRoomId(@Param("roomId") String roomId, @Param("cstmId") String cstmId);

    /**
     * 查询在线终端列表
     */
    @Query("SELECT t FROM Terminal t WHERE t.onlineState = 1 AND t.cstmId = :cstmId ORDER BY t.lastAliveTime DESC")
    List<Terminal> findOnlineTerminals(@Param("cstmId") String cstmId);

    /**
     * 查询离线终端列表
     */
    @Query("SELECT t FROM Terminal t WHERE t.onlineState = 0 AND t.cstmId = :cstmId ORDER BY t.logoffTime DESC")
    List<Terminal> findOfflineTerminals(@Param("cstmId") String cstmId);

    /**
     * 根据终端名称模糊查询
     */
    @Query("SELECT t FROM Terminal t WHERE t.terminalName LIKE %:name% AND t.cstmId = :cstmId ORDER BY t.createTime DESC")
    List<Terminal> findByTerminalNameContaining(@Param("name") String name, @Param("cstmId") String cstmId);

    /**
     * 根据IP地址查询终端
     */
    Optional<Terminal> findByIpaddrAndCstmId(String ipaddr, String cstmId);

    /**
     * 统计客户域下的终端总数
     */
    long countByCstmId(String cstmId);

    /**
     * 统计在线终端数量
     */
    @Query("SELECT COUNT(t) FROM Terminal t WHERE t.onlineState = 1 AND t.cstmId = :cstmId")
    long countOnlineTerminals(@Param("cstmId") String cstmId);

    /**
     * 统计离线终端数量
     */
    @Query("SELECT COUNT(t) FROM Terminal t WHERE t.onlineState = 0 AND t.cstmId = :cstmId")
    long countOfflineTerminals(@Param("cstmId") String cstmId);

    /**
     * 查询指定分类的终端列表
     */
    List<Terminal> findByCatIdAndCstmIdOrderByCreateTimeDesc(String catId, String cstmId);

    /**
     * 根据主板类型查询终端
     */
    List<Terminal> findByBoardTypeAndCstmIdOrderByCreateTimeDesc(String boardType, String cstmId);

    /**
     * 复合条件查询
     */
    @Query("SELECT t FROM Terminal t WHERE " +
           "(:terminalName IS NULL OR t.terminalName LIKE %:terminalName%) AND " +
           "(:onlineState IS NULL OR t.onlineState = :onlineState) AND " +
           "(:roomId IS NULL OR t.extraId2 = :roomId) AND " +
           "t.cstmId = :cstmId " +
           "ORDER BY t.createTime DESC")
    Page<Terminal> findByConditions(@Param("terminalName") String terminalName,
                                   @Param("onlineState") Integer onlineState,
                                   @Param("roomId") String roomId,
                                   @Param("cstmId") String cstmId,
                                   Pageable pageable);

    /**
     * 查询未绑定房间的终端列表
     */
    @Query("SELECT t FROM Terminal t WHERE (t.extraId2 IS NULL OR t.extraId2 = '') AND t.cstmId = :cstmId ORDER BY t.createTime DESC")
    List<Terminal> findUnboundTerminals(@Param("cstmId") String cstmId);

    /**
     * 查询已绑定房间的终端列表
     */
    @Query("SELECT t FROM Terminal t WHERE t.extraId2 IS NOT NULL AND t.extraId2 != '' AND t.cstmId = :cstmId ORDER BY t.createTime DESC")
    List<Terminal> findBoundTerminals(@Param("cstmId") String cstmId);

    /**
     * 根据网络类型查询终端
     */
    List<Terminal> findByNetworkTypeAndCstmIdOrderByCreateTimeDesc(Integer networkType, String cstmId);

    /**
     * 根据应用版本查询终端
     */
    List<Terminal> findByAppVersionAndCstmIdOrderByCreateTimeDesc(String appVersion, String cstmId);

    /**
     * 根据固件版本查询终端
     */
    @Query("SELECT t FROM Terminal t WHERE t.firmwareVersion LIKE %:firmwareVersion% AND t.cstmId = :cstmId ORDER BY t.createTime DESC")
    List<Terminal> findByFirmwareVersionContaining(@Param("firmwareVersion") String firmwareVersion, @Param("cstmId") String cstmId);

    /**
     * 根据设备型号ID查询终端
     */
    List<Terminal> findByDeviceModelIdAndCstmIdOrderByCreateTimeDesc(String deviceModelId, String cstmId);

    /**
     * 根据教室名称模糊查询
     */
    @Query("SELECT t FROM Terminal t WHERE t.classroomName LIKE %:classroomName% AND t.cstmId = :cstmId ORDER BY t.createTime DESC")
    List<Terminal> findByClassroomNameContaining(@Param("classroomName") String classroomName, @Param("cstmId") String cstmId);

    /**
     * 根据班级名称模糊查询
     */
    @Query("SELECT t FROM Terminal t WHERE t.className LIKE %:className% AND t.cstmId = :cstmId ORDER BY t.createTime DESC")
    List<Terminal> findByClassNameContaining(@Param("className") String className, @Param("cstmId") String cstmId);

    /**
     * 查询门禁终端列表
     */
    @Query("SELECT t FROM Terminal t WHERE t.entranceType IS NOT NULL AND t.entranceType != 0 AND t.cstmId = :cstmId ORDER BY t.createTime DESC")
    List<Terminal> findEntranceTerminals(@Param("cstmId") String cstmId);

    /**
     * 根据门禁开放状态查询终端
     */
    List<Terminal> findByEntranceOpenStatusAndCstmIdOrderByCreateTimeDesc(Terminal.EntranceOpenStatus entranceOpenStatus, String cstmId);

    /**
     * 根据进出类型查询终端
     */
    List<Terminal> findByEntranceTypeAndCstmIdOrderByCreateTimeDesc(Terminal.EntranceType entranceType, String cstmId);

    /**
     * 查询存储空间不足的终端（可用空间小于指定百分比）
     */
    @Query("SELECT t FROM Terminal t WHERE t.totalSpace IS NOT NULL AND t.freeSpace IS NOT NULL " +
           "AND (t.freeSpace * 100.0 / t.totalSpace) < :minFreeSpaceRate AND t.cstmId = :cstmId " +
           "ORDER BY (t.freeSpace * 100.0 / t.totalSpace) ASC")
    List<Terminal> findLowStorageTerminals(@Param("minFreeSpaceRate") Double minFreeSpaceRate, @Param("cstmId") String cstmId);

    /**
     * 根据存储空间范围查询终端
     */
    @Query("SELECT t FROM Terminal t WHERE t.totalSpace BETWEEN :minSpace AND :maxSpace AND t.cstmId = :cstmId ORDER BY t.totalSpace DESC")
    List<Terminal> findByStorageSpaceRange(@Param("minSpace") Long minSpace, @Param("maxSpace") Long maxSpace, @Param("cstmId") String cstmId);

    /**
     * 统计门禁终端数量
     */
    @Query("SELECT COUNT(t) FROM Terminal t WHERE t.entranceType IS NOT NULL AND t.entranceType != 0 AND t.cstmId = :cstmId")
    long countEntranceTerminals(@Param("cstmId") String cstmId);

    /**
     * 统计各网络类型的终端数量
     */
    @Query("SELECT t.networkType, COUNT(t) FROM Terminal t WHERE t.cstmId = :cstmId GROUP BY t.networkType")
    List<Object[]> countByNetworkType(@Param("cstmId") String cstmId);

    /**
     * 统计各应用版本的终端数量
     */
    @Query("SELECT t.appVersion, COUNT(t) FROM Terminal t WHERE t.cstmId = :cstmId GROUP BY t.appVersion")
    List<Object[]> countByAppVersion(@Param("cstmId") String cstmId);
}