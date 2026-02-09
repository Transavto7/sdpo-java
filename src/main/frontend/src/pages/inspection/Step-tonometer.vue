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
      showErrorModal: false,
      attemptCount: 0,
    }
  },
  async mounted() {
    // Запускаем измерение
    this.startMeasurement();

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
    },
    driver() {
      return this.$store.state.driver || {};
    }
  },
  methods: {
    checkPressureThresholds(systolic, diastolic, pulse) {
      // Если у водителя есть индивидуальные пороги
      if (this.driver.pressure_systolic) {
        // Проверяем по логике из OfflineInspectionSaver.java (строки 88-97)
        if (
          systolic > this.driver.pressure_systolic ||
          diastolic > this.driver.pressure_diastolic ||
          pulse > this.driver.pulse_upper ||
          pulse < this.driver.pulse_lower
        ) {
          return false; // Показатели вышли за пороги
        }
      } else {
        // Если нет индивидуальных порогов, используем дефолтную проверку
        // Как в OfflineInspectionSaver.java (строки 100-103)
        if (systolic > 150) {
          return false;
        }
      }
      return true; // Показатели в норме
    },
    startMeasurement() {
      // Останавливаем предыдущий interval если он был
      if (this.interval) {
        clearInterval(this.interval);
      }

      // Запускаем новый interval для опроса тонометра
      this.interval = setInterval(async () => {
        const result = await getPressure();

        if (result === undefined || result === null) {
          return;
        }

        if (result === 'next') {
          return;
        }

        let pulse = null;
        let systolic = null;
        let diastolic = null;

        if (result?.pulse) {
          pulse = result.pulse;
          this.inspection.pulse = pulse;
        }

        if (result?.systolic || result?.diastolic) {
          systolic = result.systolic;
          diastolic = result.diastolic;
          this.inspection.tonometer = systolic + '/' + diastolic;
        }

        clearInterval(this.interval);

        // Проверяем пороги давления и пульса
        const isThresholdsOk = this.checkPressureThresholds(systolic, diastolic, pulse);

        // Если показатели не в норме и это первая попытка - показываем модалку
        if (!isThresholdsOk && this.attemptCount === 0) {
          this.showErrorModal = true;
          return;
        }

        // Иначе переходим на следующий шаг
        this.$router.push({name: 'step-thermometer'});
      }, 1000);
    },
    retryMeasurement() {
      // Закрываем модалку
      this.showErrorModal = false;

      // Очищаем результаты измерения
      this.inspection.pulse = null;
      this.inspection.tonometer = null;

      // Увеличиваем счетчик попыток
      this.attemptCount++;

      // Запускаем новое измерение
      this.startMeasurement();
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
    <!-- Модальное окно помощи -->
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

    <!-- Модальное окно ошибки измерения -->
    <Modal
        :visible="showErrorModal"
        :show-close-button="false"
        :close-on-overlay-click="false">

      <div class="tonometer-error">
        <!-- Иконка ошибки -->
        <div class="tonometer-error__icon-wrapper">
          <div class="tonometer-error__icon">
            <i class="ri-error-warning-line"></i>
          </div>
        </div>

        <!-- Текст и кнопка -->
        <div class="tonometer-error__content">
          <h2 class="tonometer-error__title">Ошибка измерения</h2>
          <button @click="retryMeasurement" class="btn blue">Перемерить</button>
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

.tonometer-error {
  display: flex;
  flex-direction: column;
  gap: 30px;
  align-items: center;
  text-align: center;
}

.tonometer-error__icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}

.tonometer-error__icon {
  color: #c53936;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 6em;
  line-height: 1;
}

.tonometer-error__content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}

.tonometer-error__title {
  margin: 0;
  font-size: 2em;
  font-weight: 500;
  color: #3c495c;
}
</style>