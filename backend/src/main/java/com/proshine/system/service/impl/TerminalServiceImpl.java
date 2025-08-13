package com.proshine.system.service.impl;

import com.proshine.system.entity.Terminal;
import com.proshine.system.repository.TerminalRepository;
import com.proshine.system.service.TerminalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 终端设备服务实现类
 * 
 * @author system
 * @date 2024-01-01
 */
@Slf4j
@Service
@Transactional
public class TerminalServiceImpl implements TerminalService {

    @Autowired
    private TerminalRepository terminalRepository;

    @Override
    public Terminal saveOrUpdate(Terminal terminal) {
        if (terminal.getCreateTime() == null) {
            terminal.setCreateTime(new Date());
        }
        
        log.info("保存/更新终端: ID={}, 终端ID={}, 名称={}", 
            terminal.getId(), terminal.getTerminalId(), terminal.getTerminalName());
        
        return terminalRepository.save(terminal);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Terminal> findById(String id) {
        return terminalRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Terminal> findByTerminalId(String terminalId, String cstmId) {
        return terminalRepository.findByTerminalIdAndCstmId(terminalId, cstmId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Terminal> findAllByCustomerId(String cstmId) {
        return terminalRepository.findByCstmIdOrderByCreateTimeDesc(cstmId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Terminal> findByCustomerId(String cstmId, Pageable pageable) {
        return terminalRepository.findByCstmIdOrderByCreateTimeDesc(cstmId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Terminal> findByRoomId(String roomId, String cstmId) {
        return terminalRepository.findByRoomId(roomId, cstmId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Terminal> findFirstByRoomId(String roomId, String cstmId) {
        return terminalRepository.findFirstByRoomId(roomId, cstmId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Terminal> findOnlineTerminals(String cstmId) {
        return terminalRepository.findOnlineTerminals(cstmId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Terminal> findOfflineTerminals(String cstmId) {
        return terminalRepository.findOfflineTerminals(cstmId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Terminal> findByNameContaining(String name, String cstmId) {
        return terminalRepository.findByTerminalNameContaining(name, cstmId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Terminal> findByConditions(String terminalName, Integer onlineState, 
                                         String roomId, String cstmId, Pageable pageable) {
        return terminalRepository.findByConditions(terminalName, onlineState, roomId, cstmId, pageable);
    }

    @Override
    public void deleteById(String id) {
        log.info("删除终端: ID={}", id);
        terminalRepository.deleteById(id);
    }

    @Override
    public void deleteByIds(List<String> ids) {
        log.info("批量删除终端: 数量={}", ids.size());
        terminalRepository.deleteAllById(ids);
    }

    @Override
    public void bindToRoom(String terminalId, String roomId, String cstmId) {
        Optional<Terminal> terminalOpt = findByTerminalId(terminalId, cstmId);
        if (terminalOpt.isPresent()) {
            Terminal terminal = terminalOpt.get();
            String oldRoomId = terminal.getRoomId();
            terminal.setRoomId(roomId);
            terminalRepository.save(terminal);
            
            log.info("终端绑定房间: 终端ID={}, 原房间ID={}, 新房间ID={}", 
                terminalId, oldRoomId, roomId);
        } else {
            log.warn("终端不存在，无法绑定房间: 终端ID={}, 房间ID={}", terminalId, roomId);
            throw new IllegalArgumentException("终端不存在: " + terminalId);
        }
    }

    @Override
    public void unbindFromRoom(String terminalId, String cstmId) {
        Optional<Terminal> terminalOpt = findByTerminalId(terminalId, cstmId);
        if (terminalOpt.isPresent()) {
            Terminal terminal = terminalOpt.get();
            String oldRoomId = terminal.getRoomId();
            terminal.setRoomId(null);
            terminalRepository.save(terminal);
            
            log.info("终端解绑房间: 终端ID={}, 原房间ID={}", terminalId, oldRoomId);
        } else {
            log.warn("终端不存在，无法解绑房间: 终端ID={}", terminalId);
            throw new IllegalArgumentException("终端不存在: " + terminalId);
        }
    }

    @Override
    public void updateOnlineState(String terminalId, Terminal.OnlineState onlineState, String cstmId) {
        Optional<Terminal> terminalOpt = findByTerminalId(terminalId, cstmId);
        if (terminalOpt.isPresent()) {
            Terminal terminal = terminalOpt.get();
            Terminal.OnlineState oldState = terminal.getOnlineState();
            terminal.setOnlineState(onlineState);
            terminal.setLastAliveTime(new Date());
            
            if (onlineState == Terminal.OnlineState.ONLINE) {
                terminal.setLogonTime(new Date());
            } else if (onlineState == Terminal.OnlineState.OFFLINE) {
                terminal.setLogoffTime(new Date());
            }
            
            terminalRepository.save(terminal);
            
            log.info("终端状态更新: 终端ID={}, 原状态={}, 新状态={}", 
                terminalId, oldState, onlineState);
        } else {
            log.warn("终端不存在，无法更新状态: 终端ID={}", terminalId);
            throw new IllegalArgumentException("终端不存在: " + terminalId);
        }
    }

    @Override
    public void terminalOnline(String terminalId, String ipaddr, String cstmId) {
        Optional<Terminal> terminalOpt = findByTerminalId(terminalId, cstmId);
        if (terminalOpt.isPresent()) {
            Terminal terminal = terminalOpt.get();
            terminal.setOnlineState(Terminal.OnlineState.ONLINE);
            terminal.setLogonTime(new Date());
            terminal.setLastAliveTime(new Date());
            
            if (StringUtils.hasText(ipaddr)) {
                terminal.setIpaddr(ipaddr);
            }
            
            terminalRepository.save(terminal);
            
            log.info("终端上线: 终端ID={}, IP地址={}", terminalId, ipaddr);
        } else {
            log.warn("终端不存在，无法上线: 终端ID={}", terminalId);
            throw new IllegalArgumentException("终端不存在: " + terminalId);
        }
    }

    @Override
    public void terminalOffline(String terminalId, String cstmId) {
        Optional<Terminal> terminalOpt = findByTerminalId(terminalId, cstmId);
        if (terminalOpt.isPresent()) {
            Terminal terminal = terminalOpt.get();
            terminal.setOnlineState(Terminal.OnlineState.OFFLINE);
            terminal.setLogoffTime(new Date());
            
            terminalRepository.save(terminal);
            
            log.info("终端下线: 终端ID={}", terminalId);
        } else {
            log.warn("终端不存在，无法下线: 终端ID={}", terminalId);
            throw new IllegalArgumentException("终端不存在: " + terminalId);
        }
    }

    @Override
    public void updateLastAliveTime(String terminalId, String cstmId) {
        Optional<Terminal> terminalOpt = findByTerminalId(terminalId, cstmId);
        if (terminalOpt.isPresent()) {
            Terminal terminal = terminalOpt.get();
            terminal.setLastAliveTime(new Date());
            terminalRepository.save(terminal);
            
            log.debug("更新终端存活时间: 终端ID={}", terminalId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TerminalStatistics getStatistics(String cstmId) {
        long totalCount = terminalRepository.countByCstmId(cstmId);
        long onlineCount = terminalRepository.countOnlineTerminals(cstmId);
        long offlineCount = terminalRepository.countOfflineTerminals(cstmId);
        
        List<Terminal> boundTerminals = terminalRepository.findBoundTerminals(cstmId);
        List<Terminal> unboundTerminals = terminalRepository.findUnboundTerminals(cstmId);
        
        long boundCount = boundTerminals.size();
        long unboundCount = unboundTerminals.size();
        
        log.debug("终端统计: 客户域={}, 总数={}, 在线={}, 离线={}, 已绑定={}, 未绑定={}", 
            cstmId, totalCount, onlineCount, offlineCount, boundCount, unboundCount);
        
        return new TerminalStatistics(totalCount, onlineCount, offlineCount, boundCount, unboundCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Terminal> findUnboundTerminals(String cstmId) {
        return terminalRepository.findUnboundTerminals(cstmId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Terminal> findBoundTerminals(String cstmId) {
        return terminalRepository.findBoundTerminals(cstmId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTerminalId(String terminalId, String cstmId) {
        return findByTerminalId(terminalId, cstmId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIpAddress(String ipaddr, String cstmId) {
        return terminalRepository.findByIpaddrAndCstmId(ipaddr, cstmId).isPresent();
    }
}