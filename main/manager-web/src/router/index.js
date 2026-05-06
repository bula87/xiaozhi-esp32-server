import Vue from 'vue'
import VueRouter from 'vue-router'
import { isDebugSkipLoginEnabled } from '@/utils/debug'
import AuroraLayout from '@/components/AuroraLayout.vue'

Vue.use(VueRouter)

const routes = [
  // --- PUBLIC ROUTES (No Layout) ---
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/retrieve-password',
    name: 'RetrievePassword',
    component: () => import('../views/RetrievePassword.vue')
  },

  // --- PROTECTED ROUTES (Inside AuroraLayout) ---
  {
    path: '/',
    component: AuroraLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '', 
        redirect: 'dashboard'
      },
      {
        path: 'dashboard', 
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: 'role-config',
        name: 'RoleConfig',
        component: () => import('../views/RoleConfig.vue')
      },
      {
        path: 'voice-print',
        name: 'VoicePrint',
        component: () => import('../views/VoicePrint.vue')
      },
      {
        path: 'device-management',
        name: 'DeviceManagement',
        component: () => import('../views/DeviceManagement.vue')
      },
      {
        path: 'user-management',
        name: 'UserManagement',
        component: () => import('../views/UserManagement.vue')
      },
      {
        path: 'model-config',
        name: 'ModelConfig',
        component: () => import('../views/ModelConfig.vue')
      },
      {
        path: "settings",
        name: "SettingsHub",
        component: () => import("../views/SettingsHub.vue")
      },
      {
        path: 'params-management',
        name: 'ParamsManagement',
        component: () => import('../views/ParamsManagement.vue'),
        meta: { title: "Parameter Management" } 
      },
      {
        path: 'knowledge-base-management',
        name: 'KnowledgeBaseManagement',
        component: () => import('../views/KnowledgeBaseManagement.vue'),
        meta: { title: "Knowledge Base Management" }
      },
      {
        path: 'knowledge-file-upload',
        name: 'KnowledgeFileUpload',
        component: () => import('../views/KnowledgeFileUpload.vue'),
        meta: { title: "Document Upload Management" }
      },
      {
        path: 'server-side-management',
        name: 'ServerSideManager',
        component: () => import('../views/ServerSideManager.vue'),
        meta: { title: "Server Management" }
      },
      {
        path: 'ota-management',
        name: 'OtaManagement',
        component: () => import('../views/OtaManagement.vue'),
        meta: { title: "OTA Management" }
      },
      {
        path: 'voice-resource-management',
        name: 'VoiceResourceManagement',
        component: () => import('../views/VoiceResourceManagement.vue'),
        meta: { title: "Voice Resource Activation" }
      },
      {
        path: "voice-clone-management",
        name: "VoiceCloneManagement",
        component: () => import("../views/VoiceCloneManagement.vue"),
        meta: { title: "Voice Clone Management" }
      },
      {
        path: "dict-management",
        name: "DictManagement",
        component: () => import("../views/DictManagement.vue")
      },
      {
        path: "provider-management",
        name: "ProviderManagement",
        component: () => import("../views/ProviderManagement.vue")
      },
      {
        path: "agent-template-management",
        name: "AgentTemplateManagement",
        component: () => import("../views/AgentTemplateManagement.vue")
      },
      {
        path: "template-quick-config",
        name: "TemplateQuickConfig",
        component: () => import("../views/TemplateQuickConfig.vue")
      },
      {
        path: "feature-management",
        name: "FeatureManagement",
        component: () => import("../views/FeatureManagement.vue"),
        meta: { title: "Feature Configuration" }
      },
      {
        path: "replacement-word-management",
        name: "ReplacementWordManagement",
        component: () => import("../views/ReplacementWordManagement.vue"),
        meta: { title: "Replacement word management" }
      }
    ]
  }
]

const router = new VueRouter({
  base: process.env.VUE_APP_PUBLIC_PATH || "/",
  routes,
});

// Handle duplicate navigation globally
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch((err) => {
    if (err.name === "NavigationDuplicated") {
      return err; 
    } else {
      throw err;
    }
  })
}

// Route guard
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");

  // 1. Handle Developer Bypass
  if (isDebugSkipLoginEnabled) {
    if (to.name === "Login") {
      next({ name: "Dashboard" });
      return;
    }
    next();
    return;
  }

  // 2. Prevent logged-in users from seeing the Login page
  if (to.name === "Login" && token) {
    next({ name: "Dashboard" });
    return;
  }

  // 3. Check if route requires auth
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      // Unauthenticated users are sent to the newly capitalized 'Login' route
      next({ name: "Login", query: { redirect: to.fullPath } });
      return;
    }
  }
  
  next()
})

export default router;