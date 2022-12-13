export const routes = [
    { path: '', name: 'home',  component: () => import('./pages/Home') },
    { path: '/help', component: () => import('./pages/Help') },
    { path: '/login', component: () => import('./pages/Login') },
    { path: '/admin', component: () => import('./pages/admin/Index') },
    { path: '/step/1', component: () => import('./pages/steps/Step-1') },
    { path: '/step/2', component: () => import('./pages/steps/Step-2') },
    { path: '/step/3', component: () => import('./pages/steps/Step-3') },
    { path: '/step/4', component: () => import('./pages/steps/Step-4') },
    { path: '/step/5', component: () => import('./pages/steps/Step-5') },
    { path: '/step/6', component: () => import('./pages/steps/Step-6') },
    { path: '/step/7', component: () => import('./pages/steps/Step-7') },
]
