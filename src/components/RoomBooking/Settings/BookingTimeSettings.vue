<template>
  <div class="setting-section">
    <div class="section-header">
      <div>
        <h2>预约时间设置</h2>
        <p>配置预约时间段和时间限制</p>
      </div>
    </div>

    <!-- 时间设置标签页 -->
    <div class="time-settings-tabs">
      <div 
        :class="['tab-item', { active: activeTimeTab === 'workday' }]"
        @click="activeTimeTab = 'workday'"
      >
        工作日设置
      </div>
      <div 
        :class="['tab-item', { active: activeTimeTab === 'weekend' }]"
        @click="activeTimeTab = 'weekend'"
      >
        周末设置
      </div>
      <div 
        :class="['tab-item', { active: activeTimeTab === 'holiday' }]"
        @click="activeTimeTab = 'holiday'"
      >
        节假日设置
      </div>
    </div>

    <!-- 时间设置表单 -->
    <div class="setting-form">
      <el-form label-width="120px">
        <el-form-item label="开放时间">
          <el-time-picker
            v-model="timeRange"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            placeholder="选择时间范围"
          />
          <span class="form-tip">设置该时间段内可进行预约</span>
        </el-form-item>

        <el-form-item label="提前预约">
          <el-input-number
            v-model="advanceBookingDays"
            :min="1"
            :max="30"
            controls-position="right"
          />
          <span class="form-tip">天（最多可提前预约的天数）</span>
        </el-form-item>

        <el-form-item label="最晚取消">
          <el-input-number
            v-model="cancelHours"
            :min="1"
            :max="72"
            controls-position="right"
          />
          <span class="form-tip">小时（预约开始前多少小时内不可取消）</span>
        </el-form-item>

        <el-form-item label="节次设置">
          <el-select v-model="periodType" placeholder="请选择节次类型">
            <el-option label="标准8节次" value="standard8" />
            <el-option label="标准10节次" value="standard10" />
            <el-option label="自定义节次" value="custom" />
          </el-select>
          <span class="form-tip">选择预约时间的节次划分方式</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="saveTimeSettings">保存设置</el-button>
          <el-button @click="resetTimeSettings">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'BookingTimeSettings',
  emits: ['save-settings'],
  setup(props, { emit }) {
    const activeTimeTab = ref('workday')
    const timeRange = ref([new Date(2000, 1, 1, 8, 0, 0), new Date(2000, 1, 1, 18, 0, 0)])
    const advanceBookingDays = ref(7)
    const cancelHours = ref(2)
    const periodType = ref('standard8')

    const saveTimeSettings = () => {
      const settingsData = {
        activeTimeTab: activeTimeTab.value,
        timeRange: timeRange.value,
        advanceBookingDays: advanceBookingDays.value,
        cancelHours: cancelHours.value,
        periodType: periodType.value
      }
      
      emit('save-settings', { type: 'booking_time_settings', data: settingsData })
      ElMessage.success('预约时间设置保存成功')
    }

    const resetTimeSettings = () => {
      timeRange.value = [new Date(2000, 1, 1, 8, 0, 0), new Date(2000, 1, 1, 18, 0, 0)]
      advanceBookingDays.value = 7
      cancelHours.value = 2
      periodType.value = 'standard8'
      ElMessage.info('预约时间设置已重置')
    }

    return {
      activeTimeTab,
      timeRange,
      advanceBookingDays,
      cancelHours,
      periodType,
      saveTimeSettings,
      resetTimeSettings
    }
  }
}
</script>

<style scoped>
.setting-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
  font-weight: 600;
}

.section-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.setting-form {
  max-width: 600px;
}

.form-tip {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}

.time-settings-tabs {
  display: flex;
  margin-bottom: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.tab-item {
  padding: 12px 24px;
  cursor: pointer;
  background: white;
  border: 1px solid #e8e8e8;
  border-bottom: none;
  color: #666;
  font-size: 14px;
  transition: all 0.3s;
}

.tab-item:first-child {
  border-top-left-radius: 8px;
}

.tab-item:last-child {
  border-top-right-radius: 8px;
}

.tab-item.active {
  background: #4A90E2;
  color: white;
  border-color: #4A90E2;
}
</style>