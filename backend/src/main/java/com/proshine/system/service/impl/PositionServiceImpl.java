package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.SearchPositionDTO;
import com.proshine.system.entity.SysPosition;
import com.proshine.system.repository.SysPositionRepository;
import com.proshine.system.service.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of PositionService.
 */
@Service
@Slf4j
public class PositionServiceImpl implements PositionService {

    @Autowired
    private SysPositionRepository positionRepository;

    @Override
    public ResponsePageDataEntity<SysPosition> search(SearchPositionDTO condition) {
        Specification<SysPosition> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(condition.getCustomerId())) {
                predicates.add(cb.equal(root.get("cstmId"), condition.getCustomerId()));
            }
            if (StringUtils.hasText(condition.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + condition.getName() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(condition.getPageNumber() - 1, condition.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SysPosition> page = positionRepository.findAll(spec, pageable);
        ResponsePageDataEntity<SysPosition> result = new ResponsePageDataEntity<>();
        result.setTotal(page.getTotalElements());
        result.setRows(page.getContent());
        return result;
    }

    @Override
    @Transactional
    public SysPosition create(SysPosition position) {
        position.setId(null);
        return positionRepository.save(position);
    }

    @Override
    @Transactional
    public SysPosition update(String id, SysPosition position) {
        Optional<SysPosition> optional = positionRepository.findById(id);
        if (!optional.isPresent()) {
            throw new RuntimeException("职务不存在");
        }
        SysPosition exist = optional.get();
        if (StringUtils.hasText(position.getName())) {
            exist.setName(position.getName());
        }
        if (StringUtils.hasText(position.getDescription())) {
            exist.setDescription(position.getDescription());
        }
        return positionRepository.save(exist);
    }

    @Override
    @Transactional
    public void delete(String id) {
        positionRepository.deleteById(id);
    }

    @Override
    public void exportExcel(HttpServletResponse response) {
        List<SysPosition> positions = positionRepository.findAll();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("positions");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("名称");
            header.createCell(1).setCellValue("描述");
            int rowIdx = 1;
            for (SysPosition p : positions) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getName());
                row.createCell(1).setCellValue(p.getDescription());
            }
            String fileName = URLEncoder.encode("positions.xlsx", "UTF-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出职务列表失败", e);
            throw new RuntimeException("导出职务列表失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void importExcel(MultipartFile file) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();
            for (int i = 1; i < rows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String name = row.getCell(0).getStringCellValue();
                String desc = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null;
                SysPosition position = new SysPosition();
                position.setName(name);
                position.setDescription(desc);
                positionRepository.save(position);
            }
        } catch (IOException e) {
            log.error("导入职务数据失败", e);
            throw new RuntimeException("导入职务数据失败：" + e.getMessage());
        }
    }
}
