<script>
import {getTemp} from '@/helpers/thermometer';
import Modal from "@/components/Modal.vue";

export default {
  components: {Modal},
  data() {
    return {
      interval: null,
      error: {
        show: false,
        message: true,
      },
      showErrorModal: false,
      attemptCount: 0,
      lastResult: null,
    }
  },
  methods: {
    checkTemperatureThresholds(temp) {
      // Проверка пороговых значений температуры
      if (temp < 35 || temp >= 37) {
        return false;
      }
      return true;
    },
    startMeasurement() {
      this.interval = setInterval(async () => {
        let result = await getTemp();

        if (result === undefined || result === null) {
          return;
        }

        if (result === 'next') {
          return;
        }

        result = Number(result) || 36.6;
        this.lastResult = result;

        // Проверяем пороги
        const thresholdsOk = this.checkTemperatureThresholds(result);

        if (!thresholdsOk) {
          clearInterval(this.interval);
          this.attemptCount++;

          // Если это первая попытка - показываем модалку
          if (this.attemptCount === 1) {
            this.showErrorModal = true;
            return;
          }

          // Если это вторая попытка - продолжаем независимо от результата
          this.inspection.t_people = result;
          this.$router.push({name: 'step-alcometer'});
          return;
        }

        // Если пороги в норме - продолжаем
        this.inspection.t_people = result;
        clearInterval(this.interval);
        this.$router.push({name: 'step-alcometer'});
      }, 1000);
    },
    retryMeasurement() {
      this.showErrorModal = false;
      this.lastResult = null;
      this.startMeasurement();
    }
  },
  async mounted() {
    this.startMeasurement();
  },
  unmounted() {
    clearInterval(this.interval);
  },
  computed: {
    inspection() {
      return this.$store.state.inspection;
    },
    system() {
      return this.$store.state.config?.system || {};
    }
  }
}
</script>

<template>
  <div class="step-4__outer">
    <div class="step-4">
      <h3 class="animate__animated animate__fadeInUp">Измерение температуры тела</h3>
      <div v-if="error.show" class="alert alert-danger">
        {{ error.message }}
      </div>
      <img class="animate__animated animate__fadeInUp d-1" src="@/assets/images/pirometer2.png">
    </div>

    <div class="step-buttons">
      <button @click="$router.push({ name: 'step-tonometer' })" class="btn opacity blue">Назад</button>
      <button @click="$router.push({ name: 'step-alcometer' })"
              v-if="JSON.parse(system.thermometer_skip)" class="btn">Продолжить
      </button>
    </div>

    <!-- Модальное окно ошибки измерения -->
    <Modal
        :visible="showErrorModal"
        :show-close-button="false"
        :close-on-overlay-click="false">

      <div class="thermometer-error">
        <!-- Иконка ошибки -->
        <div class="thermometer-error__icon-wrapper">
          <div class="thermometer-error__icon">
            <i class="ri-error-warning-line"></i>
          </div>
        </div>

        <!-- Текст и кнопка -->
        <div class="thermometer-error__content">
          <h2 class="thermometer-error__title">Ошибка измерения</h2>
          <button @click="retryMeasurement" class="btn blue">Перемерить</button>
        </div>
      </div>

    </Modal>
  </div>
</template>

<style lang="scss" scoped>
.alert {
  position: relative;
  padding: .75rem 1.25rem;
  margin-bottom: 1rem;
  border: 1px solid transparent;
  border-radius: .25rem;
  width: 50%;
  height: 5%;
  justify-content: center;
}

.alert-danger {
  color: #721c24;
  background-color: #f8d7da;
  border-color: #f5c6cb;
}

.thermometer-error {
  display: flex;
  flex-direction: column;
  gap: 30px;
  align-items: center;
  text-align: center;
}

.thermometer-error__icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}

.thermometer-error__icon {
  color: #c53936;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 6em;
  line-height: 1;
}

.thermometer-error__content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}

.thermometer-error__title {
  margin: 0;
  font-size: 2em;
  font-weight: 500;
  color: #3c495c;
}
</style>