package com.proshine.system.controller;

import com.proshine.system.dto.SearchTerminalCondition;
import com.proshine.system.dto.TerminalDto;
import com.proshine.system.entity.Terminal;
import com.proshine.system.service.TerminalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 终端设备管理控制器
 * 
 * @author system
 * @date 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/terminals")
@Validated
public class TerminalController {

    @Autowired
    private TerminalService terminalService;

    /**
     * 创建终端
     */
    @PostMapping
    @PreAuthorize("hasPermission(null, 'terminal:create')")
    public ResponseEntity<TerminalDto> createTerminal(@Valid @RequestBody TerminalDto terminalDto) {
        log.info("创建终端: {}", terminalDto.getTerminalName());
        
        // 检查终端ID是否已存在
        if (terminalService.existsByTerminalId(terminalDto.getTerminalId(), terminalDto.getCstmId())) {
            return ResponseEntity.badRequest().build();
        }
        
        Terminal terminal = convertToEntity(terminalDto);
        Terminal savedTerminal = terminalService.saveOrUpdate(terminal);
        
        return ResponseEntity.ok(convertToDto(savedTerminal));
    }

    /**
     * 更新终端
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'terminal:update')")
    public ResponseEntity<TerminalDto> updateTerminal(@PathVariable String id, 
                                                     @Valid @RequestBody TerminalDto terminalDto) {
        log.info("更新终端: ID={}, 名称={}", id, terminalDto.getTerminalName());
        
        Optional<Terminal> existingOpt = terminalService.findById(id);
        if (!existingOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Terminal existing = existingOpt.get();
        
        // 检查终端ID是否被其他终端占用
        if (!existing.getTerminalId().equals(terminalDto.getTerminalId()) &&
            terminalService.existsByTerminalId(terminalDto.getTerminalId(), terminalDto.getCstmId())) {
            return ResponseEntity.badRequest().build();
        }
        
        // 保留原有的创建时间和创建用户
        terminalDto.setCreateTime(existing.getCreateTime());
        terminalDto.setCreateUser(existing.getCreateUser());
        
        Terminal terminal = convertToEntity(terminalDto);
        terminal.setId(id);
        Terminal savedTerminal = terminalService.saveOrUpdate(terminal);
        
        return ResponseEntity.ok(convertToDto(savedTerminal));
    }

    /**
     * 根据ID查询终端
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'terminal:read')")
    public ResponseEntity<TerminalDto> getTerminal(@PathVariable String id) {
        Optional<Terminal> terminalOpt = terminalService.findById(id);
        if (terminalOpt.isPresent()) {
            return ResponseEntity.ok(convertToDto(terminalOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 分页查询终端列表
     */
    @PostMapping("/search")
    @PreAuthorize("hasPermission(null, 'terminal:read')")
    public ResponseEntity<Page<TerminalDto>> searchTerminals(@RequestBody SearchTerminalCondition condition) {
        log.info("查询终端列表: 客户域={}, 条件={}", condition.getCstmId(), condition);
        
        Sort sort = Sort.by("desc".equals(condition.getSortDirection()) ? 
            Sort.Direction.DESC : Sort.Direction.ASC, condition.getSortField());
        Pageable pageable = PageRequest.of(condition.getPage(), condition.getSize(), sort);
        
        Page<Terminal> terminals = terminalService.findByConditions(
            condition.getTerminalName(),
            condition.getOnlineState(),
            condition.getRoomId(),
            condition.getCstmId(),
            pageable
        );
        
        Page<TerminalDto> result = terminals.map(this::convertToDto);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询客户域下所有终端
     */
    @GetMapping("/customer/{cstmId}")
    @PreAuthorize("hasPermission(null, 'terminal:read')")
    public ResponseEntity<List<TerminalDto>> getTerminalsByCustomer(@PathVariable String cstmId) {
        List<Terminal> terminals = terminalService.findAllByCustomerId(cstmId);
        List<TerminalDto> result = terminals.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * 根据房间ID查询绑定的终端
     */
    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasPermission(null, 'terminal:read')")
    public ResponseEntity<List<TerminalDto>> getTerminalsByRoom(@PathVariable String roomId,
                                                               @RequestParam String cstmId) {
        List<Terminal> terminals = terminalService.findByRoomId(roomId, cstmId);
        List<TerminalDto> result = terminals.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * 查询在线终端列表
     */
    @GetMapping("/online/{cstmId}")
    @PreAuthorize("hasPermission(null, 'terminal:read')")
    public ResponseEntity<List<TerminalDto>> getOnlineTerminals(@PathVariable String cstmId) {
        List<Terminal> terminals = terminalService.findOnlineTerminals(cstmId);
        List<TerminalDto> result = terminals.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * 查询离线终端列表
     */
    @GetMapping("/offline/{cstmId}")
    @PreAuthorize("hasPermission(null, 'terminal:read')")
    public ResponseEntity<List<TerminalDto>> getOfflineTerminals(@PathVariable String cstmId) {
        List<Terminal> terminals = terminalService.findOfflineTerminals(cstmId);
        List<TerminalDto> result = terminals.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * 查询未绑定房间的终端
     */
    @GetMapping("/unbound/{cstmId}")
    @PreAuthorize("hasPermission(null, 'terminal:read')")
    public ResponseEntity<List<TerminalDto>> getUnboundTerminals(@PathVariable String cstmId) {
        List<Terminal> terminals = terminalService.findUnboundTerminals(cstmId);
        List<TerminalDto> result = terminals.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * 删除终端
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'terminal:delete')")
    public ResponseEntity<Void> deleteTerminal(@PathVariable String id) {
        log.info("删除终端: ID={}", id);
        terminalService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除终端
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasPermission(null, 'terminal:delete')")
    public ResponseEntity<Void> batchDeleteTerminals(@RequestBody @NotEmpty List<String> ids) {
        log.info("批量删除终端: 数量={}", ids.size());
        terminalService.deleteByIds(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 绑定终端到房间
     */
    @PostMapping("/{terminalId}/bind/{roomId}")
    @PreAuthorize("hasPermission(null, 'terminal:update')")
    public ResponseEntity<Void> bindToRoom(@PathVariable String terminalId,
                                          @PathVariable String roomId,
                                          @RequestParam String cstmId) {
        log.info("绑定终端到房间: 终端ID={}, 房间ID={}", terminalId, roomId);
        terminalService.bindToRoom(terminalId, roomId, cstmId);
        return ResponseEntity.ok().build();
    }

    /**
     * 解绑终端和房间
     */
    @PostMapping("/{terminalId}/unbind")
    @PreAuthorize("hasPermission(null, 'terminal:update')")
    public ResponseEntity<Void> unbindFromRoom(@PathVariable String terminalId,
                                              @RequestParam String cstmId) {
        log.info("解绑终端和房间: 终端ID={}", terminalId);
        terminalService.unbindFromRoom(terminalId, cstmId);
        return ResponseEntity.ok().build();
    }

    /**
     * 终端上线
     */
    @PostMapping("/{terminalId}/online")
    @PreAuthorize("hasPermission(null, 'terminal:update')")
    public ResponseEntity<Void> terminalOnline(@PathVariable String terminalId,
                                              @RequestParam String cstmId,
                                              @RequestParam(required = false) String ipaddr) {
        log.info("终端上线: 终端ID={}, IP={}", terminalId, ipaddr);
        terminalService.terminalOnline(terminalId, ipaddr, cstmId);
        return ResponseEntity.ok().build();
    }

    /**
     * 终端下线
     */
    @PostMapping("/{terminalId}/offline")
    @PreAuthorize("hasPermission(null, 'terminal:update')")
    public ResponseEntity<Void> terminalOffline(@PathVariable String terminalId,
                                               @RequestParam String cstmId) {
        log.info("终端下线: 终端ID={}", terminalId);
        terminalService.terminalOffline(terminalId, cstmId);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新终端存活时间（心跳）
     */
    @PostMapping("/{terminalId}/heartbeat")
    @PreAuthorize("hasPermission(null, 'terminal:update')")
    public ResponseEntity<Void> updateHeartbeat(@PathVariable String terminalId,
                                               @RequestParam String cstmId) {
        terminalService.updateLastAliveTime(terminalId, cstmId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取终端统计信息
     */
    @GetMapping("/statistics/{cstmId}")
    @PreAuthorize("hasPermission(null, 'terminal:read')")
    public ResponseEntity<TerminalService.TerminalStatistics> getStatistics(@PathVariable String cstmId) {
        TerminalService.TerminalStatistics statistics = terminalService.getStatistics(cstmId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 检查终端ID是否存在
     */
    @GetMapping("/check/{terminalId}")
    @PreAuthorize("hasPermission(null, 'terminal:read')")
    public ResponseEntity<Map<String, Boolean>> checkTerminalIdExists(@PathVariable String terminalId,
                                                                     @RequestParam String cstmId) {
        boolean exists = terminalService.existsByTerminalId(terminalId, cstmId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * 实体转DTO
     */
    private TerminalDto convertToDto(Terminal terminal) {
        TerminalDto dto = new TerminalDto();
        BeanUtils.copyProperties(terminal, dto);
        
        // 设置在线状态相关字段
        dto.setOnlineState(terminal.getOnlineState() != null ? terminal.getOnlineState().getCode() : 0);
        dto.setOnlineStateDesc(terminal.getOnlineState() != null ? terminal.getOnlineState().getDescription() : "离线");
        dto.setIsOnline(terminal.isOnline());
        
        // 设置门禁相关字段
        dto.setEntranceOpenStatus(terminal.getEntranceOpenStatus() != null ? terminal.getEntranceOpenStatus().getCode() : null);
        dto.setEntranceOpenStatusDesc(terminal.getEntranceOpenStatus() != null ? terminal.getEntranceOpenStatus().getDescription() : null);
        dto.setEntranceType(terminal.getEntranceType() != null ? terminal.getEntranceType().getCode() : null);
        dto.setEntranceTypeDesc(terminal.getEntranceType() != null ? terminal.getEntranceType().getDescription() : null);
        dto.setIsEntranceTerminal(terminal.isEntranceTerminal());
        dto.setIsEntranceAlwaysOpen(terminal.isEntranceAlwaysOpen());
        
        // 设置存储相关字段
        dto.setStorageUsageRate(terminal.getStorageUsageRate());
        dto.setFreeSpaceRate(terminal.getFreeSpaceRate());
        dto.setFormattedTotalSpace(terminal.getFormattedTotalSpace());
        dto.setFormattedFreeSpace(terminal.getFormattedFreeSpace());
        
        return dto;
    }

    /**
     * DTO转实体
     */
    private Terminal convertToEntity(TerminalDto dto) {
        Terminal terminal = new Terminal();
        BeanUtils.copyProperties(dto, terminal);
        
        // 设置在线状态
        if (dto.getOnlineState() != null) {
            terminal.setOnlineState(Terminal.OnlineState.fromCode(dto.getOnlineState()));
        }
        
        // 设置门禁相关枚举
        if (dto.getEntranceOpenStatus() != null) {
            terminal.setEntranceOpenStatus(Terminal.EntranceOpenStatus.fromCode(dto.getEntranceOpenStatus()));
        }
        if (dto.getEntranceType() != null) {
            terminal.setEntranceType(Terminal.EntranceType.fromCode(dto.getEntranceType()));
        }
        
        return terminal;
    }
}