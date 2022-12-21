export const routes = [
    { path: '', name: 'home',  component: () => import('./pages/Home') },
    { path: '/help', component: () => import('./pages/Help') },
    { path: '/login', component: () => import('./pages/Login') },
    { path: '/admin', component: () => import('./pages/admin/Index') },

    { 
        path: '/step/1', component: () => import('./pages/steps/Step-1'), name: 'step-1',
        meta: {visible: 'driver_info', next: 'step-2', prev: 'home', number: 1 } 
    },

    { 
        path: '/step/2', component: () => import('./pages/steps/Step-2'), name: 'step-2', 
        meta: {visible: 'type_ride', next: 'step-3', prev: 'step-1', number: 2 } 
    },
    { 
        path: '/step/3', component: () => import('./pages/steps/Step-3'), name: 'step-3', 
        meta: {visible: 'tonometer_visible', next: 'step-4', prev: 'step-2', number: 3 } 
    },
    { 
        path: '/step/4', component: () => import('./pages/steps/Step-4'), name: 'step-4', 
        meta: {visible: 'thermometer_visible', next: 'step-5', prev: 'step-3', number: 4 } 
    },
    { 
        path: '/step/5', component: () => import('./pages/steps/Step-5'), name: 'step-5', 
        meta: {visible: 'alcometer_visible', next: 'step-6', prev: 'step-4', number: 5 } 
    },
    { 
        path: '/step/6', component: () => import('./pages/steps/Step-6'), name: 'step-6', 
        meta: {visible: 'question_sleep', next: 'step-7', prev: 'step-5', number: 6 } 
    },
    { 
        path: '/step/7', component: () => import('./pages/steps/Step-7'), name: 'step-7', 
        meta: {visible: 'question_helth', next: 'step-8', prev: 'step-6', number: 7 } 
    },
    { 
        path: '/step/8', component: () => import('./pages/steps/Step-8'), name: 'step-8', 
        meta: { next: 'home', prev: 'step-7', number: 8 } 
    },
]
