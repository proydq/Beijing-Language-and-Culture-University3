<template>
  <div class="dashboard-content">
    <DashboardStats 
      :stats="stats"
      @time-range-change="handleTimeRangeChange"
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
    }
  },
  emits: ['time-range-change'],
  setup(props, { emit }) {
    const handleTimeRangeChange = (timeRange) => {
      emit('time-range-change', timeRange)
    }

    return {
      handleTimeRangeChange
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