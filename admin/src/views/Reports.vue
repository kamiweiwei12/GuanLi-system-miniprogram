<template>
  <div class="reports-container">
    <!-- 筛选条件 -->
    <el-card style="margin-bottom: 16px;">
      <el-form :inline="true" :model="filters">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始"
            end-placeholder="结束"
            value-format="YYYY-MM-DD"
            style="width: 260px;"
          />
        </el-form-item>
        <el-form-item label="区域">
          <el-input v-model="filters.area" placeholder="巡检区域" clearable />
        </el-form-item>
        <el-form-item label="设备">
          <el-input v-model="filters.deviceName" placeholder="设备名称" clearable />
        </el-form-item>
        <el-form-item label="结果">
          <el-select v-model="filters.result" placeholder="全部" clearable style="width: 120px;">
            <el-option label="正常" :value="0" />
            <el-option label="故障" :value="1" />
            <el-option label="隐患" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
          <el-button type="success" @click="exportExcel" :loading="exporting">导出 Excel</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表 -->
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="department" label="部门" width="100" />
        <el-table-column prop="employeeName" label="巡检人" width="100" />
        <el-table-column prop="area" label="区域" width="120" />
        <el-table-column prop="deviceName" label="设备" width="120" />
        <el-table-column prop="inspectionTime" label="巡检时间" width="170" />
        <el-table-column label="结果" width="80">
          <template #default="{ row }">
            <el-tag :type="resultTag(row.result)" size="small">{{ resultText(row.result) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row.id)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end;"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="巡检记录详情" width="700px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="部门">{{ detail.department }}</el-descriptions-item>
          <el-descriptions-item label="巡检人">{{ detail.employeeName }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ detail.phoneNumber }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ detail.area }}</el-descriptions-item>
          <el-descriptions-item label="设备">{{ detail.deviceName }}</el-descriptions-item>
          <el-descriptions-item label="时间">{{ detail.inspectionTime }}</el-descriptions-item>
          <el-descriptions-item label="结果">
            <el-tag :type="resultTag(detail.result)">{{ resultText(detail.result) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ detail.description || '无' }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="detail.imageUrls?.length" style="margin-top: 16px;">
          <el-image
            v-for="(url, idx) in detail.imageUrls"
            :key="idx"
            :src="url"
            style="width: 100px; height: 100px; margin-right: 8px;"
            fit="cover"
            :preview-src-list="detail.imageUrls"
          />
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getReportList, getReportDetail, exportReport, InspectionRecordVO } from '@/api/report';

const list = ref<InspectionRecordVO[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);
const dateRange = ref<[string, string] | null>(null);
const filters = ref({ area: '', deviceName: '', result: null as number | null });

const detailVisible = ref(false);
const detail = ref<InspectionRecordVO | null>(null);
const exporting = ref(false);

onMounted(() => loadData());

async function loadData() {
    loading.value = true;
    try {
        const res = await getReportList({
            pageNum: currentPage.value,
            pageSize: pageSize.value,
            area: filters.value.area || undefined,
            deviceName: filters.value.deviceName || undefined,
            result: filters.value.result ?? undefined,
            startTime: dateRange.value?.[0] ? dateRange.value[0] + 'T00:00:00' : undefined,
            endTime: dateRange.value?.[1] ? dateRange.value[1] + 'T23:59:59' : undefined,
        });
        list.value = res.list;
        total.value = res.total;
    } finally {
        loading.value = false;
    }
}

function search() {
    currentPage.value = 1;
    loadData();
}

function resetFilters() {
    filters.value = { area: '', deviceName: '', result: null };
    dateRange.value = null;
    search();
}

async function openDetail(id: number) {
    detailVisible.value = true;
    detail.value = await getReportDetail(id);
}

async function exportExcel() {
    exporting.value = true;
    try {
        await exportReport({
            area: filters.value.area || undefined,
            deviceName: filters.value.deviceName || undefined,
            result: filters.value.result ?? undefined,
            startTime: dateRange.value?.[0] ? dateRange.value[0] + 'T00:00:00' : undefined,
            endTime: dateRange.value?.[1] ? dateRange.value[1] + 'T23:59:59' : undefined,
        });
        ElMessage.success('导出成功');
    } catch (e: any) {
        ElMessage.error(e.message || '导出失败');
    } finally {
        exporting.value = false;
    }
}

function resultText(result: number) {
    return result === 0 ? '正常' : result === 1 ? '故障' : '隐患';
}

function resultTag(result: number) {
    return result === 0 ? 'success' : result === 1 ? 'danger' : 'warning';
}
</script>