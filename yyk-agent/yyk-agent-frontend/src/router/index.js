import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: {
      title: '首页 - 亚克AI超级智能体应用平台',
      description: '亚克AI超级智能体应用平台提供AI编程大师和AI超级智能体服务，满足您的各种AI对话需求'
    }
  },
  {
    path: '/program-master',
    name: 'ProgramMaster',
    component: () => import('../views/ProgramMaster.vue'),
    meta: {
      title: 'AI编程大师 - 亚克AI超级智能体应用平台',
      description: 'AI编程大师是亚克AI超级智能体应用平台的专业编程顾问，帮你解答各种编程问题，提供编程建议'
    }
  },
  {
    path: '/super-agent',
    name: 'SuperAgent',
    component: () => import('../views/SuperAgent.vue'),
    meta: {
      title: 'AI超级智能体 - 亚克AI超级智能体应用平台',
      description: 'AI超级智能体是亚克AI超级智能体应用平台的全能助手，能解答各类专业问题，提供精准建议和解决方案'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局导航守卫，设置文档标题
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

export default router 