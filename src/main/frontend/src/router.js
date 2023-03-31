export const routes = [
    { path: '', name: 'home',  component: () => import('./pages/Home') },
    { path: '/help', component: () => import('./pages/Help') },
    { path: '/login', component: () => import('./pages/Login') },
    { path: '/admin', component: () => import('./pages/admin/Index') },

    { 
        path: '/step/driver', component: () => import('./pages/steps/Step-driver'), name: 'step-driver',
        meta: {visible: 'driver_info', next: 'step-photo', prev: 'home', number: 1 } 
    },
    { 
        path: '/step/photo', component: () => import('./pages/steps/Step-photo'), name: 'step-photo', 
        meta: {visible: 'driver_photo', next: 'step-ride', prev: 'step-driver', number: 2 } 
    },
    { 
        path: '/step/ride', component: () => import('./pages/steps/Step-ride'), name: 'step-ride', 
        meta: {visible: 'type_ride', next: 'step-tonometer', prev: 'step-driver', number: 3 } 
    },
    { 
        path: '/step/tonometer', component: () => import('./pages/steps/Step-tonometer'), name: 'step-tonometer', 
        meta: {visible: 'tonometer_visible', next: 'step-thermometer', prev: 'step-ride', number: 4 } 
    },
    { 
        path: '/step/thermometer', component: () => import('./pages/steps/Step-thermometer'), name: 'step-thermometer', 
        meta: {visible: 'thermometer_visible', next: 'step-sleep', prev: 'step-thermometer', number: 5 } 
    },
    { 
        path: '/step/alcometer', component: () => import('./pages/steps/Step-alcometer'), name: 'step-alcometer', 
        meta: {visible: 'alcometer_visible', next: 'step-sleep', prev: 'step-alcometer', number: 6 } 
    },
    { 
        path: '/step/sleep', component: () => import('./pages/steps/Step-sleep'), name: 'step-sleep', 
        meta: {visible: 'question_sleep', next: 'step-helth', prev: 'step-alcometer', number: 7 } 
    },
    { 
        path: '/step/helth', component: () => import('./pages/steps/Step-helth'), name: 'step-helth', 
        meta: {visible: 'question_helth', next: 'step-result', prev: 'step-sleep', number: 8 } 
    },
    { 
        path: '/step/result', component: () => import('./pages/steps/Step-result'), name: 'step-result', 
        meta: { next: 'home', prev: 'step-result', number: 9 } 
    },
]
