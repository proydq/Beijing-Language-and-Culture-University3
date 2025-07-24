<template>
  <div class="dashboard">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo">
          <el-icon size="24"><home-filled /></el-icon>
        </div>
        <span class="title">首页</span>
      </div>
      <div class="header-right">
        <div class="avatar">
          <el-icon size="20"><user /></el-icon>
        </div>
        <span class="username">张三</span>
        <el-dropdown>
          <span class="dropdown-link">
            中文
            <el-icon class="el-icon--right">
              <arrow-down />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>中文</el-dropdown-item>
              <el-dropdown-item>English</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 基础分类 -->
      <div class="category-section">
        <h3 class="category-title">基础分类</h3>
        <div class="card-grid">
          <div class="function-card active">
            <div class="card-icon">
              <el-icon size="24"><setting /></el-icon>
            </div>
            <div class="card-content">
              <h4>主题管理</h4>
              <p>主要用于主题配置界面的设计方案，实现其自主功能，说明作者及具体工作</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><message /></el-icon>
            </div>
            <div class="card-content">
              <h4>信息发布管理</h4>
              <p>主要用于各类播出式的信息，提供模块信息及其发布的功能</p>
            </div>
          </div>
          <div class="function-card" @click="handleCardClick('用户信息管理')">
            <div class="card-icon">
              <el-icon size="24"><user /></el-icon>
            </div>
            <div class="card-content">
              <h4>用户信息管理</h4>
              <p>包含用户信息、新开户信息等基础信息配置与储存功能</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><picture /></el-icon>
            </div>
            <div class="card-content">
              <h4>媒体资源管理</h4>
              <p>用于管理文档、图片、录像音频文件及存储管理</p>
            </div>
          </div>
          <div class="function-card" @click="handleCardClick('房屋信息管理')">
            <div class="card-icon">
              <el-icon size="24"><office-building /></el-icon>
            </div>
            <div class="card-content">
              <h4>房屋信息管理</h4>
              <p>包含房间信息管理基础配置及的管理</p>
            </div>
          </div>
          <!-- 新增房屋(教室)借用模块 -->
          <div class="function-card" @click="handleCardClick('房屋借用管理')">
            <div class="card-icon">
              <el-icon size="24"><school /></el-icon>
            </div>
            <div class="card-content">
              <h4>房屋借用管理</h4>
              <p>主要用于房屋（教室）的借用申请、审批和统计管理</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 控制分类 -->
      <div class="category-section">
        <h3 class="category-title">控制分类</h3>
        <div class="card-grid">
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><monitor /></el-icon>
            </div>
            <div class="card-content">
              <h4>终端管理</h4>
              <p>对于终端的管理，包含计载、运件计划、基础设备及管理与监控</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><key /></el-icon>
            </div>
            <div class="card-content">
              <h4>门禁管理</h4>
              <p>对门锁的教师管理，支持通过各级授权人员进场管理及网关门控</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><video-camera /></el-icon>
            </div>
            <div class="card-content">
              <h4>摄像头管理</h4>
              <p>主要用于管理摄像头的添加设置</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><user /></el-icon>
            </div>
            <div class="card-content">
              <h4>可视对讲管理</h4>
              <p>主要需要远程开启视频的对讲功能管理</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><cpu /></el-icon>
            </div>
            <div class="card-content">
              <h4>中控管理</h4>
              <p>主要用于管理可控制领域的管理中枢的应答开发</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 业务分类 -->
      <div class="category-section">
        <h3 class="category-title">业务分类</h3>
        <div class="card-grid">
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><calendar /></el-icon>
            </div>
            <div class="card-content">
              <h4>会议管理</h4>
              <p>主要用于会议室的预约，会议管理功能</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><user /></el-icon>
            </div>
            <div class="card-content">
              <h4>访客管理</h4>
              <p>主要用于来访人员的访问管理、提交登记功能</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><data-line /></el-icon>
            </div>
            <div class="card-content">
              <h4>图书馆管理</h4>
              <p>主要用于管理书籍的预约情况下、库存管理、订购管理</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><odometer /></el-icon>
            </div>
            <div class="card-content">
              <h4>实验室管理</h4>
              <p>主要用于实验室的预约，设备、安全检查及实验器管理</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><graduation-cap /></el-icon>
            </div>
            <div class="card-content">
              <h4>行政效管理</h4>
              <p>主要用于管理效果的应达到情况、学生能效对象、调整松紧达到功能</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><document /></el-icon>
            </div>
            <div class="card-content">
              <h4>标识管理</h4>
              <p>主要用于宾客道路场大队的管理及宾客信息下发</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><position /></el-icon>
            </div>
            <div class="card-content">
              <h4>人员去向管理</h4>
              <p>能在提出人员友商模块设备行开标实施管理，系统会以出席人员去向</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><user /></el-icon>
            </div>
            <div class="card-content">
              <h4>家校互通管理</h4>
              <p>主要用于家长和学校的沟通管理</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><timer /></el-icon>
            </div>
            <div class="card-content">
              <h4>电子时钟管理</h4>
              <p>主要用于主持时钟功能。升杯、音频主动功能的管理</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><edit /></el-icon>
            </div>
            <div class="card-content">
              <h4>考务管理</h4>
              <p>主要用于考试信息及考场安排</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><school /></el-icon>
            </div>
            <div class="card-content">
              <h4>空间教室维保</h4>
              <p>主要用于管理分析室内设备的内部保养及的各类管理</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><office-building /></el-icon>
            </div>
            <div class="card-content">
              <h4>房屋信用管理</h4>
              <p></p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><tools /></el-icon>
            </div>
            <div class="card-content">
              <h4>报修管理</h4>
              <p>包含已维修管理、数案等各项设备及工单中意见</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><operation /></el-icon>
            </div>
            <div class="card-content">
              <h4>保洁管理</h4>
              <p>主要用于清洁卫生列目标的实际安全管理</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 统计分类 -->
      <div class="category-section">
        <h3 class="category-title">统计分类</h3>
        <div class="card-grid">
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><data-line /></el-icon>
            </div>
            <div class="card-content">
              <h4>终端统计</h4>
              <p>实施监控后对于终端的数据性进行总监，支持导出功能</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><data-line /></el-icon>
            </div>
            <div class="card-content">
              <h4>环境统计</h4>
              <p>对环境监控的定时上报数据进行统计概览的支持导出功能</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><data-line /></el-icon>
            </div>
            <div class="card-content">
              <h4>预约统计</h4>
              <p>定对监控的定时上报数据进行的统计概览、支持导出功能</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><data-line /></el-icon>
            </div>
            <div class="card-content">
              <h4>用户画像统计</h4>
              <p>对监控的定时上报数据进行统计概览、支持导出功能</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><data-line /></el-icon>
            </div>
            <div class="card-content">
              <h4>访客统计</h4>
              <p>实施监控后对于终端的数据性进行总监，支持导出功能</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><data-line /></el-icon>
            </div>
            <div class="card-content">
              <h4>门禁统计</h4>
              <p>记录对于的定时上报数据进行的行为访问、支持导出功能</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><data-line /></el-icon>
            </div>
            <div class="card-content">
              <h4>日志统计</h4>
              <p>记录日志的管理</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 权限分类 -->
      <div class="category-section">
        <h3 class="category-title">权限分类</h3>
        <div class="card-grid">
          <div class="function-card" @click="handleCardClick('权限管理')">
            <div class="card-icon">
              <el-icon size="24"><user /></el-icon>
            </div>
            <div class="card-content">
              <h4>权限管理</h4>
              <p>主要是对用户的权限、角色案列的管理</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><grid /></el-icon>
            </div>
            <div class="card-content">
              <h4>控件管理</h4>
              <p>控件、软件器管理，系列等进行模块配置管理</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><connection /></el-icon>
            </div>
            <div class="card-content">
              <h4>系统流活管理</h4>
              <p>系统流活的功能配置</p>
            </div>
          </div>
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><grid /></el-icon>
            </div>
            <div class="card-content">
              <h4>体验管理</h4>
              <p>主办体验使用管理数据、客进线数据的、对干操作检管</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 部署实施运维分类 -->
      <div class="category-section">
        <h3 class="category-title">部署实施运维分类</h3>
        <div class="card-grid">
          <div class="function-card">
            <div class="card-icon">
              <el-icon size="24"><connection /></el-icon>
            </div>
            <div class="card-content">
              <h4>终端网络配置</h4>
              <p>主要用于对各设备网络控制的配置</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'Dashboard',
  setup() {
    const router = useRouter()

    const handleCardClick = (cardName) => {
      switch(cardName) {
        case '用户信息管理':
          router.push('/user-management')
          break
        case '房屋信息管理':
          router.push('/house-management')
          break
        case '权限管理':
          router.push('/role-management')
          break
        case '房屋借用管理':
          router.push('/room-booking')
          break
        // 可以添加更多卡片的跳转逻辑
        default:
          console.log('点击了:', cardName)
      }
    }

    return {
      handleCardClick
    }
  }
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: white;
  color: #4A90E2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title {
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.username {
  font-size: 16px;
  font-weight: 500;
}

.dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.dropdown-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.main-content {
  padding: 30px;
  max-width: 1400px;
  margin: 0 auto;
}

.category-section {
  margin-bottom: 40px;
}

.category-title {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 4px solid #4A90E2;
  font-weight: 600;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.function-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 2px solid transparent;
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.function-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border-color: #4A90E2;
}

.function-card.active {
  background: #FF8C42;
  color: white;
}

.function-card.active .card-icon {
  color: white;
}

.card-icon {
  flex-shrink: 0;
  width: 50px;
  height: 50px;
  background: #f8f9ff;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4A90E2;
}

.function-card.active .card-icon {
  background: rgba(255, 255, 255, 0.2);
}

.card-content h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.function-card.active .card-content h4 {
  color: white;
}

.card-content p {
  margin: 0;
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.function-card.active .card-content p {
  color: rgba(255, 255, 255, 0.9);
}

@media (max-width: 768px) {
  .header {
    padding: 15px 20px;
    flex-direction: column;
    gap: 15px;
  }
  
  .main-content {
    padding: 20px;
  }
  
  .card-grid {
    grid-template-columns: 1fr;
  }
  
  .function-card {
    padding: 15px;
  }
}
</style>