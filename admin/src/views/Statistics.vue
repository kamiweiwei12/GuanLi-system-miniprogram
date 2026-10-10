<template>
  <div class="statistics-container">
    <!-- 筛选条件 -->
    <el-card shadow="hover" class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="loadAll"
          />
        </el-form-item>
        <el-form-item label="区域">
          <el-input v-model="filterForm.area" placeholder="输入区域" clearable @clear="loadAll" style="width: 150px;" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadAll">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 总览卡片 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="stat-label">总巡检数</div>
          <div class="stat-value">{{ overview.totalRecords }}</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="card-success">
          <div class="stat-label">正常</div>
          <div class="stat-value">{{ overview.normalCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="card-danger">
          <div class="stat-label">故障</div>
          <div class="stat-value">{{ overview.faultCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="card-warning">
          <div class="stat-label">隐患</div>
          <div class="stat-value">{{ overview.hiddenCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="card-rate">
          <div class="stat-label">异常率</div>
          <div class="stat-value rate-value">{{ overview.anomalyRate }}%</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图 + 区域排行 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="14">
        <el-card shadow="hover">
          <div ref="trendChart" style="height: 380px;"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="hover">
          <div ref="areaBarChart" style="height: 380px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 设备统计 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>高频巡检设备 TOP10</span></template>
          <div ref="deviceFreqChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>故障设备排行</span></template>
          <el-table :data="deviceStats.faultDevices" stripe size="small" max-height="300">
            <el-table-column prop="deviceName" label="设备" min-width="80" />
            <el-table-column prop="faultCount" label="故障数" width="70" />
            <el-table-column label="故障率" width="80">
              <template #default="{ row }">
                <span style="color: #f56c6c;">{{ row.faultRate }}%</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>风险设备排行</span></template>
          <el-table :data="deviceStats.riskDevices" stripe size="small" max-height="300">
            <el-table-column prop="deviceName" label="设备" min-width="80" />
            <el-table-column prop="hiddenCount" label="隐患数" width="70" />
            <el-table-column label="隐患率" width="80">
              <template #default="{ row }">
                <span style="color: #e6a23c;">{{ row.hiddenRate }}%</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue';
import * as echarts from 'echarts';
import {
  getOverview, getAreaStats, getDeviceStats, getTrendStats,
  OverviewVO, AreaStatVO, DeviceStatVO, TrendStatVO, StatisticsQueryParams,
} from '@/api/statistics';

// 筛选表单
const filterForm = reactive({
  dateRange: null as [string, string] | null,
  area: '',
});

function buildParams(): StatisticsQueryParams {
  const params: StatisticsQueryParams = {};
  if (filterForm.dateRange && filterForm.dateRange.length === 2) {
    params.startDate = filterForm.dateRange[0] + 'T00:00:00';
    params.endDate = filterForm.dateRange[1] + 'T23:59:59';
  }
  if (filterForm.area) {
    params.area = filterForm.area;
  }
  return params;
}

function resetFilter() {
  filterForm.dateRange = null;
  filterForm.area = '';
  loadAll();
}

// 数据
const overview = ref<OverviewVO>({
  totalRecords: 0, normalCount: 0, faultCount: 0, hiddenCount: 0, anomalyRate: 0,
});
const areaStats = ref<AreaStatVO[]>([]);
const trendStats = ref<TrendStatVO[]>([]);
const deviceStats = ref<DeviceStatVO>({
  highFrequencyDevices: [], faultDevices: [], riskDevices: [],
});

// 图表 refs
const trendChart = ref<HTMLDivElement | null>(null);
const areaBarChart = ref<HTMLDivElement | null>(null);
const deviceFreqChart = ref<HTMLDivElement | null>(null);

let trendChartInst: echarts.ECharts | null = null;
let areaBarChartInst: echarts.ECharts | null = null;
let deviceFreqChartInst: echarts.ECharts | null = null;

onMounted(async () => {
  await loadAll();
});

async function loadAll() {
  const params = buildParams();
  try {
    const [ov, area, device, trend] = await Promise.all([
      getOverview(params),
      getAreaStats(params),
      getDeviceStats(params),
      getTrendStats(params),
    ]);
    overview.value = ov;
    areaStats.value = area;
    deviceStats.value = device;
    trendStats.value = trend;
    await nextTick();
    renderCharts();
  } catch (err) {
    console.error('获取统计数据失败', err);
  }
}

function renderCharts() {
  renderTrendChart();
  renderAreaBarChart();
  renderDeviceFreqChart();
}

function renderTrendChart() {
  if (!trendChart.value) return;
  if (!trendChartInst) {
    trendChartInst = echarts.init(trendChart.value);
  }
  // 按日期升序展示
  const data = [...trendStats.value].reverse();
  const dates = data.map(d => d.date);
  trendChartInst.setOption({
    title: { text: '巡检趋势' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['总数', '正常', '故障', '隐患'], bottom: 0 },
    xAxis: { type: 'category', data: dates, axisLabel: { rotate: 45 } },
    yAxis: { type: 'value' },
    series: [
      { name: '总数', data: data.map(d => d.totalCount), type: 'line', smooth: true, lineStyle: { width: 2 } },
      { name: '正常', data: data.map(d => d.normalCount), type: 'line', smooth: true, lineStyle: { color: '#67c23a' } },
      { name: '故障', data: data.map(d => d.faultCount), type: 'line', smooth: true, lineStyle: { color: '#f56c6c' } },
      { name: '隐患', data: data.map(d => d.hiddenCount), type: 'line', smooth: true, lineStyle: { color: '#e6a23c' } },
    ],
    grid: { bottom: 50, top: 50 },
  }, { notMerge: true });
}

function renderAreaBarChart() {
  if (!areaBarChart.value) return;
  if (!areaBarChartInst) {
    areaBarChartInst = echarts.init(areaBarChart.value);
  }
  const areas = areaStats.value.map(a => a.area);
  const rates = areaStats.value.map(a => a.anomalyRate);
  areaBarChartInst.setOption({
    title: { text: '区域异常率排行 (%)' },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: { type: 'category', data: areas, axisLabel: { rotate: 30, interval: 0 } },
    yAxis: { type: 'value', name: '%' },
    series: [{ data: rates, type: 'bar', itemStyle: { color: '#f56c6c' } }],
    grid: { bottom: 80, top: 40 },
  }, { notMerge: true });
}

function renderDeviceFreqChart() {
  if (!deviceFreqChart.value) return;
  if (!deviceFreqChartInst) {
    deviceFreqChartInst = echarts.init(deviceFreqChart.value);
  }
  const devices = deviceStats.value.highFrequencyDevices.map(d => d.deviceName).reverse();
  const counts = deviceStats.value.highFrequencyDevices.map(d => d.count).reverse();
  deviceFreqChartInst.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: devices, axisLabel: { interval: 0 } },
    series: [{ data: counts, type: 'bar', itemStyle: { color: '#409EFF' } }],
    grid: { left: 120, top: 10, bottom: 10, right: 30 },
  }, { notMerge: true });
}
</script>

<style scoped>
.statistics-container { padding: 0; }
.filter-card { margin-bottom: 0; }
.filter-card :deep(.el-card__body) { padding: 16px 20px 4px; }
.stat-label { font-size: 14px; color: #909399; }
.stat-value { font-size: 30px; font-weight: 700; margin-top: 6px; }
.card-success .stat-value { color: #67c23a; }
.card-danger .stat-value { color: #f56c6c; }
.card-warning .stat-value { color: #e6a23c; }
.card-rate .rate-value { color: #409EFF; }
</style>