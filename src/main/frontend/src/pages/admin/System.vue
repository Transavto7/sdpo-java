<script>
import {saveSystem, saveApi} from '@/helpers/settings';
import {useToast} from "vue-toastification";
import {getSizes} from '@/helpers/camera';
import {getPoint} from '@/helpers/api';

export default {
  data() {
    return {
      toast: useToast(),
      videoSizes: [],
    }
  },
  async mounted() {
    this.videoSizes = await getSizes();
  },
  methods: {
    async save() {
      await saveSystem(this.system);
      await saveApi(this.config.url, this.config.token);
      this.$store.state.point = await getPoint();
      this.toast.success('Настройки сохранены');
    },
  },
  computed: {
    system() {
      return this.$store.state.config?.system || {};
    },
    config() {
      return this.$store.state.config?.main || {};
    },
    connection() {
      return this.$store.state.connection || false;
    }
  },
}
</script>

<template>
  <div class="admin__system">
    <div class="admin__system-card animate__animated animate__fadeInUp d-1">
      <div class="admin__system-card__title">
        Основные настройки
      </div>
      <div class="admin__system-card__item">
        <span>Информация о водителе</span>
        <label class="switch">
          <input type="checkbox" v-model="system.driver_info">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>Тип осмотра</span>
        <label class="switch">
          <input type="checkbox" v-model="system.type_ride">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>Вопрос о сне</span>
        <label class="switch">
          <input type="checkbox" v-model="system.question_sleep">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>вопрос о самочувствии</span>
        <label class="switch">
          <input type="checkbox" v-model="system.question_helth">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>Ручной режим</span>
        <label class="switch">
          <input type="checkbox" v-model="system.manual_mode">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>Переход в начало</span>
        <label class="switch">
          <input type="checkbox" v-model="system.auto_start">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>Задержка перед <br> повторным прохождением <br>(в миллисекундах)</span>
        <input min="1000" class="medium" type="number" v-model="system.delay_before_retry_inspection">
      </div>
    </div>


    <div class="admin__system-card animate__animated animate__fadeInUp d-2">
      <div class="admin__system-card__title">
        Алкометр
      </div>
      <div class="admin__system-card__item">
        <span>Пропуск</span>
        <label class="switch">
          <input type="checkbox" v-model="system.alcometer_skip">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>включен</span>
        <label class="switch">
          <input type="checkbox" v-model="system.alcometer_visible">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>Быстрый режим</span>
        <label class="switch">
          <input type="checkbox" v-model="system.alcometer_fast">
          <div class="slider round"></div>
        </label>
      </div>
    </div>


    <div class="admin__system-card animate__animated animate__fadeInUp d-3">
      <div class="admin__system-card__title">
        Тонометр
      </div>
      <div class="admin__system-card__item">
        <span>Пропуск</span>
        <label class="switch">
          <input type="checkbox" v-model="system.tonometer_skip">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>включен</span>
        <label class="switch">
          <input type="checkbox" v-model="system.tonometer_visible">
          <div class="slider round"></div>
        </label>
      </div>
    </div>


    <div class="admin__system-card animate__animated animate__fadeInUp d-4">
      <div class="admin__system-card__title">
        Камера
      </div>
      <div class="admin__system-card__item">
        <span>Качество</span>
        <select v-model="system.camera_dimension">
          <option v-for="size in videoSizes" :key="size" :value="size">{{ size }}</option>
        </select>
      </div>
      <div class="admin__system-card__item">
        <span>Видео</span>
        <label class="switch">
          <input type="checkbox" v-model="system.camera_video">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>Фото</span>
        <label class="switch">
          <input type="checkbox" v-model="system.camera_photo">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>Фото водителя</span>
        <label class="switch">
          <input type="checkbox" v-model="system.driver_photo">
          <div class="slider round"></div>
        </label>
      </div>
    </div>


    <div class="admin__system-card animate__animated animate__fadeInUp d-5">
      <div class="admin__system-card__title">
        Принтер
      </div>
      <div class="admin__system-card__item">
        <span>Печать</span>
        <label class="switch">
          <input type="checkbox" v-model="system.printer_write">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>Количество</span>
        <input min="1" class="small" type="number" v-model="system.print_count">
      </div>
    </div>


    <div class="admin__system-card animate__animated animate__fadeInUp d-6">
      <div class="admin__system-card__title">
        Термометр
      </div>
      <div class="admin__system-card__item">
        <span>Пропуск</span>
        <label class="switch">
          <input type="checkbox" v-model="system.thermometer_skip">
          <div class="slider round"></div>
        </label>
      </div>
      <div class="admin__system-card__item">
        <span>включен</span>
        <label class="switch">
          <input type="checkbox" v-model="system.thermometer_visible">
          <div class="slider round"></div>
        </label>
      </div>
    </div>
    <div class="admin__system-card block animate__animated animate__fadeInUp d-6">
      <div class="admin__system-card__title">
        Настройки API
        <div class="connection-status"
             :class="{active: connection}"
        >
          <span>{{ connection }}</span>
        </div>
      </div>
      <div class="admin__system-card__item">
        <span>Адрес</span>
        <input v-model="config.url">
      </div>
      <div class="admin__system-card__item">
        <span>Токен</span>
        <input v-model="config.token">
      </div>
    </div>

    <div class="admin__system-footer  animate__animated animate__fadeInUp d-7">
      <button @click="save" class="btn blue">Сохранить</button>
    </div>
  </div>
</template>