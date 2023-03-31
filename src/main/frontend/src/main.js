import { createApp } from 'vue'
import App from './App.vue'
import './assets/scss/style.scss'
import 'remixicon/fonts/remixicon.css'
import store from './store'
import axios from 'axios';
import { createWebHistory, createRouter } from "vue-router";
import { routes } from './router'
import Toast from "vue-toastification"
import "vue-toastification/dist/index.css"
import { loadSettings } from './helpers/settings'
import { checkConnect } from './helpers/api'
import 'animate.css'
import { closeAlcometer } from './helpers/alcometer'

axios.defaults.baseURL = 'http://localhost:8080/';
window.axios = axios;
loadSettings();


const router = createRouter({
    history: createWebHistory(),
    routes,
    mode: 'history',
    scrollBehavior(to, from, savedPosition) {
      if (savedPosition) {
        return savedPosition
      } else {
        return { top: 0, left: 0 }
      }
    },
})

router.beforeEach((to, from, next) => {
  if (from.name === 'step-alcometer') {
    closeAlcometer();
  }

  if (to.path.includes('/step/')) {
    if (!store.state.inspection?.driver_id) {
      return router.push({ name: 'home'});
    }

    if (to.meta?.visible && store.state.config?.system) {
      if (!JSON.parse(store.state.config.system[to.meta.visible])) {
        if (from.meta?.number > to.meta.number) {
          return router.push({ name: to.meta.prev });
        }

        return router.push({ name: to.meta.next });
      }
    }
  }

  if (to.path.includes('/admin/')) {
    if (!store.state.admin) {
      return router.push({ name: 'home' });
    }
  }

  if (to.name === 'home') {
    store.state.inspection = {};
  }

  return next();
})

setInterval(async () => {
  if (store.state.config?.main?.url) {
    store.state.connection = await checkConnect(store.state.config.main.url);
  }
}, 2000);

createApp(App)
    .use(store)
    .use(Toast, {
        position: 'top-center',
        timeout: 2500
    })
    .use(router)
    .mount('#app')
