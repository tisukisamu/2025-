import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '../stores/user'
import MainLayout from '../components/MainLayout.vue'

const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/auth/Login.vue'),
        meta: { public: true, title: '登录' }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/auth/Register.vue'),
        meta: { public: true, title: '注册' }
    },

    {
        path: '/',
        component: MainLayout,
        redirect: '/home',
        children: [

            {
                path: 'home',
                name: 'Home',
                component: () => import('../views/Home.vue'),
                meta: { title: '首页' }
            },

            {
                path: 'about',
                name: 'About',
                component: () => import('../views/About.vue'),
                meta: { public: true, title: '关于' }
            },

            {
                path: 'today',
                name: 'Today',
                component: () => import('../views/check/Today.vue'),
                meta: { requireAuth: true, title: '今日打卡' }
            },

            {
                path: 'habits',
                name: 'Habits',
                component: () => import('../views/habit/List.vue'),
                meta: { requireAuth: true, title: '习惯管理' }
            },

            {
                path: 'habits/create',
                name: 'HabitCreate',
                component: () => import('../views/habit/Form.vue'),
                meta: { requireAuth: true, title: '创建习惯' }
            },

            {
                path: 'habits/:id',
                name: 'HabitDetail',
                component: () => import('../views/habit/Detail.vue'),
                meta: { requireAuth: true, title: '习惯详情' }
            },

            {
                path: 'habits/:id/edit',
                name: 'HabitEdit',
                component: () => import('../views/habit/Form.vue'),
                meta: { requireAuth: true, title: '编辑习惯' }
            },

            {
                path: 'habits/categories',
                name: 'Categories',
                component: () => import('../views/habit/Categories.vue'),
                meta: { requireAuth: true, title: '分类管理' }
            },

            {
                path: 'templates/habits',
                name: 'HabitTemplates',
                component: () => import('../views/templates/HabitTemplates.vue'),
                meta: { requireAuth: true, title: '模板中心' }
            },

            {
                path: 'calendar',
                name: 'Calendar',
                component: () => import('../views/check/Calendar.vue'),
                meta: { requireAuth: true, title: '打卡日历' }
            },

            {
                path: 'history',
                name: 'History',
                component: () => import('../views/check/History.vue'),
                meta: { requireAuth: true, title: '打卡历史' }
            },

            {
                path: 'statistics',
                name: 'Statistics',
                component: () => import('../views/statistics/Dashboard.vue'),
                meta: { requireAuth: true, title: '数据统计' }
            },

            {
                path: 'statistics/heatmap',
                name: 'Heatmap',
                component: () => import('../views/statistics/Heatmap.vue'),
                meta: { requireAuth: true, title: '热力图' }
            },

            {
                path: 'statistics/weekly',
                name: 'WeeklyReport',
                component: () => import('../views/statistics/WeeklyReport.vue'),
                meta: { requireAuth: true, title: '周报分析' }
            },

            {
                path: 'achievements',
                name: 'Achievements',
                component: () => import('../views/achievements/Index.vue'),
                meta: { requireAuth: true, title: '成就系统' }
            },
            {
                path: 'achievements/ranking',
                name: 'AchievementRanking',
                component: () => import('../views/achievements/Ranking.vue'),
                meta: { requireAuth: true, title: '成就排行榜' }
            },

            {
                path: 'reminders',
                name: 'Reminders',
                component: () => import('../views/reminders/Today.vue'),
                meta: { requireAuth: true, title: '提醒中心' }
            },

            {
                path: 'community',
                name: 'Community',
                component: () => import('../views/community/Index.vue'),
                meta: { requireAuth: true, title: '打卡社区' },
                redirect: '/community/feed',
                children: [
                    {
                        path: 'feed',
                        name: 'CommunityFeed',
                        component: () => import('../views/community/Feed.vue'),
                        meta: { requireAuth: true, title: '社区动态' }
                    },
                    {
                        path: 'publish',
                        name: 'CommunityPublish',
                        component: () => import('../views/community/Publish.vue'),
                        meta: { requireAuth: true, title: '发布动态' }
                    },
                    {
                        path: 'mine',
                        name: 'CommunityMine',
                        component: () => import('../views/community/Mine.vue'),
                        meta: { requireAuth: true, title: '我的动态' }
                    }
                ]
            },

            {
                path: 'alumni',
                name: 'Alumni',
                component: () => import('../views/alumni/Index.vue'),
                meta: { requireAuth: true, title: '校友互督' },
                redirect: '/alumni/nearby',
                children: [
                    {
                        path: 'nearby',
                        name: 'AlumniNearby',
                        component: () => import('../views/alumni/Nearby.vue'),
                        meta: { requireAuth: true, title: '附近发现' }
                    },
                    {
                        path: 'teams',
                        name: 'AlumniTeams',
                        component: () => import('../views/alumni/Teams.vue'),
                        meta: { requireAuth: true, title: '小队互督' }
                    },
                    {
                        path: 'profile',
                        name: 'AlumniProfile',
                        component: () => import('../views/alumni/Profile.vue'),
                        meta: { requireAuth: true, title: '校友资料' }
                    }
                ]
            },

            {
                path: 'profile',
                name: 'Profile',
                component: () => import('../views/Profile.vue'),
                meta: { requireAuth: true, title: '个人中心' }
            },

            {
                path: 'settings',
                name: 'Settings',
                component: () => import('../views/settings/Index.vue'),
                meta: { requireAuth: true, title: '系统设置' }
            },

            {
                path: 'users',
                name: 'Users',
                component: () => import('../views/Users.vue'),
                meta: { requireAuth: true, requireAdmin: true, title: '用户管理' }
            },

            {
                path: 'admin',
                name: 'Admin',
                component: () => import('../views/admin/Dashboard.vue'),
                meta: { requireAuth: true, requireAdmin: true, title: '管理后台' }
            }
        ]
    },

    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('../views/NotFound.vue'),
        meta: { title: '页面未找到' }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior(_to, _from, savedPosition) {
        if (savedPosition) return savedPosition
        return { top: 0 }
    }
})

router.beforeEach(async (to, _from, next) => {

    const userStore = useUserStore()

    document.title = `${to.meta.title || 'HabitFlow'} - HabitFlow`

    if (!userStore.userInfo && userStore.token) {
        await userStore.init()
    }

    if (to.meta.public) {
        if (userStore.isLoggedIn && (to.path === '/login' || to.path === '/register')) {
            return next('/home')
        }
        return next()
    }

    if (to.meta.requireAuth && !userStore.isLoggedIn) {
        return next({
            path: '/login',
            query: { redirect: to.fullPath }
        })
    }

    if (to.meta.requireAdmin && !userStore.isAdmin) {
        return next('/')
    }

    next()
})

export default router
