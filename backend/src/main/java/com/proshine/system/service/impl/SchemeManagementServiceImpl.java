package com.proshine.system.service.impl;


import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.entity.*;
import com.proshine.system.repository.*;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.SchemeManagementService;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 方案管理服务实现类
 * 
 * @author system
 * @date 2024-01-01
 */
@Service
@Slf4j
public class SchemeManagementServiceImpl implements SchemeManagementService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ClassroomApprovalConfigRepository approvalConfigRepository;

    @Autowired
    private ClassroomApprovalLevelRepository approvalLevelRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private AreaRepository areaRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String CLASSROOM_TYPE_NAME = "教室";

    @Override
    public ResponsePageDataEntity<ClassroomVo> searchClassrooms(SearchClassroomCondition condition) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 如果有审批过滤条件，需要先获取所有符合条件的房间，然后在内存中过滤和分页
        if (StringUtils.hasText(condition.getApprovalFilter()) && !"all".equals(condition.getApprovalFilter())) {
            return searchClassroomsWithApprovalFilter(condition, customerId);
        }
        
        // 没有审批过滤条件时，直接在数据库层面分页
        return searchClassroomsWithoutApprovalFilter(condition, customerId);
    }
    
    /**
     * 带审批过滤条件的教室查询（内存分页）
     */
    private ResponsePageDataEntity<ClassroomVo> searchClassroomsWithApprovalFilter(SearchClassroomCondition condition, String customerId) {
        // 构建查询条件（不包含分页）
        Specification<Room> spec = buildRoomSpecification(condition, customerId);
        
        // 查询所有符合条件的房间
        List<Room> allRooms = roomRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "lastUpdateTime"));
        
        // 获取所有房间的审批配置
        List<String> roomIds = allRooms.stream().map(Room::getId).collect(Collectors.toList());
        Map<String, ClassroomApprovalConfig> configMap = new HashMap<>();
        Map<String, List<ClassroomApprovalLevel>> levelMap = new HashMap<>();
        
        if (!roomIds.isEmpty()) {
            List<ClassroomApprovalConfig> configs = approvalConfigRepository.findByRoomIdInAndCstmId(roomIds, customerId);
            configMap = configs.stream().collect(Collectors.toMap(ClassroomApprovalConfig::getRoomId, config -> config));
            
            List<String> configIds = configs.stream().map(ClassroomApprovalConfig::getId).collect(Collectors.toList());
            if (!configIds.isEmpty()) {
                List<ClassroomApprovalLevel> levels = approvalLevelRepository.findByConfigIdInAndCstmIdOrderByLevelNumber(configIds, customerId);
                levelMap = levels.stream().collect(Collectors.groupingBy(ClassroomApprovalLevel::getConfigId));
            }
        }
        
        // 转换为VO并应用审批过滤
        List<ClassroomVo> allClassroomVos = new ArrayList<>();
        for (Room room : allRooms) {
            ClassroomApprovalConfig config = configMap.get(room.getId());
            String configId = config != null ? config.getId() : null;
            List<ClassroomApprovalLevel> levels = configId != null ? levelMap.get(configId) : null;
            ClassroomVo vo = convertToClassroomVo(room, config, levels);
            
            boolean needApproval = "yes".equals(vo.getNeedApproval());
            boolean shouldInclude = "yes".equals(condition.getApprovalFilter()) ? needApproval : !needApproval;
            if (shouldInclude) {
                allClassroomVos.add(vo);
            }
        }
        
        // 内存分页
        int total = allClassroomVos.size();
        int startIndex = (condition.getPageNumber() - 1) * condition.getPageSize();
        int endIndex = Math.min(startIndex + condition.getPageSize(), total);
        
        List<ClassroomVo> pagedClassroomVos = startIndex < total ? 
            allClassroomVos.subList(startIndex, endIndex) : new ArrayList<>();
        
        ResponsePageDataEntity<ClassroomVo> result = new ResponsePageDataEntity<>();
        result.setTotal((long) total);
        result.setRows(pagedClassroomVos);
        return result;
    }
    
    /**
     * 不带审批过滤条件的教室查询（数据库分页）
     */
    private ResponsePageDataEntity<ClassroomVo> searchClassroomsWithoutApprovalFilter(SearchClassroomCondition condition, String customerId) {
        // 构建查询条件
        Specification<Room> spec = buildRoomSpecification(condition, customerId);
        
        // 分页查询
        Pageable pageable = PageRequest.of(
            condition.getPageNumber() - 1, 
            condition.getPageSize(),
            Sort.by(Sort.Direction.DESC, "lastUpdateTime")
        );
        
        Page<Room> roomPage = roomRepository.findAll(spec, pageable);
        
        // 获取当前页房间的审批配置
        List<String> roomIds = roomPage.getContent().stream().map(Room::getId).collect(Collectors.toList());
        Map<String, ClassroomApprovalConfig> configMap = new HashMap<>();
        Map<String, List<ClassroomApprovalLevel>> levelMap = new HashMap<>();
        
        if (!roomIds.isEmpty()) {
            List<ClassroomApprovalConfig> configs = approvalConfigRepository.findByRoomIdInAndCstmId(roomIds, customerId);
            configMap = configs.stream().collect(Collectors.toMap(ClassroomApprovalConfig::getRoomId, config -> config));
            
            List<String> configIds = configs.stream().map(ClassroomApprovalConfig::getId).collect(Collectors.toList());
            if (!configIds.isEmpty()) {
                List<ClassroomApprovalLevel> levels = approvalLevelRepository.findByConfigIdInAndCstmIdOrderByLevelNumber(configIds, customerId);
                levelMap = levels.stream().collect(Collectors.groupingBy(ClassroomApprovalLevel::getConfigId));
            }
        }
        
        // 转换为VO
        List<ClassroomVo> classroomVos = new ArrayList<>();
        for (Room room : roomPage.getContent()) {
            ClassroomApprovalConfig config = configMap.get(room.getId());
            String configId = config != null ? config.getId() : null;
            List<ClassroomApprovalLevel> levels = configId != null ? levelMap.get(configId) : null;
            ClassroomVo vo = convertToClassroomVo(room, config, levels);
            classroomVos.add(vo);
        }
        
        ResponsePageDataEntity<ClassroomVo> result = new ResponsePageDataEntity<>();
        result.setTotal(roomPage.getTotalElements());
        result.setRows(classroomVos);
        return result;
    }
    
    /**
     * 构建房间查询条件
     */
    private Specification<Room> buildRoomSpecification(SearchClassroomCondition condition, String customerId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域过滤
            predicates.add(criteriaBuilder.equal(root.get("cstmId"), customerId));
            
            // 只查询教室类型
            predicates.add(criteriaBuilder.equal(root.get("roomTypeName"), CLASSROOM_TYPE_NAME));

            // 未被逻辑删除
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            
            // 教室名称模糊查询
            if (StringUtils.hasText(condition.getRoomName())) {
                predicates.add(criteriaBuilder.like(root.get("roomName"), "%" + condition.getRoomName() + "%"));
            }
            
            // 房间号模糊查询
            if (StringUtils.hasText(condition.getRoomNo())) {
                predicates.add(criteriaBuilder.like(root.get("roomNo"), "%" + condition.getRoomNo() + "%"));
            }
            
            // 教室编号模糊查询（存储在extend1字段）
            if (StringUtils.hasText(condition.getRoomCode())) {
                predicates.add(criteriaBuilder.like(root.get("extend1"), "%" + condition.getRoomCode() + "%"));
            }
            
            // 楼栋过滤
            if (StringUtils.hasText(condition.getRoomAreaId())) {
                predicates.add(criteriaBuilder.equal(root.get("roomAreaId"), condition.getRoomAreaId()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    @Transactional
    public ClassroomAddResult addClassroom(ClassroomAddVo classroomAddVo) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 检查教室名称和房间号是否重复
        Optional<Room> existing = roomRepository.findByRoomNameAndRoomNoAndCstmId(
            classroomAddVo.getClassroomName(), 
            classroomAddVo.getRoomNumber(), 
            customerId
        );
        if (existing.isPresent()) {
            throw new RuntimeException("教室名称和房间号已存在");
        }
        
        // 创建Room实体
        Room room = new Room();
        room.setId(UUID.randomUUID().toString());
        room.setCstmId(customerId);
        room.setRoomName(classroomAddVo.getClassroomName());
        room.setRoomNo(classroomAddVo.getRoomNumber());
        room.setRoomAreaId(classroomAddVo.getRoomAreaId());
        room.setRoomAreaName(classroomAddVo.getRoomAreaName());
        room.setRoomTypeName(CLASSROOM_TYPE_NAME); // 设置为教室类型
        room.setRoomVolume(classroomAddVo.getRoomVolume());
        room.setRemark(classroomAddVo.getRemarks());
        room.setExtend1(classroomAddVo.getClassroomCode()); // 教室编号存储在extend1
        
        // 处理房间面积
        if (StringUtils.hasText(classroomAddVo.getRoomArea())) {
            try {
                room.setRoomArea(Double.parseDouble(classroomAddVo.getRoomArea()));
            } catch (NumberFormatException e) {
                room.setRoomArea(null);
            }
        }
        
        room.setCreateTime(System.currentTimeMillis());
        room.setLastUpdateTime(System.currentTimeMillis());
        
        // 保存Room
        Room savedRoom = roomRepository.save(room);
        
        // 创建审批配置
        createApprovalConfig(savedRoom.getId(), classroomAddVo.getAllowBookerSelectApprover(), 
            classroomAddVo.getApprovalLevels(), customerId);
        
        return ClassroomAddResult.of(savedRoom.getId());
    }

    @Override
    public ClassroomVo getClassroomDetail(String id) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 查找教室
        Optional<Room> roomOpt = roomRepository.findByIdAndCstmId(id, customerId);
        if (!roomOpt.isPresent()) {
            throw new RuntimeException("教室不存在");
        }
        
        Room room = roomOpt.get();
        
        // 查找审批配置
        ClassroomApprovalConfig config = approvalConfigRepository.findByRoomIdAndCstmId(id, customerId).get();
        
        // 查找审批级别
        List<ClassroomApprovalLevel> levels = new ArrayList<>();
        if (config != null) {
            levels = approvalLevelRepository.findByConfigIdAndCstmIdOrderByLevelNumber(config.getId(), customerId);
        }
        
        // 转换为VO并返回完整信息
        ClassroomVo vo = convertToClassroomVo(room, config, levels);
        
        // 为详情接口添加更完整的审批级别信息
        if (config != null && !levels.isEmpty()) {
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
        
        return vo;
    }

    @Override
    @Transactional
    public void updateClassroom(ClassroomUpdateVo classroomUpdateVo) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 查找教室
        Optional<Room> roomOpt = roomRepository.findByIdAndCstmId(classroomUpdateVo.getId(), customerId);
        if (!roomOpt.isPresent()) {
            throw new RuntimeException("教室不存在");
        }
        
        Room room = roomOpt.get();
        
        // 更新基本信息
        if (StringUtils.hasText(classroomUpdateVo.getClassroomName())) {
            room.setRoomName(classroomUpdateVo.getClassroomName());
        }
        if (StringUtils.hasText(classroomUpdateVo.getRoomNumber())) {
            room.setRoomNo(classroomUpdateVo.getRoomNumber());
        }
        if (StringUtils.hasText(classroomUpdateVo.getRoomAreaName())) {
            room.setRoomAreaName(classroomUpdateVo.getRoomAreaName());
        }
        if (StringUtils.hasText(classroomUpdateVo.getRoomAreaId())) {
            room.setRoomAreaId(classroomUpdateVo.getRoomAreaId());
        }
        if (StringUtils.hasText(classroomUpdateVo.getRemarks())) {
            room.setRemark(classroomUpdateVo.getRemarks());
        }
        if (classroomUpdateVo.getRoomVolume() != null) {
            room.setRoomVolume(classroomUpdateVo.getRoomVolume());
        }
        
        // 处理房间面积
        if (StringUtils.hasText(classroomUpdateVo.getRoomArea())) {
            try {
                room.setRoomArea(Double.parseDouble(classroomUpdateVo.getRoomArea()));
            } catch (NumberFormatException e) {
                room.setRoomArea(null);
            }
        }
        
        room.setLastUpdateTime(System.currentTimeMillis());
        
        // 保存Room
        roomRepository.save(room);
        
        // 更新审批配置
        updateApprovalConfig(room.getId(), classroomUpdateVo.getAllowBookerSelectApprover(), 
            classroomUpdateVo.getApprovalLevels(), customerId);
    }

    @Override
    @Transactional
    public void batchDeleteClassrooms(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        String customerId = SecurityUtil.getCustomerId();
        
        // 逻辑删除Room（不删除审批配置，以便恢复时能保留原有的审批设置）
        long now = System.currentTimeMillis();
        roomRepository.logicalDeleteByIdsAndCstmId(ids, customerId, now);
        
        log.info("批量逻辑删除教室成功，数量: {}", ids.size());
    }

    @Override
    @Transactional
    public void batchSetApproval(BatchApprovalVo batchApprovalVo) {
        String customerId = SecurityUtil.getCustomerId();
        
        for (String roomId : batchApprovalVo.getRoomIds()) {
            updateBatchApprovalConfig(roomId, batchApprovalVo.getAllowBookerSelectApprover(), 
                batchApprovalVo.getApprovalLevels(), customerId);
        }
    }

    @Override
    @Transactional
    public SyncResult syncClassroomsFromRoom() {
        String customerId = SecurityUtil.getCustomerId();
        
        // 查询所有roomTypeName为"教室"的Room记录
        List<Room> classroomRooms = roomRepository.findByRoomTypeNameAndCstmId(CLASSROOM_TYPE_NAME, customerId);
        
        int syncCount = 0;
        
        for (Room room : classroomRooms) {
            // 检查是否已存在审批配置
            if (!approvalConfigRepository.existsByRoomIdAndCstmId(room.getId(), customerId)) {
                // 创建默认审批配置
                createDefaultApprovalConfig(room.getId(), customerId);
                syncCount++;
            }
        }
        
        return SyncResult.of(syncCount);
    }

    @Override
    public ResponsePageDataEntity<PersonnelVo> searchPersonnel(SearchPersonnelCondition condition) {
        String customerId = SecurityUtil.getCustomerId();
        
        // 构建查询条件
        Specification<SysUser> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域过滤
            predicates.add(criteriaBuilder.equal(root.get("customerId"), customerId));
            
            // 未删除
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            
            // 姓名模糊查询
            if (StringUtils.hasText(condition.getName())) {
                predicates.add(criteriaBuilder.like(root.get("realName"), "%" + condition.getName() + "%"));
            }
            
            // 部门模糊查询
            if (StringUtils.hasText(condition.getDepartment())) {
                predicates.add(criteriaBuilder.like(root.get("departmentName"), "%" + condition.getDepartment() + "%"));
            }
            
            // 职位模糊查询
            if (StringUtils.hasText(condition.getPosition())) {
                predicates.add(criteriaBuilder.like(root.get("positionName"), "%" + condition.getPosition() + "%"));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 分页查询
        Pageable pageable = PageRequest.of(
            condition.getPageNumber() - 1, 
            condition.getPageSize(),
            Sort.by(Sort.Direction.ASC, "realName")
        );
        
        Page<SysUser> userPage = sysUserRepository.findAll(spec, pageable);
        
        // 转换为VO
        List<PersonnelVo> personnelVos = userPage.getContent().stream()
            .map(this::convertToPersonnelVo)
            .collect(Collectors.toList());
        
        ResponsePageDataEntity<PersonnelVo> result = new ResponsePageDataEntity<>();
        result.setTotal(userPage.getTotalElements());
        result.setRows(personnelVos);
        return result;
    }

    @Override
    public void exportClassrooms(List<String> roomIds, HttpServletResponse response) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String fileName = "classrooms_" + System.currentTimeMillis() + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            
            // 查询要导出的教室数据
            List<Room> rooms;
            if (roomIds != null && !roomIds.isEmpty()) {
                rooms = roomRepository.findByIdsAndCstmId(roomIds, customerId);
            } else {
                rooms = roomRepository.findByRoomTypeNameAndCstmId(CLASSROOM_TYPE_NAME, customerId);
            }
            
            // 获取审批配置
            List<String> allRoomIds = rooms.stream().map(Room::getId).collect(Collectors.toList());
            Map<String, ClassroomApprovalConfig> configMap = new HashMap<>();
            Map<String, List<ClassroomApprovalLevel>> levelMap = new HashMap<>();
            
            if (!allRoomIds.isEmpty()) {
                List<ClassroomApprovalConfig> configs = approvalConfigRepository.findByRoomIdInAndCstmId(allRoomIds, customerId);
                configMap = configs.stream().collect(Collectors.toMap(ClassroomApprovalConfig::getRoomId, config -> config));
                
                List<String> configIds = configs.stream().map(ClassroomApprovalConfig::getId).collect(Collectors.toList());
                if (!configIds.isEmpty()) {
                    List<ClassroomApprovalLevel> levels = approvalLevelRepository.findByConfigIdInAndCstmIdOrderByLevelNumber(configIds, customerId);
                    levelMap = levels.stream().collect(Collectors.groupingBy(ClassroomApprovalLevel::getConfigId));
                }
            }
            
            // 构建CSV内容（简化实现，实际项目中建议使用Apache POI生成Excel）
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("教室名称,房间号,教室编号,楼栋,容量,面积,是否需要审批,允许预约人选择审批人,一级审批人,二级审批人,三级审批人,备注\n");
            
            for (Room room : rooms) {
                ClassroomApprovalConfig config = configMap.get(room.getId());
                List<ClassroomApprovalLevel> levels = config != null ? levelMap.get(config.getId()) : null;
                
                csvContent.append(escapeCSV(room.getRoomName())).append(",");
                csvContent.append(escapeCSV(room.getRoomNo())).append(",");
                csvContent.append(escapeCSV(room.getExtend1())).append(",");
                csvContent.append(escapeCSV(room.getRoomAreaName())).append(",");
                csvContent.append(room.getRoomVolume() != null ? room.getRoomVolume() : "").append(",");
                csvContent.append(room.getRoomArea() != null ? room.getRoomArea() : "").append(",");
                
                if (config != null) {
                    csvContent.append(config.getNeedApproval() ? "是" : "否").append(",");
                    csvContent.append(config.getAllowBookerSelectApprover() ? "是" : "否").append(",");
                    
                    String firstApprover = "";
                    String secondApprover = "";
                    String thirdApprover = "";
                    
                    if (levels != null) {
                        for (ClassroomApprovalLevel level : levels) {
                            switch (level.getLevelNumber()) {
                                case 1:
                                    firstApprover = level.getApproverNames();
                                    break;
                                case 2:
                                    secondApprover = level.getApproverNames();
                                    break;
                                case 3:
                                    thirdApprover = level.getApproverNames();
                                    break;
                            }
                        }
                    }
                    
                    csvContent.append(escapeCSV(firstApprover)).append(",");
                    csvContent.append(escapeCSV(secondApprover)).append(",");
                    csvContent.append(escapeCSV(thirdApprover)).append(",");
                } else {
                    csvContent.append("否,是,,,");
                }
                
                csvContent.append(escapeCSV(room.getRemark())).append("\n");
            }
            
            // 写入响应
            response.getWriter().write(csvContent.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            log.error("导出教室数据失败", e);
            throw new RuntimeException("导出教室数据失败: " + e.getMessage());
        }
    }

    @Override
    public ImportResult importClassrooms(MultipartFile file) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            
            if (file.isEmpty()) {
                throw new RuntimeException("导入文件不能为空");
            }
            
            // 读取文件内容（简化实现，假设是CSV格式）
            String content = new String(file.getBytes(), "UTF-8");
            String[] lines = content.split("\n");
            
            if (lines.length <= 1) {
                throw new RuntimeException("导入文件格式错误或无数据");
            }
            
            int successCount = 0;
            List<String> errors = new ArrayList<>();
            
            // 跳过标题行，从第二行开始处理
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                try {
                    String[] fields = parseCSVLine(line);
                    if (fields.length < 6) {
                        errors.add("第" + (i + 1) + "行：数据格式错误，字段数量不足");
                        continue;
                    }
                    
                    String roomName = fields[0].trim();
                    String roomNo = fields[1].trim();
                    String roomCode = fields[2].trim();
                    String building = fields[3].trim();
                    String volumeStr = fields[4].trim();
                    String areaStr = fields[5].trim();
                    
                    if (roomName.isEmpty() || roomNo.isEmpty()) {
                        errors.add("第" + (i + 1) + "行：教室名称和房间号不能为空");
                        continue;
                    }
                    
                    // 检查是否已存在
                    Optional<Room> existing = roomRepository.findByRoomNameAndRoomNoAndCstmId(roomName, roomNo, customerId);
                    if (existing.isPresent()) {
                        errors.add("第" + (i + 1) + "行：教室名称和房间号已存在");
                        continue;
                    }
                    
                    // 创建Room实体
                    Room room = new Room();
                    room.setId(UUID.randomUUID().toString());
                    room.setCstmId(customerId);
                    room.setRoomName(roomName);
                    room.setRoomNo(roomNo);
                    room.setExtend1(roomCode);
                    room.setRoomAreaName(building);
                    room.setRoomTypeName(CLASSROOM_TYPE_NAME);
                    
                    // 处理容量
                    if (!volumeStr.isEmpty()) {
                        try {
                            room.setRoomVolume(Integer.parseInt(volumeStr));
                        } catch (NumberFormatException e) {
                            errors.add("第" + (i + 1) + "行：容量格式错误");
                            continue;
                        }
                    }
                    
                    // 处理面积
                    if (!areaStr.isEmpty()) {
                        try {
                            room.setRoomArea(Double.parseDouble(areaStr));
                        } catch (NumberFormatException e) {
                            errors.add("第" + (i + 1) + "行：面积格式错误");
                            continue;
                        }
                    }
                    
                    room.setCreateTime(System.currentTimeMillis());
                    room.setLastUpdateTime(System.currentTimeMillis());
                    
                    // 保存Room
                    Room savedRoom = roomRepository.save(room);
                    
                    // 创建默认审批配置
                    createDefaultApprovalConfig(savedRoom.getId(), customerId);
                    
                    successCount++;
                    
                } catch (Exception e) {
                    errors.add("第" + (i + 1) + "行：处理失败 - " + e.getMessage());
                }
            }
            
            ImportResult result = new ImportResult();
             result.setSuccessCount(successCount);
             result.setFailCount(errors.size());
             result.setErrors(errors);
             
             if (!errors.isEmpty()) {
                 log.warn("导入过程中出现错误: {}", String.join("; ", errors));
             }
             
             return result;
            
        } catch (Exception e) {
            log.error("导入教室数据失败", e);
            throw new RuntimeException("导入教室数据失败: " + e.getMessage());
        }
    }
    
    /**
     * CSV字段转义
     */
    private String escapeCSV(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
    
    /**
     * 解析CSV行
     */
    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentField.append('"');
                    i++; // 跳过下一个引号
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    /**
     * 转换为教室VO
     */
    private ClassroomVo convertToClassroomVo(Room room, ClassroomApprovalConfig config, List<ClassroomApprovalLevel> levels) {
        ClassroomVo vo = new ClassroomVo();
        vo.setId(room.getId());
        vo.setRoomName(room.getRoomName());
        vo.setRoomNo(room.getRoomNo());
        vo.setRoomAreaName(room.getRoomAreaName());
        vo.setRoomAreaId(room.getRoomAreaId());
        vo.setRoomCode(room.getExtend1());
        vo.setRoomVolume(room.getRoomVolume());
        vo.setRemark(room.getRemark());
        vo.setRoomArea(room.getRoomArea() != null ? room.getRoomArea().toString() : "");
        
        // 格式化时间
        if (room.getLastUpdateTime() != null) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(room.getLastUpdateTime()), 
                ZoneId.systemDefault());
            vo.setLastUpdateTime(dateTime.format(DATE_TIME_FORMATTER));
        }
        
        // 设置审批相关信息
        if (config != null) {
            vo.setNeedApproval(config.getNeedApproval() ? "yes" : "no");
            vo.setAllowBookerSelectApprover(config.getAllowBookerSelectApprover() ? "yes" : "no");
            
            // 设置审批人信息
            if (levels != null && !levels.isEmpty()) {
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
            }
        } else {
            vo.setNeedApproval("no");
            vo.setAllowBookerSelectApprover("yes");
        }
        
        // 设置是否可调换（暂时默认为yes）
        vo.setIsExchange("yes");
        
        return vo;
    }

    /**
     * 转换为人员VO
     */
    private PersonnelVo convertToPersonnelVo(SysUser user) {
        PersonnelVo vo = new PersonnelVo();
        vo.setId(user.getId());
        vo.setName(user.getRealName());
        vo.setDepartment(user.getDepartmentName());
        vo.setPosition(user.getPositionName());
        vo.setPhone(user.getPhone());
        vo.setEmployeeNo(user.getJobNumber());
        vo.setUsername(user.getUsername());
        return vo;
    }

    /**
     * 创建审批配置
     */
    private void createApprovalConfig(String roomId, String allowBookerSelectApprover, 
                                    List<ClassroomAddVo.ApprovalLevelVo> approvalLevels, String customerId) {
        // 创建审批配置
        ClassroomApprovalConfig config = new ClassroomApprovalConfig();
        config.setId(UUID.randomUUID().toString());
        config.setCstmId(customerId);
        config.setRoomId(roomId);
        config.setAllowBookerSelectApprover("yes".equals(allowBookerSelectApprover));
        config.setNeedApproval(approvalLevels != null && !approvalLevels.isEmpty());
        
        ClassroomApprovalConfig savedConfig = approvalConfigRepository.save(config);
        
        // 创建审批级别
        if (approvalLevels != null && !approvalLevels.isEmpty()) {
            for (ClassroomAddVo.ApprovalLevelVo levelVo : approvalLevels) {
                ClassroomApprovalLevel level = new ClassroomApprovalLevel();
                level.setId(UUID.randomUUID().toString());
                level.setCstmId(customerId);
                level.setConfigId(savedConfig.getId());
                level.setLevelNumber(levelVo.getLevel());
                level.setApproverNames(levelVo.getApprovers());
                level.setApproverIds(levelVo.getApproverIds());
                
                approvalLevelRepository.save(level);
            }
        }
    }

    /**
     * 更新审批配置 - 用于ClassroomUpdateVo
     */
    private void updateApprovalConfig(String roomId, String allowBookerSelectApprover, 
                                    List<ClassroomUpdateVo.ApprovalLevelVo> approvalLevels, String customerId) {
        updateApprovalConfigInternal(roomId, allowBookerSelectApprover, approvalLevels, customerId);
    }
    
    /**
     * 更新审批配置 - 用于BatchApprovalVo
     */
    private void updateBatchApprovalConfig(String roomId, String allowBookerSelectApprover, 
                                         List<BatchApprovalVo.ApprovalLevelVo> approvalLevels, String customerId) {
        updateApprovalConfigInternal(roomId, allowBookerSelectApprover, approvalLevels, customerId);
    }
    
    /**
     * 更新审批配置的内部实现
     */
    private <T> void updateApprovalConfigInternal(String roomId, String allowBookerSelectApprover, 
                                                List<T> approvalLevels, String customerId) {
        // 查找或创建审批配置
        Optional<ClassroomApprovalConfig> configOpt = approvalConfigRepository.findByRoomIdAndCstmId(roomId, customerId);
        ClassroomApprovalConfig config;
        
        if (configOpt.isPresent()) {
            config = configOpt.get();
            // 删除旧的审批级别
            approvalLevelRepository.deleteByConfigIdAndCstmId(config.getId(), customerId);
        } else {
            config = new ClassroomApprovalConfig();
            config.setId(UUID.randomUUID().toString());
            config.setCstmId(customerId);
            config.setRoomId(roomId);
        }
        
        // 更新配置
        if (StringUtils.hasText(allowBookerSelectApprover)) {
            config.setAllowBookerSelectApprover("yes".equals(allowBookerSelectApprover));
        }
        config.setNeedApproval(approvalLevels != null && !approvalLevels.isEmpty());
        
        ClassroomApprovalConfig savedConfig = approvalConfigRepository.save(config);
        
        // 创建新的审批级别
        if (approvalLevels != null && !approvalLevels.isEmpty()) {
            for (T levelVo : approvalLevels) {
                try {
                    ClassroomApprovalLevel level = new ClassroomApprovalLevel();
                    level.setId(UUID.randomUUID().toString());
                    level.setCstmId(customerId);
                    level.setConfigId(savedConfig.getId());
                    
                    // 使用反射获取属性值
                    Class<?> clazz = levelVo.getClass();
                    Integer levelNum = (Integer) clazz.getMethod("getLevel").invoke(levelVo);
                    String approvers = (String) clazz.getMethod("getApprovers").invoke(levelVo);
                    String approverIds = (String) clazz.getMethod("getApproverIds").invoke(levelVo);
                    
                    level.setLevelNumber(levelNum);
                    level.setApproverNames(approvers);
                    level.setApproverIds(approverIds);
                    
                    approvalLevelRepository.save(level);
                } catch (Exception e) {
                    log.error("处理审批级别数据失败", e);
                }
            }
        }
    }

    /**
     * 创建默认审批配置
     */
    private void createDefaultApprovalConfig(String roomId, String customerId) {
        ClassroomApprovalConfig config = new ClassroomApprovalConfig();
        config.setId(UUID.randomUUID().toString());
        config.setCstmId(customerId);
        config.setRoomId(roomId);
        config.setAllowBookerSelectApprover(true);
        config.setNeedApproval(false);
        
        approvalConfigRepository.save(config);
    }
}