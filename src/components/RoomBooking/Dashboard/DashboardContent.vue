<template>
  <div class="dashboard-content">
    <DashboardStats 
      :stats="stats"
      :distribution-data="distributionData"
      :trend-data="trendData"
      :loading="loading"
      @time-range-change="handleTimeRangeChange"
      @custom-range-change="handleCustomRangeChange"
    />
  </div>
</template>

<script>
import DashboardStats from './DashboardStats.vue'

export default {
  name: 'DashboardContent',
  components: {
    DashboardStats
  },
  props: {
    stats: {
      type: Object,
      default: () => ({
        totalBookings: 0,
        teacherBookings: 0,
        studentBookings: 0
      })
    },
    distributionData: {
      type: Array,
      default: () => []
    },
    trendData: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['time-range-change', 'custom-range-change'],
  setup(props, { emit }) {
    const handleTimeRangeChange = (timeRange) => {
      emit('time-range-change', timeRange)
    }

    const handleCustomRangeChange = (startDate, endDate) => {
      emit('custom-range-change', startDate, endDate)
    }

    return {
      handleTimeRangeChange,
      handleCustomRangeChange
    }
  }
}
</script>

<style scoped>
.dashboard-content {
  padding: 20px;
  background: #f9f9f9;
  min-height: calc(100vh - 120px);
}
</style>