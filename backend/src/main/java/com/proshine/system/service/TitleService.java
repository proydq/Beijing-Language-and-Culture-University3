package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.SearchTitleDTO;
import com.proshine.system.dto.TitleVo;
import com.proshine.system.entity.SysTitle;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Title service interface.
 */
public interface TitleService {

    /**
     * Search titles.
     */
    ResponsePageDataEntity<TitleVo> search(SearchTitleDTO condition);

    /**
     * Create a new title.
     */
    SysTitle create(SysTitle title);

    /**
     * Update title by id.
     */
    SysTitle update(String id, SysTitle title);

    /**
     * Delete title.
     */
    void delete(String id);

    /**
     * Export excel.
     */
    void exportExcel(HttpServletResponse response);

    /**
     * Import excel.
     */
    void importExcel(MultipartFile file);
}
