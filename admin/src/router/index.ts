import { createRouter, createWebHistory } from 'vue-router';
import { getToken } from '@/utils/storage';

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
    },
    {
        path: '/',
        component: () => import('@/components/Layout.vue'),
        children: [
            {
                path: '',
                redirect: '/dashboard',
            },
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/Dashboard.vue'),
            },
            {
                path: 'reports',
                name: 'Reports',
                component: () => import('@/views/Reports.vue'),
            },
            {
                path: 'users',
                name: 'Users',
                component: () => import('@/views/Users.vue'),
            },
        ],
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// 全局路由守卫
router.beforeEach((to, _from, next) => {
    const token = getToken();
    if (to.path === '/login') {
        // 已登录用户访问登录页 → 跳转 dashboard
        if (token) {
            next('/dashboard');
        } else {
            next();
        }
    } else {
        // 未登录用户访问后台页面 → 跳转登录页
        if (!token) {
            next('/login');
        } else {
            next();
        }
    }
});

export default router;