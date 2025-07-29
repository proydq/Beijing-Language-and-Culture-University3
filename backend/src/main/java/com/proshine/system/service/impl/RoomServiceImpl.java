package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.RoomSaveVo;
import com.proshine.system.dto.RoomTypeVo;
import com.proshine.system.dto.RoomVo;
import com.proshine.system.dto.SearchRoomCondition;
import com.proshine.system.entity.Room;
import com.proshine.system.exception.RoomException;
import com.proshine.system.repository.RoomRepository;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.AreaService;
import com.proshine.system.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 房间管理服务实现类
 * 
 * @author system
 * @date 2024-01-01
 */
@Service
@Slf4j
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AreaService areaService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ResponsePageDataEntity<RoomVo> searchRooms(SearchRoomCondition condition) {
        // 设置客户域
        String customerId = SecurityUtil.getCustomerId();
        
        // 构建查询条件
        Specification<Room> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域条件
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            
            // 房屋名称模糊查询
            if (StringUtils.hasText(condition.getRoomName())) {
                predicates.add(criteriaBuilder.like(root.get("roomName"), "%" + condition.getRoomName() + "%"));
            }
            
            // 房间号码模糊查询
            if (StringUtils.hasText(condition.getRoomNo())) {
                predicates.add(criteriaBuilder.like(root.get("roomNo"), "%" + condition.getRoomNo() + "%"));
            }
            
            // 房屋类型名称
            if (StringUtils.hasText(condition.getRoomTypeName())) {
                predicates.add(criteriaBuilder.equal(root.get("roomTypeName"), condition.getRoomTypeName()));
            }
            
            // 房间区域ID - 支持递归查询子区域
            if (StringUtils.hasText(condition.getRoomAreaId())) {
                try {
                    // 获取指定区域及其所有子区域的ID列表
                    List<String> areaIds = areaService.getAllChildAreaIds(condition.getRoomAreaId());
                    if (areaIds != null && !areaIds.isEmpty()) {
                        predicates.add(root.get("roomAreaId").in(areaIds));
                    } else {
                        // 如果没有找到子区域，则使用原来的精确匹配
                        predicates.add(criteriaBuilder.equal(root.get("roomAreaId"), condition.getRoomAreaId()));
                    }
                } catch (Exception e) {
                    log.warn("获取子区域ID失败，使用精确匹配查询: {}", e.getMessage());
                    // 如果获取子区域失败，则使用原来的精确匹配
                    predicates.add(criteriaBuilder.equal(root.get("roomAreaId"), condition.getRoomAreaId()));
                }
            }
            
            // 房间区域名称
            if (StringUtils.hasText(condition.getRoomAreaName())) {
                predicates.add(criteriaBuilder.like(root.get("roomAreaName"), "%" + condition.getRoomAreaName() + "%"));
            }
            
            // 房间编码（扩展字段1）
            if (StringUtils.hasText(condition.getRoomCode())) {
                predicates.add(criteriaBuilder.like(root.get("extend1"), "%" + condition.getRoomCode() + "%"));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 分页查询
        Pageable pageable = PageRequest.of(condition.getPageNumber() - 1, condition.getPageSize(),
                Sort.by(Sort.Direction.DESC, "lastUpdateTime"));
        Page<Room> page = roomRepository.findAll(spec, pageable);
        
        // 转换为VO
        List<RoomVo> voList = page.getContent().stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
        
        // 构建返回结果
        ResponsePageDataEntity<RoomVo> result = new ResponsePageDataEntity<>();
        result.setTotal(page.getTotalElements());
        result.setRows(voList);
        
        return result;
    }

    @Override
    public RoomVo getRoomById(String id) {
        String customerId = SecurityUtil.getCustomerId();
        Optional<Room> optional = roomRepository.findByIdAndCstmId(id, customerId);
        if (!optional.isPresent()) {
            throw new RoomException.RoomNotFoundException(id);
        }
        return convertToVo(optional.get());
    }

    @Override
    @Transactional
    public RoomVo saveRoom(RoomSaveVo roomSaveVo) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 校验唯一性
        if (!checkRoomUniqueness(roomSaveVo.getRoomName(), roomSaveVo.getRoomNo(), roomSaveVo.getId())) {
            throw new RoomException.RoomDuplicateException(roomSaveVo.getRoomName(), roomSaveVo.getRoomNo());
        }
        
        Room room;
        boolean isEdit = StringUtils.hasText(roomSaveVo.getId());
        
        if (isEdit) {
            // 编辑模式
            Optional<Room> optional = roomRepository.findByIdAndCstmId(roomSaveVo.getId(), customerId);
            if (!optional.isPresent()) {
                throw new RoomException.RoomNotFoundException(roomSaveVo.getId());
            }
            room = optional.get();
        } else {
            // 新增模式
            room = new Room();
            room.setId(UUID.randomUUID().toString());
            room.setCstmId(customerId);
            room.setCreateTime(System.currentTimeMillis());
        }
        
        // 设置字段
        room.setRoomName(roomSaveVo.getRoomName());
        room.setRoomAreaId(roomSaveVo.getRoomAreaId());
        room.setRoomAreaName(roomSaveVo.getRoomAreaName());
        room.setRoomNo(roomSaveVo.getRoomNo());
        room.setRoomTypeId(roomSaveVo.getRoomTypeId());
        room.setRoomTypeName(roomSaveVo.getRoomTypeName());
        room.setRoomVolume(roomSaveVo.getRoomVolume());
        room.setRemark(roomSaveVo.getRemark());
        room.setExtend1(roomSaveVo.getRoomCode()); // 房间编码存储在extend1
        room.setExtend2(roomSaveVo.getExtend2());
        
        // 处理房间面积字段
        if (StringUtils.hasText(roomSaveVo.getRoomArea())) {
            try {
                room.setRoomArea(Double.parseDouble(roomSaveVo.getRoomArea()));
            } catch (NumberFormatException e) {
                room.setRoomArea(null);
            }
        } else {
            room.setRoomArea(null);
        }
        
        room.setLastUpdateTime(System.currentTimeMillis());
        
        // 保存
        Room saved = roomRepository.save(room);
        return convertToVo(saved);
    }

    @Override
    @Transactional
    public void deleteRoom(String id) {
        String customerId = SecurityUtil.getCustomerId();
        Optional<Room> optional = roomRepository.findByIdAndCstmId(id, customerId);
        if (!optional.isPresent()) {
            throw new RoomException.RoomNotFoundException(id);
        }
        roomRepository.deleteByIdAndCstmId(id, customerId);
    }

    @Override
    @Transactional
    public void batchDeleteRooms(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        String customerId = SecurityUtil.getCustomerId();
        roomRepository.deleteByIdsAndCstmId(ids, customerId);
    }

    @Override
    public String importRooms(MultipartFile file) {
        // TODO: 实现Excel导入逻辑
        throw new RoomException.RoomImportException("导入功能暂未实现");
    }

    @Override
    public void exportRooms(SearchRoomCondition condition, HttpServletResponse response) {
        // TODO: 实现Excel导出逻辑
        throw new RoomException.RoomExportException("导出功能暂未实现");
    }

    @Override
    public List<RoomTypeVo> getRoomTypes() {
        // 返回预定义的房屋类型
        return Arrays.asList(
                RoomTypeVo.of("CLASSROOM", "教室"),
                RoomTypeVo.of("MEETING_ROOM", "会议室"),
                RoomTypeVo.of("LABORATORY", "实验室"),
                RoomTypeVo.of("OFFICE", "办公室"),
                RoomTypeVo.of("DORMITORY", "宿舍"),
                RoomTypeVo.of("LIBRARY", "图书馆"),
                RoomTypeVo.of("CANTEEN", "食堂"),
                RoomTypeVo.of("GYMNASIUM", "体育馆"),
                RoomTypeVo.of("AUDITORIUM", "礼堂"),
                RoomTypeVo.of("OTHER", "其他")
        );
    }

    @Override
    public boolean checkRoomUniqueness(String roomName, String roomNo, String excludeId) {
        String customerId = SecurityUtil.getCustomerId();
        
        Optional<Room> existing;
        if (StringUtils.hasText(excludeId)) {
            // 编辑时排除当前记录
            existing = roomRepository.findByRoomNameAndRoomNoAndCstmIdAndIdNot(roomName, roomNo, customerId, excludeId);
        } else {
            // 新增时直接查询
            existing = roomRepository.findByRoomNameAndRoomNoAndCstmId(roomName, roomNo, customerId);
        }
        
        return !existing.isPresent();
    }

    /**
     * 转换为VO对象
     */
    private RoomVo convertToVo(Room room) {
        if (room == null) {
            return null;
        }
        
        RoomVo vo = new RoomVo();
        vo.setId(room.getId());
        vo.setRoomName(room.getRoomName());
        vo.setRoomAreaId(room.getRoomAreaId());
        vo.setRoomAreaName(room.getRoomAreaName());
        vo.setRoomNo(room.getRoomNo());
        vo.setRoomTypeName(room.getRoomTypeName());
        vo.setRoomVolume(room.getRoomVolume());
        vo.setRemark(room.getRemark());
        vo.setRoomCode(room.getExtend1()); // 房间编码从extend1获取
        vo.setExtend2(room.getExtend2());
        
        // 格式化时间
        if (room.getLastUpdateTime() != null) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(room.getLastUpdateTime()), 
                ZoneId.systemDefault());
            vo.setLastUpdateTime(dateTime.format(DATE_TIME_FORMATTER));
            vo.setUpdateTime(dateTime.format(DATE_TIME_FORMATTER));
        }
        
        // 设置前端显示字段
        vo.setRoomArea(room.getRoomArea() != null ? room.getRoomArea().toString() : ""); // 从roomArea字段获取
        vo.setCapacity(room.getRoomVolume() != null ? room.getRoomVolume().toString() : "");
        
        return vo;
    }
}