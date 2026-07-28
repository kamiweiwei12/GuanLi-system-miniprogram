<template>
  <div class="dashboard-container">
    <!-- 概览卡片 -->
    <el-row :gutter="20" class="overview-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-label">总巡检数量</div>
          <div class="stat-value">{{ stats.totalRecords }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="card-success">
          <div class="stat-label">正常</div>
          <div class="stat-value">{{ stats.byResult.normal }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="card-danger">
          <div class="stat-label">故障</div>
          <div class="stat-value">{{ stats.byResult.fault }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="card-warning">
          <div class="stat-label">隐患</div>
          <div class="stat-value">{{ stats.byResult.hidden }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表行 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <div ref="trendChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div ref="deptChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 部门统计表 -->
    <el-card shadow="hover" style="margin-top: 20px;">
      <template #header><span>部门统计排行</span></template>
      <el-table :data="stats.byDepartment" stripe>
        <el-table-column prop="department" label="部门" />
        <el-table-column prop="count" label="巡检数量" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import * as echarts from 'echarts';
import { getStatistics, StatisticsVO } from '@/api/statistics';

const stats = ref<StatisticsVO>({
    totalRecords: 0,
    byResult: { normal: 0, fault: 0, hidden: 0 },
    byDepartment: [],
    byDate: [],
});

const trendChart = ref<HTMLDivElement | null>(null);
const deptChart = ref<HTMLDivElement | null>(null);

onMounted(async () => {
    try {
        const data = await getStatistics();
        stats.value = data;
        await nextTick();
        renderTrendChart();
        renderDeptChart();
    } catch (err) {
        console.error('获取统计数据失败', err);
    }
});

function renderTrendChart() {
    if (!trendChart.value) return;
    const chart = echarts.init(trendChart.value);
    const dates = stats.value.byDate.map(d => d.date).reverse();
    const counts = stats.value.byDate.map(d => d.count).reverse();
    chart.setOption({
        title: { text: '最近 30 天巡检趋势' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: dates },
        yAxis: { type: 'value' },
        series: [{ data: counts, type: 'line', smooth: true }],
    });
}

function renderDeptChart() {
    if (!deptChart.value) return;
    const chart = echarts.init(deptChart.value);
    const depts = stats.value.byDepartment.map(d => d.department);
    const data = stats.value.byDepartment.map(d => ({ name: d.department, value: d.count }));
    chart.setOption({
        title: { text: '部门统计占比' },
        tooltip: { trigger: 'item' },
        series: [{ type: 'pie', radius: '60%', data }],
    });
}
</script>

<style scoped>
.dashboard-container { padding: 0; }
.overview-row { margin-bottom: 20px; }
.stat-label { font-size: 14px; color: #909399; }
.stat-value { font-size: 32px; font-weight: 700; margin-top: 8px; }
.card-success .stat-value { color: #67c23a; }
.card-danger .stat-value { color: #f56c6c; }
.card-warning .stat-value { color: #e6a23c; }
</style>