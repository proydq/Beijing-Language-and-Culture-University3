-- 为测试用户添加头像数据
UPDATE sys_user 
SET avatar_url = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
    email = 'testuser@example.com'
WHERE username = 'testuser' AND customer_id = 'demo';

-- 验证更新结果
SELECT id, username, real_name, avatar_url, email 
FROM sys_user 
WHERE username = 'testuser' AND customer_id = 'demo';