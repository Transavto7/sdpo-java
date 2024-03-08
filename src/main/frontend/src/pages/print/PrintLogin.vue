<script>
import InputPersonalNumberForm from "@/components/InputPersonalNumberForm";
import { getDriver } from '@/helpers/api';
import { useToast } from "vue-toastification";
import MedicSelect from '@/components/MedicSelect.vue';
import ApprovalModal from '@/components/ApprovalModal.vue';

export default {
   name: 'PrintLogin',
  components: { MedicSelect, ApprovalModal, InputPersonalNumberForm },
  data() {
    return {
      driver_id: '',
      error: '',
      toast: useToast(),
      loading: false,
      processingApproval : false,
      visibleApprovalDoc : false,
    }
  },
  methods: {
    async start() {
      this.$router.push('/print/index');
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
          await this.start()
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
      if (!this.hasDriver) this.processingApproval = false;
    }
  },
  computed: {
    config() {
      return this.$store.state.config;
    },
    inspection() {
      return this.$store.state.inspection;
    },
    hasDriver() {
      return this.inspection.driver_id && !this.error
    }
  },
  mounted() {
    this.$refs.numbers.querySelectorAll('button').forEach((button, index) => {
      button.classList.add('animate__animated');
      button.classList.add('animate__fadeInUp');
      button.style.setProperty('animation-delay', `${0.6 + (0.05 * index)}s`);
    });
  }
}
</script>

<template>
  <div class="home">
    <div class="driver-form">
      <div v-if="inspection.driver_fio" class="driver-form__title animate__animated animate__fadeInDown d-2">
        {{ inspection.driver_fio }}
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