-- 1) 扩展 sys_permission：引入 API 类型（3），保留 MENU(1)、BUTTON(2)=page-button 语义
ALTER TABLE sys_permission
  ADD COLUMN IF NOT EXISTS visible TINYINT DEFAULT 1,
  ADD COLUMN IF NOT EXISTS icon VARCHAR(64),
  ADD COLUMN IF NOT EXISTS path VARCHAR(200),
  ADD COLUMN IF NOT EXISTS component VARCHAR(200),
  ADD COLUMN IF NOT EXISTS type TINYINT NOT NULL COMMENT '1=MENU,2=BUTTON(page-button),3=API';

-- 2) 页面/按钮 ⇄ 接口 绑定关系表
CREATE TABLE IF NOT EXISTS sys_web_page_interface_rel (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  web_page_resource_id BIGINT NOT NULL COMMENT '页面或按钮资源ID（type=1/2）',
  interface_resource_id BIGINT NOT NULL COMMENT '接口资源ID（type=3）',
  UNIQUE KEY uk_bind (web_page_resource_id, interface_resource_id),
  KEY idx_if (interface_resource_id)
);

-- 3) URL 权限元数据（若已有 sys_permission_api 可跳过）
CREATE TABLE IF NOT EXISTS sys_permission_api (
  id          VARCHAR(36) PRIMARY KEY,
  http_method VARCHAR(10) NOT NULL,
  ant_pattern VARCHAR(200) NOT NULL,
  perm_code   VARCHAR(100) NOT NULL,
  sort        INT DEFAULT 0,
  UNIQUE KEY uk_method_pattern (http_method, ant_pattern),
  KEY idx_perm_code (perm_code)
);

-- 4) 索引建议
ALTER TABLE sys_role_permission
  ADD KEY IF NOT EXISTS idx_role (role_id),
  ADD KEY IF NOT EXISTS idx_perm (permission_id);

-- 5) 示例：注册一个接口资源（删除实验室）与一个按钮资源，并绑定关系
INSERT IGNORE INTO sys_permission (id, perm_code, name, type, visible) VALUES
  (NULL,'interface:laboratory:delete','删除实验室接口',3,1),
  (NULL,'page-button:laboratory:delete','删除实验室按钮',2,1);

INSERT IGNORE INTO sys_web_page_interface_rel (web_page_resource_id, interface_resource_id)
SELECT btn.id, api.id
  FROM sys_permission btn, sys_permission api
 WHERE btn.perm_code='page-button:laboratory:delete'
   AND api.perm_code='interface:laboratory:delete';

-- 6) 把接口 URL 挂上元数据（示例）
INSERT IGNORE INTO sys_permission_api (id,http_method,ant_pattern,perm_code,sort)
VALUES (UUID(),'POST','/api/laboratory/delete','interface:laboratory:delete',10);
