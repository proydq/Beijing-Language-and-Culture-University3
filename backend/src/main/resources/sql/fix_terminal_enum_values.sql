-- 修复 tb_terminal 表中的枚举值异常问题
-- 将超出枚举范围的值重置为默认值

-- 修复 online_state 字段（只允许 0, 1, 2）
UPDATE tb_terminal 
SET online_state = 0 
WHERE online_state NOT IN (0, 1, 2) OR online_state IS NULL;

-- 修复 entrance_open_status 字段（只允许 0, 1, 2）
UPDATE tb_terminal 
SET entrance_open_status = 0 
WHERE entrance_open_status NOT IN (0, 1, 2);

-- 修复 entrance_type 字段（只允许 0, 1, 2）
UPDATE tb_terminal 
SET entrance_type = 0 
WHERE entrance_type NOT IN (0, 1, 2);

-- 查看修复后的数据
SELECT 
    COUNT(*) as total_count,
    SUM(CASE WHEN online_state NOT IN (0, 1, 2) THEN 1 ELSE 0 END) as invalid_online_state,
    SUM(CASE WHEN entrance_open_status NOT IN (0, 1, 2) THEN 1 ELSE 0 END) as invalid_entrance_open_status,
    SUM(CASE WHEN entrance_type NOT IN (0, 1, 2) THEN 1 ELSE 0 END) as invalid_entrance_type
FROM tb_terminal;