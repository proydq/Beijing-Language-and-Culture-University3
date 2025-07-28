package com.proshine.system.user.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.entity.SysUser;
import com.proshine.system.user.dto.SearchUserCondition;
import com.proshine.system.user.dto.UserSaveRequest;
import com.proshine.system.user.dto.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface UserService {
    ResponsePageDataEntity<UserVO> search(SearchUserCondition condition);

    SysUser findById(String id);

    UserVO create(UserSaveRequest request);

    UserVO update(String id, UserSaveRequest request);

    void delete(String id);

    void exportExcel(SearchUserCondition condition, HttpServletResponse response);

    void importExcel(MultipartFile file);
}
