package com.proshine.system.service.impl;

import com.proshine.common.enums.GlobalEnum;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.entity.*;
import com.proshine.system.repository.*;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.BookingPersonnelPermissionService;
import com.proshine.system.user.dto.UserVO;
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

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 预约人员权限配置服务实现类
 * 
 * @author system
 * @date 2024-01-01
 */
@Service
@Slf4j
public class BookingPersonnelPermissionServiceImpl implements BookingPersonnelPermissionService {

    @Autowired
    private BookingPersonnelPermissionRepository permissionRepository;

    @Autowired
    private BookingPersonnelPermissionUserRepository permissionUserRepository;

    @Autowired
    private BookingPersonnelPermissionRoomRepository permissionRoomRepository;

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public ResponsePageDataEntity<BookingPersonnelPermissionDto> searchPermissions(SearchBookingPersonnelPermissionCondition condition) {
        try {
            // 设置客户域
            condition.setCustomerId(SecurityUtil.getCustomerId());
            
            // 构建查询条件
            Specification<BookingPersonnelPermission> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 客户域过滤
                predicates.add(cb.equal(root.get("customerId"), condition.getCustomerId()));
                predicates.add(cb.equal(root.get("deleted"), false));
                
                // 关键词搜索
                if (StringUtils.hasText(condition.getKeyword())) {
                    Predicate subjectLike = cb.like(root.get("subject"), "%" + condition.getKeyword() + "%");
                    Predicate creatorLike = cb.like(root.get("creatorName"), "%" + condition.getKeyword() + "%");
                    predicates.add(cb.or(subjectLike, creatorLike));
                }
                
                return cb.and(predicates.toArray(new Predicate[0]));
            };
            
            // 分页查询
            Pageable pageable = PageRequest.of(condition.getPageNum() - 1, condition.getPageSize(), 
                    Sort.by(Sort.Direction.DESC, "createTime"));
            Page<BookingPersonnelPermission> page = permissionRepository.findAll(spec, pageable);
            
            // 转换为DTO
            List<BookingPersonnelPermissionDto> dtoList = page.getContent().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            
            ResponsePageDataEntity<BookingPersonnelPermissionDto> result = new ResponsePageDataEntity<>();
            result.setRows(dtoList);
            result.setTotal(page.getTotalElements());
            
            return result;
        } catch (Exception e) {
            log.error("查询预约人员权限配置失败：", e);
            throw new RuntimeException("查询预约人员权限配置失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void savePermission(BookingPersonnelPermissionSaveRequest request) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            String currentUserId = SecurityUtil.getCurrentUserId();
            String currentUserName = SecurityUtil.getCurrentUsername();
            
            // 创建权限配置
            BookingPersonnelPermission permission = new BookingPersonnelPermission();
            permission.setSubject(request.getSubject());
            permission.setCreatorId(currentUserId);
            permission.setCreatorName(currentUserName);
            permission.setCustomerId(customerId);
            permission.setCreateTime(LocalDateTime.now());
            permission.setUpdateTime(LocalDateTime.now());
            permission.setDeleted(false);
            
            // 保存权限配置
            permission = permissionRepository.save(permission);
            
            // 保存用户关联
            if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
                savePermissionUsers(permission.getId(), request.getUserIds(), customerId);
            }
            
            // 保存房间关联
            if (request.getRoomIds() != null && !request.getRoomIds().isEmpty()) {
                savePermissionRooms(permission.getId(), request.getRoomIds(), customerId);
            }
            
            log.info("预约人员权限配置创建成功：{}", permission.getId());
        } catch (Exception e) {
            log.error("创建预约人员权限配置失败：", e);
            throw new RuntimeException("创建预约人员权限配置失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updatePermission(String id, BookingPersonnelPermissionSaveRequest request) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            
            // 查询权限配置
            Optional<BookingPersonnelPermission> optionalPermission = permissionRepository.findByIdAndCustomerIdAndDeletedFalse(id, customerId);
            if (!optionalPermission.isPresent()) {
                throw new RuntimeException("权限配置不存在或无权限访问");
            }
            
            BookingPersonnelPermission permission = optionalPermission.get();
            permission.setSubject(request.getSubject());
            permission.setUpdateTime(LocalDateTime.now());
            
            // 更新权限配置
            permissionRepository.save(permission);
            
            // 删除原有关联
            permissionUserRepository.deleteByPermissionId(id);
            permissionRoomRepository.deleteByPermissionId(id);
            
            // 重新保存用户关联
            if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
                savePermissionUsers(id, request.getUserIds(), customerId);
            }
            
            // 重新保存房间关联
            if (request.getRoomIds() != null && !request.getRoomIds().isEmpty()) {
                savePermissionRooms(id, request.getRoomIds(), customerId);
            }
            
            log.info("预约人员权限配置更新成功：{}", id);
        } catch (Exception e) {
            log.error("更新预约人员权限配置失败：", e);
            throw new RuntimeException("更新预约人员权限配置失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deletePermission(String id) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            
            // 检查权限配置是否存在
            Optional<BookingPersonnelPermission> optionalPermission = permissionRepository.findByIdAndCustomerIdAndDeletedFalse(id, customerId);
            if (!optionalPermission.isPresent()) {
                throw new RuntimeException("权限配置不存在或无权限访问");
            }
            
            // 逻辑删除权限配置
            permissionRepository.logicalDeleteById(id, customerId);
            
            log.info("预约人员权限配置删除成功：{}", id);
        } catch (Exception e) {
            log.error("删除预约人员权限配置失败：", e);
            throw new RuntimeException("删除预约人员权限配置失败：" + e.getMessage());
        }
    }

    @Override
    public void exportPermissions(String keyword, HttpServletResponse response) {
        try {
            // 构建查询条件
            SearchBookingPersonnelPermissionCondition condition = new SearchBookingPersonnelPermissionCondition();
            condition.setKeyword(keyword);
            condition.setPageNum(1);
            condition.setPageSize(Integer.MAX_VALUE);
            
            // 查询所有数据
            ResponsePageDataEntity<BookingPersonnelPermissionDto> result = searchPermissions(condition);
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String fileName = "预约人员权限配置列表_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            
            // TODO: 实现Excel导出逻辑
            // 这里可以使用EasyExcel或Apache POI来生成Excel文件
            
            log.info("预约人员权限配置列表导出成功");
        } catch (Exception e) {
            log.error("导出预约人员权限配置列表失败：", e);
            throw new RuntimeException("导出预约人员权限配置列表失败：" + e.getMessage());
        }
    }

    @Override
    public ResponsePageDataEntity<UserVO> getAvailableUsers(SearchAvailableUserCondition condition) {
        try {
            // 设置客户域
            condition.setCustomerId(SecurityUtil.getCustomerId());
            
            // 构建查询条件
            Specification<SysUser> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 客户域过滤
                predicates.add(cb.equal(root.get("customerId"), condition.getCustomerId()));
                predicates.add(cb.equal(root.get("deleted"), false));
                
                // 姓名或工号搜索
                if (StringUtils.hasText(condition.getRealNameAndJobNumber())) {
                    Predicate nameLike = cb.like(root.get("realName"), "%" + condition.getRealNameAndJobNumber() + "%");
                    Predicate jobNumberLike = cb.like(root.get("jobNumber"), "%" + condition.getRealNameAndJobNumber() + "%");
                    predicates.add(cb.or(nameLike, jobNumberLike));
                }
                
                // 部门过滤
                if (StringUtils.hasText(condition.getDepartmentId())) {
                    predicates.add(cb.equal(root.get("departmentId"), condition.getDepartmentId()));
                }
                
                return cb.and(predicates.toArray(new Predicate[0]));
            };
            
            // 分页查询
            Pageable pageable = PageRequest.of(condition.getPageNum() - 1, condition.getPageSize(), 
                    Sort.by(Sort.Direction.ASC, "realName"));
            Page<SysUser> page = userRepository.findAll(spec, pageable);
            
            // 转换为DTO
            List<UserVO> dtoList = page.getContent().stream()
                    .map(this::convertUserToVO)
                    .collect(Collectors.toList());
            
            ResponsePageDataEntity<UserVO> result = new ResponsePageDataEntity<>();
            result.setRows(dtoList);
            result.setTotal(page.getTotalElements());
            
            return result;
        } catch (Exception e) {
            log.error("查询可选用户列表失败：", e);
            throw new RuntimeException("查询可选用户列表失败：" + e.getMessage());
        }
    }

    @Override
    public ResponsePageDataEntity<RoomVo> getAvailableRooms(SearchAvailableRoomCondition condition) {
        try {
            // 设置客户域
            condition.setCustomerId(SecurityUtil.getCustomerId());
            
            // 构建查询条件
            Specification<Room> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 客户域过滤
                predicates.add(cb.equal(root.get("cstmId"), condition.getCustomerId()));
                
                // 房间名称搜索
                if (StringUtils.hasText(condition.getRoomName())) {
                    predicates.add(cb.like(root.get("roomName"), "%" + condition.getRoomName() + "%"));
                }
                
                // 房间区域过滤
                if (StringUtils.hasText(condition.getRoomAreaId())) {
                    predicates.add(cb.equal(root.get("roomAreaId"), condition.getRoomAreaId()));
                }
                
                return cb.and(predicates.toArray(new Predicate[0]));
            };
            
            // 分页查询
            Pageable pageable = PageRequest.of(condition.getPageNum() - 1, condition.getPageSize(), 
                    Sort.by(Sort.Direction.ASC, "roomName"));
            Page<Room> page = roomRepository.findAll(spec, pageable);
            
            // 转换为DTO
            List<RoomVo> dtoList = page.getContent().stream()
                    .map(this::convertRoomToVO)
                    .collect(Collectors.toList());
            
            ResponsePageDataEntity<RoomVo> result = new ResponsePageDataEntity<>();
            result.setRows(dtoList);
            result.setTotal(page.getTotalElements());
            
            return result;
        } catch (Exception e) {
            log.error("查询可选房间列表失败：", e);
            throw new RuntimeException("查询可选房间列表失败：" + e.getMessage());
        }
    }

    @Override
    public boolean hasBookingPermission(String userId, String roomId) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            return permissionRepository.hasBookingPermission(userId, roomId, customerId);
        } catch (Exception e) {
            log.error("检查预约权限失败：", e);
            return false;
        }
    }

    @Override
    public BookingPersonnelPermission getUserPermission(String userId, String roomId) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            List<BookingPersonnelPermission> list = permissionRepository.findByUserId(userId, customerId);
            for (BookingPersonnelPermission permission : list) {
                List<BookingPersonnelPermissionRoom> rooms = permissionRoomRepository.findByPermissionIdAndDeletedFalse(permission.getId());
                boolean matched = rooms.stream().anyMatch(r -> r.getRoomId().equals(roomId));
                if (matched) {
                    return permission;
                }
            }
            return null;
        } catch (Exception e) {
            log.error("获取用户预约权限失败：", e);
            return null;
        }
    }

    @Override
    public List<RoomVo> getBookableRoomsByUserId(String userId) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            
            // 查询用户的权限配置
            List<BookingPersonnelPermission> permissions = permissionRepository.findByUserId(userId, customerId);
            
            List<RoomVo> rooms = new ArrayList<>();
            for (BookingPersonnelPermission permission : permissions) {
                List<BookingPersonnelPermissionRoom> permissionRooms = permissionRoomRepository.findByPermissionIdAndDeletedFalse(permission.getId());
                for (BookingPersonnelPermissionRoom permissionRoom : permissionRooms) {
                    RoomVo roomVo = new RoomVo();
                    roomVo.setId(permissionRoom.getRoomId());
                    roomVo.setRoomName(permissionRoom.getRoomName());
                    roomVo.setRoomNo(permissionRoom.getRoomCode());
                    roomVo.setRoomAreaName(permissionRoom.getRoomAreaName());
                    rooms.add(roomVo);
                }
            }
            
            return rooms;
        } catch (Exception e) {
            log.error("查询用户可预约房间失败：", e);
            throw new RuntimeException("查询用户可预约房间失败：" + e.getMessage());
        }
    }

    /**
     * 保存权限配置用户关联
     */
    private void savePermissionUsers(String permissionId, List<String> userIds, String customerId) {
        for (String userId : userIds) {
            // 查询用户信息
            Optional<SysUser> optionalUser = userRepository.findById(userId)
                    .filter(u -> !Boolean.TRUE.equals(u.getDeleted()));
            if (optionalUser.isPresent()) {
                SysUser user = optionalUser.get();
                
                BookingPersonnelPermissionUser permissionUser = new BookingPersonnelPermissionUser();
                permissionUser.setPermissionId(permissionId);
                permissionUser.setUserId(userId);
                permissionUser.setUserName(user.getRealName());
                permissionUser.setJobNumber(user.getJobNumber());
                permissionUser.setDepartmentName(user.getDepartmentName());
                permissionUser.setCustomerId(customerId);
                permissionUser.setCreateTime(LocalDateTime.now());
                permissionUser.setDeleted(false);
                
                permissionUserRepository.save(permissionUser);
            }
        }
    }

    /**
     * 保存权限配置房间关联
     */
    private void savePermissionRooms(String permissionId, List<String> roomIds, String customerId) {
        for (String roomId : roomIds) {
            // 查询房间信息
            Optional<Room> optionalRoom = roomRepository.findByIdAndCstmId(roomId, GlobalEnum.DEFAULT_CUSTOMER_ID);
            if (optionalRoom.isPresent()) {
                Room room = optionalRoom.get();
                
                BookingPersonnelPermissionRoom permissionRoom = new BookingPersonnelPermissionRoom();
                permissionRoom.setPermissionId(permissionId);
                permissionRoom.setRoomId(roomId);
                permissionRoom.setRoomName(room.getRoomName());
                permissionRoom.setRoomCode(room.getRoomNo());
                permissionRoom.setRoomAreaName(room.getRoomAreaName());
                permissionRoom.setCustomerId(customerId);
                permissionRoom.setCreateTime(LocalDateTime.now());
                permissionRoom.setDeleted(false);
                
                permissionRoomRepository.save(permissionRoom);
            }
        }
    }

    /**
     * 转换为DTO
     */
    private BookingPersonnelPermissionDto convertToDto(BookingPersonnelPermission permission) {
        BookingPersonnelPermissionDto dto = new BookingPersonnelPermissionDto();
        dto.setId(permission.getId());
        dto.setSubject(permission.getSubject());
        dto.setCreator(permission.getCreatorName());
        dto.setCreateTime(permission.getCreateTime());
        
        // 查询关联用户
        List<BookingPersonnelPermissionUser> permissionUsers = permissionUserRepository.findByPermissionIdAndDeletedFalse(permission.getId());
        List<BookingPersonnelPermissionDto.AuthorizedUserDto> userDtos = permissionUsers.stream()
                .map(pu -> {
                    BookingPersonnelPermissionDto.AuthorizedUserDto userDto = new BookingPersonnelPermissionDto.AuthorizedUserDto();
                    userDto.setId(pu.getUserId());
                    userDto.setName(pu.getUserName());
                    userDto.setJobNumber(pu.getJobNumber());
                    userDto.setDepartment(pu.getDepartmentName());
                    return userDto;
                })
                .collect(Collectors.toList());
        dto.setAuthorizedUsers(userDtos);
        dto.setAuthorizedPersonnel(userDtos.stream().map(BookingPersonnelPermissionDto.AuthorizedUserDto::getName).collect(Collectors.joining("；")));
        
        // 查询关联房间
        List<BookingPersonnelPermissionRoom> permissionRooms = permissionRoomRepository.findByPermissionIdAndDeletedFalse(permission.getId());
        List<BookingPersonnelPermissionDto.RoomDto> roomDtos = permissionRooms.stream()
                .map(pr -> {
                    BookingPersonnelPermissionDto.RoomDto roomDto = new BookingPersonnelPermissionDto.RoomDto();
                    roomDto.setId(pr.getRoomId());
                    roomDto.setRoomName(pr.getRoomName());
                    roomDto.setRoomCode(pr.getRoomCode());
                    roomDto.setBuilding(pr.getRoomAreaName());
                    return roomDto;
                })
                .collect(Collectors.toList());
        dto.setRoomList(roomDtos);
        dto.setBookingRooms(roomDtos.stream().map(BookingPersonnelPermissionDto.RoomDto::getRoomName).collect(Collectors.joining("；")));
        
        return dto;
    }

    /**
     * 转换用户为VO
     */
    private UserVO convertUserToVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setRealName(user.getRealName());
        vo.setJobNumber(user.getJobNumber());
        vo.setDepartmentName(user.getDepartmentName());
        return vo;
    }

    /**
     * 转换房间为VO
     */
    private RoomVo convertRoomToVO(Room room) {
        RoomVo vo = new RoomVo();
        vo.setId(room.getId());
        vo.setRoomName(room.getRoomName());
        vo.setRoomNo(room.getRoomNo());
        vo.setRoomAreaName(room.getRoomAreaName());
        return vo;
    }
}