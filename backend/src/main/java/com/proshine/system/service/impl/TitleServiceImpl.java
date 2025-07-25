package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.SearchTitleDTO;
import com.proshine.system.dto.TitleVo;
import com.proshine.system.entity.SysTitle;
import com.proshine.system.repository.SysTitleRepository;
import com.proshine.system.service.TitleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Implementation of TitleService.
 */
@Service
@Slf4j
public class TitleServiceImpl implements TitleService {

    @Autowired
    private SysTitleRepository titleRepository;

    @Override
    public ResponsePageDataEntity<TitleVo> search(SearchTitleDTO condition) {
        Specification<SysTitle> spec = (root, query, cb) -> {
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
        Page<SysTitle> page = titleRepository.findAll(spec, pageable);
        List<TitleVo> vos = page.getContent().stream().map(this::toVo).collect(Collectors.toList());
        ResponsePageDataEntity<TitleVo> result = new ResponsePageDataEntity<>();
        result.setTotal(page.getTotalElements());
        result.setRows(vos);
        return result;
    }

    private TitleVo toVo(SysTitle title) {
        TitleVo vo = new TitleVo();
        BeanUtils.copyProperties(title, vo);
        return vo;
    }

    @Override
    @Transactional
    public SysTitle create(SysTitle title) {
        title.setId(null);
        return titleRepository.save(title);
    }

    @Override
    @Transactional
    public SysTitle update(String id, SysTitle title) {
        return titleRepository.findById(id).map(exist -> {
            if (StringUtils.hasText(title.getName())) {
                exist.setName(title.getName());
            }
            if (StringUtils.hasText(title.getDescription())) {
                exist.setDescription(title.getDescription());
            }
            return titleRepository.save(exist);
        }).orElseThrow(() -> new RuntimeException("职称不存在"));
    }

    @Override
    @Transactional
    public void delete(String id) {
        titleRepository.deleteById(id);
    }

    @Override
    public void exportExcel(HttpServletResponse response) {
        List<SysTitle> titles = titleRepository.findAll();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("titles");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("名称");
            header.createCell(1).setCellValue("描述");
            int rowIdx = 1;
            for (SysTitle t : titles) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(t.getName());
                row.createCell(1).setCellValue(t.getDescription());
            }
            String fileName = URLEncoder.encode("titles.xlsx", "UTF-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出职称列表失败", e);
            throw new RuntimeException("导出职称列表失败：" + e.getMessage());
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
                SysTitle title = new SysTitle();
                title.setName(name);
                title.setDescription(desc);
                titleRepository.save(title);
            }
        } catch (IOException e) {
            log.error("导入职称数据失败", e);
            throw new RuntimeException("导入职称数据失败：" + e.getMessage());
        }
    }
}
