<script>
import InputPersonalNumberForm from "@/components/InputPersonalNumberForm";
import { getDriver } from '@/helpers/api';
import { useToast } from "vue-toastification";

export default {
  components: {InputPersonalNumberForm },
  data() {
    return {
      driver_id: '',
      driver: {},
      error: '',
      toast: useToast(),
      loading: false,
    }
  },
  methods: {
    async start() {
      this.$emit('success-auth', this.driver)
    },
    updateDriverId(inputPassword) {
      this.driver_id = inputPassword;
    },
    async checkDriver() {
      this.error = null;

      if (this.driver_id.length < 6) {
        return;
      }

      this.loading = true;
      try {
        const driver = await getDriver(this.driver_id);
        if (driver) {
          this.driver = driver
          this.error = null;
          await this.start()
        } else {
          this.error = 'Водитель не найден';
        }
      } catch (error) {
        this.error = error.response?.data?.message || 'Неизвестная ошибка';
      }

      this.loading = false;
    }
  },
  watch: {
    driver_id: function (val) {
      this.checkDriver();
      if (!this.hasDriver) this.processingApproval = false;
    }
  },
  computed: {
    config() {
      return this.$store.state.config;
    },
    hasDriver() {
      return this.driver.driver_id && !this.error
    }
  },
  mounted() {
  }
}
</script>

<template>
  <div class="home">
    <div class="driver-form">
      <div v-if="driver.driver_fio" class="driver-form__title animate__animated animate__fadeInDown d-2">
        {{ driver.driver_fio }}
      </div>
      <div v-else class="driver-form__title animate__animated animate__fadeInDown d-2">
        Введите ваш идентификатор
      </div>
      <div class="driver-form__input">
        <input type="number" class="animate__animated animate__fadeIn d-5" v-model="driver_id" @input="checkDriver" />
      </div>

      <input-personal-number-form
          @password=" (inputPassword) => updateDriverId(inputPassword)"
      />
      <div v-if="error" class="driver-form__not-found animate__animated animate__fadeInUp">{{ error }}</div>
    </div>
  </div>
</template>