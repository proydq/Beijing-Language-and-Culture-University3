package com.proshine.system.service;


import com.proshine.system.entity.Terminal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 终端服务接口
 */
public interface TerminalService {
    
    /**
     * 保存或更新终端
     * @param terminal 终端实体
     * @return 保存后的终端
     */
    Terminal save(Terminal terminal);
    
    /**
     * 根据ID查询终端
     * @param id 终端ID
     * @param cstmId 客户域ID
     * @return 终端信息
     */
    Optional<Terminal> findById(String id, String cstmId);
    
    /**
     * 根据客户域ID和终端ID查询
     * @param cstmId 客户域ID
     * @param terminalId 终端ID
     * @return 终端信息
     */
    Optional<Terminal> findByCstmIdAndTerminalId(String cstmId, String terminalId);
    
    /**
     * 根据客户域ID查询所有终端
     * @param cstmId 客户域ID
     * @return 终端列表
     */
    List<Terminal> findByCstmIdAndExtraId2(String cstmId, String extraId2);
    
    /**
     * 分页查询终端
     * @param cstmId 客户域ID
     * @param pageable 分页参数
     * @return 终端分页数据
     */
    Page<Terminal> findByCstmId(String cstmId, Pageable pageable);


    List<Terminal> findByCstmId(String cstmId);
    
    /**
     * 根据终端名称模糊查询
     * @param cstmId 客户域ID
     * @param terminalName 终端名称
     * @return 终端列表
     */
    List<Terminal> findByTerminalNameLike(String cstmId, String terminalName);
    
    /**
     * 根据在线状态查询
     * @param cstmId 客户域ID
     * @param onlineState 在线状态
     * @return 终端列表
     */
    List<Terminal> findByOnlineState(String cstmId, Integer onlineState);
    
    /**
     * 根据教室名称查询
     * @param cstmId 客户域ID
     * @param classroomName 教室名称
     * @return 终端列表
     */
    List<Terminal> findByClassroomName(String cstmId, String classroomName);
    
    /**
     * 统计在线终端数量
     * @param cstmId 客户域ID
     * @return 在线终端数量
     */
    Long countOnlineTerminals(String cstmId);
    
    /**
     * 更新终端在线状态
     * @param id 终端ID
     * @param cstmId 客户域ID
     * @param onlineState 在线状态
     * @return 是否更新成功
     */
    boolean updateOnlineState(String id, String cstmId, Integer onlineState);
    
    /**
     * 终端登录
     * @param id 终端ID
     * @param cstmId 客户域ID
     * @return 是否更新成功
     */
    boolean terminalLogon(String id, String cstmId);
    
    /**
     * 终端登出
     * @param id 终端ID
     * @param cstmId 客户域ID
     * @return 是否更新成功
     */
    boolean terminalLogoff(String id, String cstmId);
    
    /**
     * 删除终端
     * @param id 终端ID
     * @param cstmId 客户域ID
     * @return 是否删除成功
     */
    boolean delete(String id, String cstmId);
    
    /**
     * 批量保存终端
     * @param terminals 终端列表
     * @return 保存后的终端列表
     */
    List<Terminal> saveAll(List<Terminal> terminals);
    
    /**
     * 检查终端ID是否存在
     * @param cstmId 客户域ID
     * @param terminalId 终端ID
     * @return 是否存在
     */
    boolean existsTerminalId(String cstmId, String terminalId);
    
    /**
     * 创建新终端
     * @param terminal 终端信息
     * @return 创建的终端
     */
    Terminal createTerminal(Terminal terminal);
    
    /**
     * 更新终端信息
     * @param terminal 终端信息
     * @return 更新后的终端
     */
    Terminal updateTerminal(Terminal terminal);
}