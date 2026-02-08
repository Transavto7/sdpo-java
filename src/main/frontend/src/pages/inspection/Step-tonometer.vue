<script>
import {getPressure} from '@/helpers/tonometer';
import {disableTonometer} from '@/helpers/tonometer';
import Modal from "@/components/Modal.vue";

export default {
  components: {Modal},
  data() {
    return {
      interval: null,
      showModal: false,
      showHelpButton: false,
      helpButtonTimer: null,
    }
  },
  async mounted() {
    this.interval = setInterval(async () => {
      const result = await getPressure();

      if (result === undefined || result === null) {
        return;
      }

      if (result === 'next') {
        return;
      }

      if (result?.pulse) {
        this.inspection.pulse = result.pulse;
      }

      if (result?.systolic || result?.diastolic) {
        this.inspection.tonometer = result.systolic + '/' + result.diastolic;
      }

      clearInterval(this.interval);
      this.$router.push({name: 'step-thermometer'});
    }, 1000);

    // Показать кнопку помощи через 2 минуты
    this.helpButtonTimer = setTimeout(() => {
      this.showHelpButton = true;
    }, 120000);
  },
  unmounted() {
    clearInterval(this.interval);
    clearTimeout(this.helpButtonTimer);
    disableTonometer();
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
  <div class="step-3__outer">
    <div class="step-3">
      <h3 class="animate__animated animate__fadeInDown">Измерение артериального давления</h3>
      <div class="step-3__items">
        <div class="step-3__item animate__animated animate__fadeInDown d-1">
          <span>1</span>
          Закрепите манжету тонометра на левом плече на 2-3 см выше сгиба локтя
        </div>
        <div class="step-3__item animate__animated animate__fadeInDown d-2">
          <span>2</span>
          Положите руку на стол
        </div>
        <div class="step-3__item animate__animated animate__fadeInDown d-3">
          <span>3</span>
          Убедитесь, что манжета находится на уровне сердца
        </div>
        <div class="step-3__item animate__animated animate__fadeInDown d-4">
          <span>4</span>
          Нажмите кнопку СТАРТ на тонометре
        </div>
      </div>

      <div class="step-3__subtext animate__animated animate__fadeInDown d-5">
        Не двигайтесь, пока идет измерение.<br>
        После окончания измерения снимите манжету
      </div>
    </div>

    <div class="step-buttons">
      <button @click="$router.push({ name: 'step-ride' })" class="btn opacity blue">Назад</button>
      <button @click="showModal=true" v-if="showHelpButton" class="btn grey">Не работает тонометр?</button>
      <button @click="$router.push({ name: 'step-thermometer' })" v-if="JSON.parse(system.tonometer_skip)" class="btn">
        Продолжить
      </button>
    </div>
    <Modal
        :visible="showModal"
        :show-close-button="true"
        @close="showModal = false">

      <div class="tonometer-help">
        <!-- Левый блок с иконкой -->
        <div class="tonometer-help__icon-wrapper">
          <div class="tonometer-help__icon">
            <i class="ri-error-warning-line"></i>
          </div>
        </div>

        <!-- Правый блок с инструкциями -->
        <div class="tonometer-help__content">
          <p class="tonometer-help__text">
            Выключите провод питания и вытащите батарейки из тонометра на 20 секунд. Тонометр сбросится.
          </p>
          <p class="tonometer-help__text">
            Затем подключите питание и повторите замер, нажав на кнопку "Старт".
          </p>
          <button @click="$router.push('/help')" class="btn opacity blue">Если сброс питания не помог, позвоните нам!</button>
        </div>
      </div>

    </Modal>
  </div>
</template>
<style scoped lang="scss">
.start-buttons {
  display: flex;
  flex-direction: row;
  justify-content: start;
  width: 100%;
  gap: 2rem;
}

.tonometer-help {
  display: flex;
  flex-direction: row;
  gap: 30px;
  align-items: center;
}

.tonometer-help__icon-wrapper {
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

.tonometer-help__icon {
  color: #c53936;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 5em;
  line-height: 1;
}

.tonometer-help__content {
  display: flex;
  flex-direction: column;
  gap: 15px;
  flex: 1;
}

.tonometer-help__text {
  margin: 0;
  font-size: 1.1em;
  line-height: 1.6;
  color: #3c495c;
}
</style>