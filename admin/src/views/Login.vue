<template>
  <div class="login-wrapper">
    <el-card class="login-card">
      <h2 class="login-title">巡检管理后台</h2>
      <p class="login-desc">开发阶段请输入管理员 Token 登录</p>
      <el-form @submit.prevent="handleLogin">
        <el-form-item>
          <el-input
            v-model="token"
            type="password"
            placeholder="请输入 Admin Token"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { setToken } from '@/utils/storage';
import http from '@/api/request';

const router = useRouter();
const token = ref('');
const loading = ref(false);

async function handleLogin() {
  const value = token.value.trim();
  if (!value) {
    ElMessage.warning('请输入 Token');
    return;
  }

  loading.value = true;
  try {
    // 保存 token
    setToken(value);

    // 验证 token（调用 statistics 接口）
    await http.get('/statistics');

    ElMessage.success('登录成功');
    router.push('/dashboard');
  } catch (err: any) {
    // 清除无效 token
    setToken(''); // 临时保存空值触发 removeToken
    localStorage.removeItem('inspection_admin_token');

    if (err?.response?.status === 403) {
      ElMessage.error('当前账号无管理员权限');
    } else if (err?.response?.status === 401) {
      ElMessage.error('Token 无效');
    } else {
      ElMessage.error(err?.message || 'Token 无效或网络错误');
    }
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f2f5;
}
.login-card {
  width: 400px;
}
.login-title {
  text-align: center;
  margin-bottom: 8px;
}
.login-desc {
  text-align: center;
  color: #909399;
  font-size: 13px;
  margin-bottom: 24px;
}
</style>