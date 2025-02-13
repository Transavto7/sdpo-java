<script>
import {getDriver, savePhone} from '@/helpers/api/api';
import {useToast} from "vue-toastification";
import MedicSelect from '@/components/MedicSelect.vue';
import InputPersonalNumberForm from "@/components/InputPersonalNumberForm";
import InputPhoneNumber from "@/components/InputPhoneNumber";
import InputAndSavePhone from "@/pages/driver/InputAndSavePhone";
import store from "@/store";
import {getSettings} from "@/helpers/settings";

export default {
  name: 'Home',
  components: {InputAndSavePhone, InputPhoneNumber, MedicSelect, InputPersonalNumberForm},
  data() {
    return {
      driver_id: '',
      error: '',
      toast: useToast(),
      loading: false,
    }
  },
  methods: {
    async start() {
      const driver = await getDriver(this.driver_id);
      if (!driver) {
        this.driver_id = '';
        this.toast.error('Водитель с указаным ID не найден');
        return;
      }

      this.$store.state.inspection.driver_id = driver.hash_id;
      this.$store.state.inspection.driver_fio = driver.fio;

      if (this.needSetNumberPhone)
        this.$router.push('/number-phone/add/')
      else
        this.$router.push({name: 'step-driver'});
    },
    updateDriverId(inputPassword) {
      this.driver_id = inputPassword;
    },
    async checkDriver() {
      this.$store.state.inspection = {};
      this.error = null;

      if (this.driver_id.length < 6) {
        return;
      }

      this.loading = true;
      try {
        const driver = await getDriver(this.driver_id);
        if (driver) {
          this.$store.state.inspection.driver_id = driver.hash_id;
          this.$store.state.inspection.driver_fio = driver.fio;
          this.$store.state.driver = driver;
          this.error = null;
        } else {
          this.$store.state.inspection = {};
          this.error = 'Водитель не найден';
        }
      } catch (error) {
        console.log(error.response?.data?.message);
        this.error = error.response?.data?.message || 'Неизвестная ошибка';
        this.$store.state.inspection = {};
      }

      this.loading = false;
    }
  },
  watch: {
    driver_id: function (val) {
      this.checkDriver();
    }
  },
  computed: {
    terminalIsLocked() {

      if (!store.state.connection) {
        if (this.system.max_inspection_in_offline_mod - this.system.count_inspections <= 0) return true;

        let lastOnline = this.system.last_online || new Date();
        return (((new Date()) - Date.parse(lastOnline)) / 8.64e7 > this.system.delay_day_in_offline_mod)
      }
      return false;
    },
    system() {
      return this.$store.state.config?.system || {};
    },
    config() {
      return this.$store.state.config;
    },
    inspection() {
      return this.$store.state.inspection;
    },
    hasDriver() {
      return this.inspection.driver_id && !this.error
    },
    hasPhoneNumber() {
      return this.$store.state.driver.phone && !this.error
    },
    needSetNumberPhone() {
      return getSettings('check_phone_number')
          && this.hasDriver
          && !this.hasPhoneNumber
          && store.state.connection !== undefined;
    }
  },
}
</script>

<template>
  <medic-select/>
  <div class="home">
    <div v-if="terminalIsLocked" style="width: 100%; justify-content: center; display: flex">
      <div class="driver-form__not-found animate__animated animate__fadeInUp">
        Работа терминала приостановлена.
        Проверьте подключение к интернету
      </div>
    </div>
    <div v-else class="driver-form">
      <div v-if="inspection.driver_fio" class="driver-form__title animate__animated animate__fadeInDown d-2">
        {{ inspection.driver_fio }}
      </div>
      <div v-else class="driver-form__title animate__animated animate__fadeInDown d-2">
        Введите ваш идентификатор
      </div>
      <div class="driver-form__input">
        <input type="number"
               class="animate__animated animate__fadeIn d-5"
               v-model="driver_id"
               @input="checkDriver"/>
      </div>

      <input-personal-number-form
          @password=" (inputPassword) => updateDriverId(inputPassword)"
      />
      <button v-if="hasDriver"
              @click="start"
              class="btn animate__animated animate__fadeInUp">
        начать осмотр
      </button>
      <div v-else-if="error"
           class="driver-form__not-found animate__animated animate__fadeInUp">{{ error }}
      </div>
    </div>
  </div>
</template>