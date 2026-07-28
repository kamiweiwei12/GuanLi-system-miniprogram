<template>
  <div class="users-container">
    <!-- 搜索栏 -->
    <el-card style="margin-bottom: 16px;">
      <el-form :inline="true" :model="filters">
        <el-form-item label="昵称">
          <el-input v-model="filters.nickname" placeholder="用户昵称" clearable />
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="filters.phone" placeholder="手机号" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="filters.roleCode" placeholder="全部" clearable style="width: 140px;">
            <el-option label="ADMIN" value="ADMIN" />
            <el-option label="DEPT_ADMIN" value="DEPT_ADMIN" />
            <el-option label="EMPLOYEE" value="EMPLOYEE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表 -->
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机" width="130" />
        <el-table-column prop="departmentName" label="部门" width="120" />
        <el-table-column label="角色" width="130">
          <template #default="{ row }">
            <el-tag :type="roleTag(row.roleCode)" size="small">{{ row.roleName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              active-text="启用"
              inactive-text="禁用"
              inline-prompt
              @change="(val: boolean) => handleStatusChange(row.id, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openRoleDialog(row)">修改角色</el-button>
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

    <!-- 修改角色弹窗 -->
    <el-dialog v-model="roleDialogVisible" title="修改用户角色" width="400px">
      <el-form>
        <el-form-item label="当前用户">
          <span>{{ roleTarget?.nickname }} (ID: {{ roleTarget?.id }})</span>
        </el-form-item>
        <el-form-item label="新角色">
          <el-select v-model="selectedRoleId" placeholder="请选择角色" style="width: 100%;">
            <el-option label="ADMIN (系统管理员)" :value="3" />
            <el-option label="DEPT_ADMIN (部门管理员)" :value="2" />
            <el-option label="EMPLOYEE (普通员工)" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRoleChange">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getUsers, updateUserRole, updateUserStatus, UserVO } from '@/api/user';

const list = ref<UserVO[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);
const filters = ref({ nickname: '', phone: '', roleCode: '' });

const roleDialogVisible = ref(false);
const roleTarget = ref<UserVO | null>(null);
const selectedRoleId = ref<number>(1);

onMounted(() => loadData());

async function loadData() {
    loading.value = true;
    try {
        const res = await getUsers({
            pageNum: currentPage.value,
            pageSize: pageSize.value,
            nickname: filters.value.nickname || undefined,
            phone: filters.value.phone || undefined,
            roleCode: filters.value.roleCode || undefined,
        });
        list.value = res.list;
        total.value = res.total;
    } catch (err: any) {
        ElMessage.error('当前账号无权限访问用户管理');
    } finally {
        loading.value = false;
    }
}

function search() {
    currentPage.value = 1;
    loadData();
}

function resetFilters() {
    filters.value = { nickname: '', phone: '', roleCode: '' };
    search();
}

async function handleStatusChange(userId: number, enabled: boolean) {
    try {
        await updateUserStatus(userId, enabled ? 1 : 0);
        ElMessage.success(enabled ? '已启用' : '已禁用');
        loadData();
    } catch (err: any) {
        ElMessage.error(err.message || '操作失败');
    }
}

function openRoleDialog(user: UserVO) {
    roleTarget.value = user;
    if (user.roleCode === 'ADMIN') selectedRoleId.value = 3;
    else if (user.roleCode === 'DEPT_ADMIN') selectedRoleId.value = 2;
    else selectedRoleId.value = 1;
    roleDialogVisible.value = true;
}

async function confirmRoleChange() {
    if (!roleTarget.value) return;
    try {
        await updateUserRole(roleTarget.value.id, selectedRoleId.value);
        ElMessage.success('角色修改成功');
        roleDialogVisible.value = false;
        loadData();
    } catch (err: any) {
        ElMessage.error(err.message || '修改失败');
    }
}

function roleTag(roleCode: string) {
    if (roleCode === 'ADMIN') return 'danger';
    if (roleCode === 'DEPT_ADMIN') return 'warning';
    return 'info';
}
</script>