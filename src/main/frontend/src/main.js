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
import {getSettings, loadSettings} from './helpers/settings'
import {checkConnect, saveInspection} from './helpers/api/api'
import { closeAlcometer } from './helpers/alcometer'
import 'animate.css'
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'

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
    if (to.name === 'home' && getSettings('alcometer_fast') && getSettings('alcometer_retry') && Number(store.state.inspection.alcometer_result) > 0) {
      store.state.inspection.photo = store.state.temp.photo;
      store.state.inspection.video = store.state.temp.video;
      return router.push({ name: 'step-result'});
    }
  }

  if (to.name === 'step-retry') {
    from.meta.number = 0;
    return router.push({ name: to.meta.next });
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

  if (to.path.includes('/settings/')) {
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
  if (store.state.config?.connection?.url) {
    store.state.connection = await checkConnect(store.state.config.connection.url);
    if (store.state.connection === undefined) store.state.connection = false;
  }
}, 2000);

createApp(App)
    .use(store)
    .use(Toast, {
        position: 'top-center',
        timeout: 4500
    })
    .use(router)
    .component('VueDatePicker', VueDatePicker)
    .mount('#app')
