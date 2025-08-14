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

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 终端服务实现类
 */
@Slf4j
@Service
public class TerminalServiceImpl implements TerminalService {
    
    @Autowired
    private TerminalRepository terminalRepository;
    
    @Override
    @Transactional
    public Terminal save(Terminal terminal) {
        if (terminal.getCreateTime() == null) {
            terminal.setCreateTime(new Date());
        }
        return terminalRepository.save(terminal);
    }
    
    @Override
    public Optional<Terminal> findById(String id, String cstmId) {
        Terminal.TerminalId terminalId = new Terminal.TerminalId();
        terminalId.setId(id);
        terminalId.setCstmId(cstmId);
        return terminalRepository.findById(terminalId);
    }
    
    @Override
    public Optional<Terminal> findByCstmIdAndTerminalId(String cstmId, String terminalId) {
        return terminalRepository.findByCstmIdAndTerminalId(cstmId, terminalId);
    }
    
    @Override
    public List<Terminal> findByCstmIdAndExtraId2(String cstmId,String extraId2) {
        return terminalRepository.findByCstmIdAndExtraId2(cstmId,extraId2);
    }
    
    @Override
    public Page<Terminal> findByCstmId(String cstmId, Pageable pageable) {
        return terminalRepository.findByCstmId(cstmId, pageable);
    }

    @Override
    public List<Terminal> findByCstmId(String cstmId) {
        return terminalRepository.findByCstmId(cstmId);
    }

    @Override
    public List<Terminal> findByTerminalNameLike(String cstmId, String terminalName) {
        return terminalRepository.findByCstmIdAndTerminalNameLike(cstmId, terminalName);
    }
    
    @Override
    public List<Terminal> findByOnlineState(String cstmId, Integer onlineState) {
        return terminalRepository.findByCstmIdAndOnlineState(cstmId, onlineState);
    }
    
    @Override
    public List<Terminal> findByClassroomName(String cstmId, String classroomName) {
        return terminalRepository.findByCstmIdAndClassroomName(cstmId, classroomName);
    }
    
    @Override
    public Long countOnlineTerminals(String cstmId) {
        return terminalRepository.countOnlineTerminalsByCstmId(cstmId);
    }
    
    @Override
    @Transactional
    public boolean updateOnlineState(String id, String cstmId, Integer onlineState) {
        try {
            int updated = terminalRepository.updateOnlineState(id, cstmId, onlineState, new Date());
            return updated > 0;
        } catch (Exception e) {
            log.error("更新终端在线状态失败: id={}, cstmId={}, onlineState={}", id, cstmId, onlineState, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean terminalLogon(String id, String cstmId) {
        try {
            int updated = terminalRepository.updateLogonTime(id, cstmId, new Date());
            if (updated > 0) {
                log.info("终端登录成功: id={}, cstmId={}", id, cstmId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("终端登录失败: id={}, cstmId={}", id, cstmId, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean terminalLogoff(String id, String cstmId) {
        try {
            int updated = terminalRepository.updateLogoffTime(id, cstmId, new Date());
            if (updated > 0) {
                log.info("终端登出成功: id={}, cstmId={}", id, cstmId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("终端登出失败: id={}, cstmId={}", id, cstmId, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean delete(String id, String cstmId) {
        try {
            Terminal.TerminalId terminalId = new Terminal.TerminalId();
            terminalId.setId(id);
            terminalId.setCstmId(cstmId);
            
            if (terminalRepository.existsById(terminalId)) {
                terminalRepository.deleteById(terminalId);
                log.info("删除终端成功: id={}, cstmId={}", id, cstmId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除终端失败: id={}, cstmId={}", id, cstmId, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public List<Terminal> saveAll(List<Terminal> terminals) {
        // 设置创建时间
        terminals.forEach(terminal -> {
            if (terminal.getCreateTime() == null) {
                terminal.setCreateTime(new Date());
            }
        });
        return terminalRepository.saveAll(terminals);
    }
    
    @Override
    public boolean existsTerminalId(String cstmId, String terminalId) {
        return terminalRepository.existsByCstmIdAndTerminalId(cstmId, terminalId);
    }
    
    @Override
    @Transactional
    public Terminal createTerminal(Terminal terminal) {
        // 如果没有设置ID，生成一个
        if (terminal.getId() == null || terminal.getId().isEmpty()) {
            terminal.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        
        // 检查终端ID是否已存在
        if (terminal.getTerminalId() != null && 
            existsTerminalId(terminal.getCstmId(), terminal.getTerminalId())) {
            throw new RuntimeException("终端ID已存在: " + terminal.getTerminalId());
        }
        
        // 设置默认值
        if (terminal.getOnlineState() == null) {
            terminal.setOnlineState(0); // 默认离线
        }
        if (terminal.getCreateTime() == null) {
            terminal.setCreateTime(new Date());
        }
        
        Terminal saved = terminalRepository.save(terminal);
        log.info("创建终端成功: id={}, terminalId={}, terminalName={}", 
                saved.getId(), saved.getTerminalId(), saved.getTerminalName());
        
        return saved;
    }
    
    @Override
    @Transactional
    public Terminal updateTerminal(Terminal terminal) {
        // 检查终端是否存在
        Terminal.TerminalId terminalId = new Terminal.TerminalId();
        terminalId.setId(terminal.getId());
        terminalId.setCstmId(terminal.getCstmId());
        
        Optional<Terminal> existingOpt = terminalRepository.findById(terminalId);
        if (!existingOpt.isPresent()) {
            throw new RuntimeException("终端不存在: id=" + terminal.getId());
        }
        
        Terminal existing = existingOpt.get();
        
        // 如果修改了terminalId，检查是否重复
        if (terminal.getTerminalId() != null && 
            !terminal.getTerminalId().equals(existing.getTerminalId()) &&
            existsTerminalId(terminal.getCstmId(), terminal.getTerminalId())) {
            throw new RuntimeException("终端ID已存在: " + terminal.getTerminalId());
        }
        
        // 保留创建时间
        terminal.setCreateTime(existing.getCreateTime());
        
        Terminal saved = terminalRepository.save(terminal);
        log.info("更新终端成功: id={}, terminalId={}, terminalName={}", 
                saved.getId(), saved.getTerminalId(), saved.getTerminalName());
        
        return saved;
    }
}