export const routes = [
    { path: '', name: 'home',  component: () => import('./pages/Home') },
    { path: '/help', component: () => import('./pages/Help') },
    { path: '/login', component: () => import('./pages/settings/Login') },
    { path: '/print/index', component: () => import('./pages/print/PrintMain') },
    { path: '/print/qr', component: () => import('./pages/print/QR/PrintQr') },
    { path: '/settings', component: () => import('./pages/settings/Index') },
    { path: '/number-phone/add', component: () => import('./pages/driver/InputAndSavePhone') },

    { 
        path: '/step/driver', component: () => import('./pages/inspection/Step-driver'), name: 'step-driver',
        meta: {visible: 'driver_info', next: 'step-photo', prev: 'home', number: 1 }
    },
    { 
        path: '/step/photo', component: () => import('./pages/inspection/Step-photo'), name: 'step-photo',
        meta: {visible: 'driver_photo', next: 'step-ride', prev: 'step-driver', number: 2 } 
    },
    { 
        path: '/step/ride', component: () => import('./pages/inspection/Step-ride'), name: 'step-ride',
        meta: {visible: 'type_ride', next: 'step-tonometer', prev: 'step-driver', number: 3 } 
    },
    { 
        path: '/step/tonometer', component: () => import('./pages/inspection/Step-tonometer'), name: 'step-tonometer',
        meta: {visible: 'tonometer_visible', next: 'step-thermometer', prev: 'step-ride', number: 4 } 
    },
    { 
        path: '/step/thermometer', component: () => import('./pages/inspection/Step-thermometer'), name: 'step-thermometer',
        meta: {visible: 'thermometer_visible', next: 'step-alcometer', prev: 'step-thermometer', number: 5 } 
    },
    { 
        path: '/step/alcometer', component: () => import('./pages/inspection/Step-alcometer'), name: 'step-alcometer',
        meta: {visible: 'alcometer_visible', next: 'step-sleep', prev: 'step-alcometer', number: 6 } 
    },
    { 
        path: '/step/sleep', component: () => import('./pages/inspection/Step-sleep'), name: 'step-sleep',
        meta: {visible: 'question_sleep', next: 'step-helth', prev: 'step-alcometer', number: 7 } 
    },
    { 
        path: '/step/helth', component: () => import('./pages/inspection/Step-helth'), name: 'step-helth',
        meta: {visible: 'question_helth', next: 'step-result', prev: 'step-sleep', number: 8 } 
    },
    { 
        path: '/step/result', component: () => import('./pages/inspection/Step-result'), name: 'step-result',
        meta: { next: 'home', prev: 'step-result', number: 9 } 
    },
    {
        path: '/step/retry', component: () => import('./pages/inspection/Step-result'), name: 'step-retry',
        meta: { next: 'step-driver', prev: 'step-result', number: 0 } },
]
