// 这个脚本用于修复项目中硬编码的 /api 路径问题
// 执行方式: node fix-api-paths.js

const fs = require('fs');
const path = require('path');

// 需要处理的文件列表
const filesToFix = [
  'src/composables/useUserManagement.js',
  'src/composables/usePositionManagement.js', 
  'src/composables/useTitleManagement.js',
  'src/api/roomSchedule.js',
  'src/api/roomBookingManagement.js',
  'src/api/approvalManagement.js',
  'src/components/system/PermissionUrl.vue',
  'src/components/system/PermissionAlias.vue',
  'src/views/system/PermissionDemo.vue',
  'src/views/system/permission/components/PermissionAlias.vue'
];

// 路径映射关系
const pathMappings = {
  '/api/room': 'API_PATHS.ROOM',
  '/api/room-booking': 'API_PATHS.ROOM_BOOKING',
  '/api/room-schedule': 'API_PATHS.ROOM_SCHEDULE',
  '/api/user': 'API_PATHS.USER',
  '/api/organization': 'API_PATHS.ORGANIZATION',
  '/api/position': 'API_PATHS.POSITION',
  '/api/title': 'API_PATHS.TITLE',
  '/api/upload': 'API_PATHS.UPLOAD',
  '/api/permission': 'API_PATHS.PERMISSION'
};

// 修复文件
function fixFile(filePath) {
  console.log(`\nProcessing: ${filePath}`);
  
  let content = fs.readFileSync(filePath, 'utf8');
  let modified = false;
  
  // 1. 对于所有文件，移除硬编码的 /api 前缀
  const apiPattern = /request\.(get|post|put|delete|patch)\s*\(\s*['"`]\/api\//g;
  if (apiPattern.test(content)) {
    content = content.replace(apiPattern, 'request.$1(`/');
    modified = true;
    console.log('  ✓ Removed /api prefix');
  }
  
  // 2. 对于 API 文件，确保导入了 API_PATHS
  if (filePath.includes('src/api/') && !content.includes('import { API_PATHS }')) {
    const importPattern = /^import .* from ['"]@\/utils\/request['"]/m;
    if (importPattern.test(content)) {
      content = content.replace(
        importPattern,
        `$&\nimport { API_PATHS } from './apiConfig'`
      );
      modified = true;
      console.log('  ✓ Added API_PATHS import');
    }
  }
  
  // 3. 替换路径为 API_PATHS 常量（针对 API 文件）
  if (filePath.includes('src/api/')) {
    Object.entries(pathMappings).forEach(([oldPath, newPath]) => {
      const regex = new RegExp(`['"\`]${oldPath.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}`, 'g');
      if (regex.test(content)) {
        content = content.replace(regex, '${' + newPath + '}');
        // 修复模板字符串
        content = content.replace(/'\$\{([^}]+)\}'/g, '`${$1}`');
        content = content.replace(/"\$\{([^}]+)\}"/g, '`${$1}`');
        modified = true;
        console.log(`  ✓ Replaced ${oldPath} with ${newPath}`);
      }
    });
  }
  
  if (modified) {
    fs.writeFileSync(filePath, content, 'utf8');
    console.log('  ✓ File updated');
  } else {
    console.log('  - No changes needed');
  }
}

// 执行修复
console.log('Starting API path fixes...\n');

filesToFix.forEach(file => {
  const fullPath = path.join(__dirname, file);
  if (fs.existsSync(fullPath)) {
    fixFile(fullPath);
  } else {
    console.log(`\n⚠️  File not found: ${file}`);
  }
});

console.log('\n✅ API path fixes completed!');
console.log('\nNext steps:');
console.log('1. Run "npm run build" to rebuild the project');
console.log('2. Deploy the new build to nginx');
console.log('3. Test all API endpoints');