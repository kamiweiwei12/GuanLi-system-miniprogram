<template>
  <el-container style="height: 100vh;">
    <el-aside width="220px">
      <Sidebar />
    </el-aside>
    <el-container>
      <el-header style="background: #fff; border-bottom: 1px solid #dcdfe6;">
        <h3 style="margin: 0; line-height: 60px;">巡检管理系统</h3>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import Sidebar from './Sidebar.vue';
</script>