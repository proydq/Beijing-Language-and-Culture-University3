package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.ClassroomVo;
import com.proshine.system.dto.SearchClassroomCondition;
import com.proshine.system.entity.Room;
import com.proshine.system.entity.ClassroomApprovalConfig;
import com.proshine.system.entity.ClassroomApprovalLevel;
import com.proshine.system.repository.RoomRepository;
import com.proshine.system.repository.ClassroomApprovalConfigRepository;
import com.proshine.system.repository.ClassroomApprovalLevelRepository;
import com.proshine.system.service.RecycleBinService;
import com.proshine.system.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 回收站服务实现类
 * 
 * @author system
 * @date 2024-01-01
 */
@Service
@Slf4j
public class RecycleBinServiceImpl implements RecycleBinService {

    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private ClassroomApprovalConfigRepository approvalConfigRepository;
    
    @Autowired
    private ClassroomApprovalLevelRepository approvalLevelRepository;

    @Override
    public ResponsePageDataEntity<ClassroomVo> searchDeletedClassrooms(SearchClassroomCondition condition) {
        try {
            // 构建分页对象
            Pageable pageable = PageRequest.of(
                condition.getPageNumber() - 1, 
                condition.getPageSize(),
                Sort.by(Sort.Direction.DESC, "deleteTime")
            );

            // 构建查询条件
            Page<Room> roomPage;
            String roomName = condition.getRoomName();
            String roomAreaId = condition.getRoomAreaId();

            if (StringUtils.hasText(roomName) || StringUtils.hasText(roomAreaId)) {
                roomPage = roomRepository.findDeletedRoomsWithConditions(
                    roomName, roomName, roomAreaId, pageable);
            } else {
                roomPage = roomRepository.findByIsDeletedTrue(pageable);
            }

            // 获取客户域ID
            String customerId = SecurityUtil.getCustomerId();
            
            // 批量获取审批配置
            List<String> roomIds = roomPage.getContent().stream()
                .map(Room::getId)
                .collect(Collectors.toList());
            
            List<ClassroomApprovalConfig> configs = approvalConfigRepository.findByRoomIdInAndCstmId(roomIds, customerId);
            Map<String, ClassroomApprovalConfig> configMap = configs.stream()
                .collect(Collectors.toMap(ClassroomApprovalConfig::getRoomId, config -> config));
            
            // 批量获取审批级别
            List<String> configIds = configs.stream()
                .map(ClassroomApprovalConfig::getId)
                .collect(Collectors.toList());
            
            List<ClassroomApprovalLevel> allLevels = configIds.isEmpty() ? new ArrayList<>() : 
                approvalLevelRepository.findByConfigIdInAndCstmIdOrderByLevelNumber(configIds, customerId);
            Map<String, List<ClassroomApprovalLevel>> levelMap = allLevels.stream()
                .collect(Collectors.groupingBy(ClassroomApprovalLevel::getConfigId));
            
            // 转换为VO对象并根据审批条件过滤
            List<ClassroomVo> classroomVos = new ArrayList<>();
            for (Room room : roomPage.getContent()) {
                ClassroomApprovalConfig config = configMap.get(room.getId());
                String configId = config != null ? config.getId() : null;
                List<ClassroomApprovalLevel> levels = configId != null ? levelMap.get(configId) : null;
                ClassroomVo vo = convertToClassroomVo(room, config, levels);
                
                // 根据审批筛选条件过滤
                if (StringUtils.hasText(condition.getApprovalFilter()) && !"all".equals(condition.getApprovalFilter())) {
                    boolean needApproval = "yes".equals(vo.getNeedApproval());
                    boolean shouldInclude = "yes".equals(condition.getApprovalFilter()) ? needApproval : !needApproval;
                    if (!shouldInclude) {
                        continue;
                    }
                }
                
                classroomVos.add(vo);
            }

            // 构建分页响应
            ResponsePageDataEntity<ClassroomVo> response = new ResponsePageDataEntity<>();
            response.setRows(classroomVos);
            response.setTotal(roomPage.getTotalElements());

            return response;
        } catch (Exception e) {
            log.error("搜索已删除教室失败", e);
            throw new RuntimeException("搜索已删除教室失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void restoreClassroom(String id) {
        try {
            Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("教室不存在"));
            
            if (!room.getIsDeleted()) {
                throw new RuntimeException("教室未被删除，无需恢复");
            }

            room.setIsDeleted(false);
            room.setDeleteTime(null);
            room.setLastUpdateTime(System.currentTimeMillis());
            
            roomRepository.save(room);
            log.info("恢复教室成功，ID: {}", id);
        } catch (Exception e) {
            log.error("恢复教室失败，ID: {}", id, e);
            throw new RuntimeException("恢复教室失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void batchRestoreClassrooms(List<String> ids) {
        try {
            List<Room> rooms = roomRepository.findAllById(ids);
            if (rooms.size() != ids.size()) {
                throw new RuntimeException("部分教室不存在");
            }

            long currentTime = System.currentTimeMillis();
            for (Room room : rooms) {
                if (!room.getIsDeleted()) {
                    throw new RuntimeException("教室 " + room.getRoomName() + " 未被删除，无需恢复");
                }
                room.setIsDeleted(false);
                room.setDeleteTime(null);
                room.setLastUpdateTime(currentTime);
            }

            roomRepository.saveAll(rooms);
            log.info("批量恢复教室成功，数量: {}", rooms.size());
        } catch (Exception e) {
            log.error("批量恢复教室失败", e);
            throw new RuntimeException("批量恢复教室失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void permanentDeleteClassroom(String id) {
        try {
            Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("教室不存在"));
            
            if (!room.getIsDeleted()) {
                throw new RuntimeException("教室未被逻辑删除，无法永久删除");
            }

            roomRepository.delete(room);
            log.info("永久删除教室成功，ID: {}", id);
        } catch (Exception e) {
            log.error("永久删除教室失败，ID: {}", id, e);
            throw new RuntimeException("永久删除教室失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void batchPermanentDeleteClassrooms(List<String> ids) {
        try {
            List<Room> rooms = roomRepository.findAllById(ids);
            if (rooms.size() != ids.size()) {
                throw new RuntimeException("部分教室不存在");
            }

            for (Room room : rooms) {
                if (!room.getIsDeleted()) {
                    throw new RuntimeException("教室 " + room.getRoomName() + " 未被逻辑删除，无法永久删除");
                }
            }

            roomRepository.deleteAll(rooms);
            log.info("批量永久删除教室成功，数量: {}", rooms.size());
        } catch (Exception e) {
            log.error("批量永久删除教室失败", e);
            throw new RuntimeException("批量永久删除教室失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void clearRecycleBin() {
        try {
            List<Room> deletedRooms = roomRepository.findByIsDeletedTrue();
            if (!deletedRooms.isEmpty()) {
                roomRepository.deleteAll(deletedRooms);
                log.info("清空回收站成功，删除数量: {}", deletedRooms.size());
            }
        } catch (Exception e) {
            log.error("清空回收站失败", e);
            throw new RuntimeException("清空回收站失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getRecycleBinStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 总数
            long totalCount = roomRepository.countByIsDeletedTrue();
            stats.put("totalCount", totalCount);
            
            // 今天删除的数量
            long todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long todayEnd = todayStart + 24 * 60 * 60 * 1000 - 1;
            long todayCount = roomRepository.countByIsDeletedTrueAndDeleteTimeBetween(todayStart, todayEnd);
            stats.put("todayCount", todayCount);
            
            // 本周删除的数量
            long weekStart = LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long weekCount = roomRepository.countByIsDeletedTrueAndDeleteTimeGreaterThanEqual(weekStart);
            stats.put("weekCount", weekCount);
            
            return stats;
        } catch (Exception e) {
            log.error("获取回收站统计信息失败", e);
            throw new RuntimeException("获取回收站统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 转换Room实体为ClassroomVo
     */
    private ClassroomVo convertToClassroomVo(Room room, ClassroomApprovalConfig config, List<ClassroomApprovalLevel> levels) {
        ClassroomVo vo = new ClassroomVo();
        vo.setId(room.getId());
        vo.setRoomName(room.getRoomName());
        vo.setRoomNo(room.getRoomNo());
        vo.setRoomAreaName(room.getRoomAreaName());
        vo.setRoomAreaId(room.getRoomAreaId());
        vo.setRoomVolume(room.getRoomVolume());
        vo.setRoomArea(room.getRoomArea() != null ? room.getRoomArea().toString() : null);
        vo.setRemark(room.getRemark());
        vo.setLastUpdateTime(room.getLastUpdateTime() != null ? room.getLastUpdateTime().toString() : null);
        
        // 设置审批相关信息
        if (config != null) {
            vo.setAllowBookerSelectApprover(config.getAllowBookerSelectApprover() ? "yes" : "no");
            vo.setNeedApproval(config.getNeedApproval() ? "yes" : "no");
            
            // 设置审批级别信息
            if (levels != null && !levels.isEmpty()) {
                // 为兼容性设置旧字段
                for (ClassroomApprovalLevel level : levels) {
                    switch (level.getLevelNumber()) {
                        case 1:
                            vo.setFirstApprover(level.getApproverNames());
                            break;
                        case 2:
                            vo.setSecondApprover(level.getApproverNames());
                            break;
                        case 3:
                            vo.setThirdApprover(level.getApproverNames());
                            break;
                    }
                }
                
                // 设置详细的审批级别列表
                List<ClassroomVo.ApprovalLevelDetail> approvalLevels = new ArrayList<>();
                for (ClassroomApprovalLevel level : levels) {
                    ClassroomVo.ApprovalLevelDetail detail = new ClassroomVo.ApprovalLevelDetail();
                    detail.setLevel(level.getLevelNumber());
                    detail.setApprovers(level.getApproverNames());
                    detail.setApproverIds(level.getApproverIds());
                    approvalLevels.add(detail);
                }
                vo.setApprovalLevels(approvalLevels);
            }
        } else {
            // 设置默认值
            vo.setAllowBookerSelectApprover("no");
            vo.setNeedApproval("no");
        }
        
        return vo;
    }
}