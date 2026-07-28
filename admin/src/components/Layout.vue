<template>
  <el-container style="height: 100vh;">
    <el-aside width="220px">
      <Sidebar />
    </el-aside>
    <el-container>
      <el-header style="background: #fff; border-bottom: 1px solid #dcdfe6; display: flex; justify-content: space-between; align-items: center;">
        <h3 style="margin: 0; line-height: 60px;">巡检管理系统</h3>
        <el-button type="danger" plain size="small" @click="handleLogout">退出登录</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { removeToken } from '@/utils/storage';
import Sidebar from './Sidebar.vue';

const router = useRouter();

function handleLogout() {
  removeToken();
  router.push('/login');
}
</script>