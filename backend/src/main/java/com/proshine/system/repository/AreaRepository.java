package com.proshine.system.repository;

import com.proshine.system.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 区域数据访问层
 */
@Repository
public interface AreaRepository extends JpaRepository<Area, String>, JpaSpecificationExecutor<Area> {

    /**
     * 根据父节点ID查询子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<Area> findByParentId(String parentId);

    /**
     * 根据父节点ID统计子节点数量
     *
     * @param parentId 父节点ID
     * @return 子节点数量
     */
    long countByParentId(String parentId);

    /**
     * 根据客户域查询所有区域
     *
     * @param cstmId 客户域
     * @return 区域列表
     */
    List<Area> findByCstmId(String cstmId);

    /**
     * 根据客户域和类型查询区域
     *
     * @param cstmId 客户域
     * @param type 类型
     * @return 区域列表
     */
    List<Area> findByCstmIdAndType(String cstmId, String type);

    /**
     * 根据客户域和父节点ID查询子节点
     *
     * @param cstmId 客户域
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<Area> findByCstmIdAndParentId(String cstmId, String parentId);

    /**
     * 根据客户域查询根节点（parentId为空或null）
     *
     * @param cstmId 客户域
     * @return 根节点列表
     */
    @Query("SELECT a FROM Area a WHERE a.cstmId = :cstmId AND (a.parentId IS NULL OR a.parentId = '')")
    List<Area> findRootAreasByCstmId(@Param("cstmId") String cstmId);

    /**
     * 级联删除：删除指定节点及其所有子节点
     *
     * @param id 节点ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Area a WHERE a.id = :id OR a.parentId = :id")
    void deleteByIdCascade(@Param("id") String id);
}