<template>
  <el-menu
    router
    :default-active="route.path"
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409EFF"
  >
    <el-menu-item index="/dashboard">Dashboard</el-menu-item>
    <el-menu-item index="/reports">巡检记录</el-menu-item>
    <el-menu-item v-if="isAdmin" index="/users">用户管理</el-menu-item>
  </el-menu>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import http from '@/api/request';

const route = useRoute();
const isAdmin = ref(false);

onMounted(async () => {
  try {
    await http.get('/users?pageSize=1');
    isAdmin.value = true;
  } catch {
    isAdmin.value = false;
  }
});
</script>