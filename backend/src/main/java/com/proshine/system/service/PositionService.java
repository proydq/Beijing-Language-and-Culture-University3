package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.SearchPositionDTO;
import com.proshine.system.entity.SysPosition;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Position service interface.
 */
public interface PositionService {

    /**
     * Search positions with pagination.
     */
    ResponsePageDataEntity<SysPosition> search(SearchPositionDTO condition);

    /**
     * Create new position.
     */
    SysPosition create(SysPosition position);

    /**
     * Update existing position.
     */
    SysPosition update(String id, SysPosition position);

    /**
     * Delete position by id.
     */
    void delete(String id);

    /**
     * Export position list to Excel.
     */
    void exportExcel(HttpServletResponse response);

    /**
     * Import position data from Excel.
     */
    void importExcel(MultipartFile file);
}
