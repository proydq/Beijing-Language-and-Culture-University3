package com.proshine.system.repository;

import com.proshine.system.entity.ContinuousBookingSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 连续预约设置数据访问层
 */
@Repository
public interface ContinuousBookingSettingRepository extends JpaRepository<ContinuousBookingSetting, String>, JpaSpecificationExecutor<ContinuousBookingSetting> {

    /**
     * 根据房间ID查询连续预约设置
     *
     * @param roomId 房间ID
     * @param cstmId 客户域
     * @return 连续预约设置
     */
    Optional<ContinuousBookingSetting> findByRoomIdAndCstmId(String roomId, String cstmId);

    /**
     * 根据客户域查询所有连续预约设置
     *
     * @param cstmId 客户域
     * @return 连续预约设置列表
     */
    List<ContinuousBookingSetting> findByCstmId(String cstmId);

    /**
     * 根据房间ID列表查询连续预约设置
     *
     * @param roomIds 房间ID列表
     * @param cstmId 客户域
     * @return 连续预约设置列表
     */
    List<ContinuousBookingSetting> findByRoomIdInAndCstmId(List<String> roomIds, String cstmId);

    /**
     * 批量更新连续预约设置
     *
     * @param roomIds 房间ID列表
     * @param continuousDays 连续天数
     * @param continuousType 连续类型
     * @param isUnlimited 是否无限制
     * @param canContinuous 是否允许连续预约
     * @param cstmId 客户域
     * @param updateTime 更新时间
     */
    @Modifying
    @Transactional
    @Query("UPDATE ContinuousBookingSetting c SET c.continuousDays = :continuousDays, " +
           "c.continuousType = :continuousType, c.isUnlimited = :isUnlimited, " +
           "c.canContinuous = :canContinuous, c.lastUpdateTime = :updateTime " +
           "WHERE c.roomId IN :roomIds AND c.cstmId = :cstmId")
    int batchUpdateByRoomIds(@Param("roomIds") List<String> roomIds,
                            @Param("continuousDays") Integer continuousDays,
                            @Param("continuousType") ContinuousBookingSetting.ContinuousType continuousType,
                            @Param("isUnlimited") Boolean isUnlimited,
                            @Param("canContinuous") Boolean canContinuous,
                            @Param("cstmId") String cstmId,
                            @Param("updateTime") Long updateTime);

    /**
     * 删除指定房间的连续预约设置
     *
     * @param roomId 房间ID
     * @param cstmId 客户域
     */
    @Modifying
    @Transactional
    void deleteByRoomIdAndCstmId(String roomId, String cstmId);
}