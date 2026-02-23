<script>
import {
  closeAlcometer, closeAlcometrSocket,
  enableModeFromSystemConfig,
  enableSlowModeAlcometer,
  getAlcometerResult
} from '@/helpers/alcometer';
import {makeMedia, stopMedia} from '@/helpers/camera';
import {getSettings} from "@/helpers/settings";
import {closeDriverPhoto} from "@/helpers/api/api";
import Modal from "@/components/Modal.vue";

export default {
  components: {Modal},
  data() {
    return {
      interval: null,
      seconds: 5,
      needRetry: false,
      showRetry: false,
      statusAlcometer: "",
      statusPrev: "prew",
      statusNow: "now",
      recording: false,
      showErrorModal: false,
      attemptCount: 0,
      lastResult: null,
    }
  },
  watch:  {
    statusNow: async function () {
      if (this.isChangeStatus("WAIT")) {
        await this.runWebCam()

      }
      if ((this.isChangeStatus("RESULT") || this.isChangeStatus("ERROR") || this.isChangeStatus("FREE") || this.isChangeStatus("STOP")) && this.recording) {
        await this.stopWebCam()
      }
    }
  },
  methods: {
    isChangeStatus(status) {
       return (this.statusNow === status && this.statusPrev !== status)
    },
    connect() {
      this.connection = new WebSocket("ws://localhost:8080/device/alcometer/status")
      this.connection.onmessage = async (event) => {
        this.statusPrev = this.statusNow;
        this.statusNow = event.data;
      }

      this.connection.onopen = (event) => {
        console.log("Successfully connected to the echo websocket server...")
      }

      this.connection.error = (error) => {
        console.log(error);
      }
    },
    async disconnect() {
      console.log('Disconnect websoket server');
      await closeAlcometrSocket();
    },


    async runWebCam() {
      if (!this.recording) {
        this.recording = true;
        const data = await makeMedia(this.identifier);
        this.inspection.photo = data?.photo;
        this.inspection.video = data?.video;
        console.log("start media")
      }
    },
    async stopWebCam() {
      if (this.recording) {
        this.recording = false;
        await stopMedia(this.identifier);
        console.log("stop media")
      }
    },
    nextStep() {
      this.$router.push({name: 'step-sleep'});
    },
    async prevStep() {
      this.$router.push({name: 'step-thermometer'});
    },
    async retry() {
      this.needRetry = true;
      setTimeout(() => {
        this.showRetry = true;
      }, 3000);
      await enableSlowModeAlcometer();
      await closeAlcometer();
      this.runCountdown();
    },
    hasResult(result) {
      return !(result === undefined || result === null || result === 'next');

    },
    hasError(result) {
      return result === "error";
    },
    checkRetry(result) {
      return getSettings('alcometer_fast') && getSettings('alcometer_retry') && Number(result) > 0 && !this.needRetry;
    },
    runCountdown() {
      this.seconds = 5;
      this.timerInterval = setInterval(() => {
        this.seconds--;
        if (this.seconds < 1) {
          clearInterval(this.timerInterval);
        }
      }, 1000);
    },
    async retryMeasurement() {
      // Закрываем модалку
      this.showErrorModal = false;
      this.lastResult = null;

      // Если включен режим количественного замера - переключаемся на него
      if (getSettings('alcometer_retry')) {
        await enableSlowModeAlcometer();
      }

      // Сбрасываем алкометр
      await closeAlcometer();

      // Перезапускаем таймер обратного отсчета
      this.runCountdown();

      // Перезапускаем интервал опроса результата
      this.requestInterval = setInterval(async () => {
        const result = await getAlcometerResult();

        if (!this.hasResult(result)) {
          return;
        }

        // Если получена ошибка от устройства - продолжаем ждать
        if (this.hasError(result)) {
          return;
        }

        const resultValue = Number(result) || 0;
        this.lastResult = resultValue;

        // На второй попытке с положительным результатом переходим на результат
        clearInterval(this.requestInterval);
        this.inspection.alcometer_result = resultValue;
        this.inspection.alcometer_mode = getSettings('alcometer_fast') ? '0' : '1';
        this.$router.push({name: 'step-result'});
      }, 700);
    },
  },
  async mounted() {
    this.connect()
    // await this.runWebCam();
    this.runCountdown()

    this.requestInterval = setInterval(async () => {
      const result = await getAlcometerResult();

      if (!this.hasResult(result)) {
        return;
      }

      // Если получена ошибка от устройства - продолжаем ждать
      if (this.hasError(result)) {
        return;
      }

      const resultValue = Number(result) || 0;
      this.lastResult = resultValue;

      // В ручном режиме - старое поведение
      if (getSettings('manual_mode')) {
        if (this.checkRetry(result)) {
          this.inspection.alcometer_result = result;
          await this.retry();
          return;
        }
        this.inspection.alcometer_result = resultValue;
        this.inspection.alcometer_mode = getSettings('alcometer_fast') ? '0' : '1';
        this.nextStep();
        return;
      }

      // Проверяем пороги алкометра (> 0 = положительный результат)
      if (resultValue > 0) {
        clearInterval(this.requestInterval);
        this.attemptCount++;

        // Если это первая попытка - показываем модалку
        if (this.attemptCount === 1) {
          this.inspection.alcometer_result = resultValue;
          this.showErrorModal = true;
          return;
        }

        // Если это вторая попытка с положительным результатом - переходим на результат
        this.inspection.alcometer_result = resultValue;
        this.inspection.alcometer_mode = getSettings('alcometer_fast') ? '0' : '1';
        this.$router.push({name: 'step-result'});
        return;
      }

      // Если результат отрицательный (0) - продолжаем
      this.inspection.alcometer_result = resultValue;
      this.inspection.alcometer_mode = getSettings('alcometer_fast') ? '0' : '1';
      this.nextStep();
    }, 700);
  },
  unmounted() {
    if (this.recording) {
      this.stopWebCam();
    }
    this.disconnect()
    enableModeFromSystemConfig(getSettings('alcometer_fast'));
    clearInterval(this.requestInterval);
    clearInterval(this.timerInterval);
  },
  computed: {
    inspection() {
      return this.$store.state.inspection;
    },
    identifier() {
      return this.$store.state.inspection.driver_id || this.$store.state.inspection.person_id;
    },
    system() {
      return this.$store.state.config?.system || {};
    },
    status() {
      if (this.seconds === 5) {
        return 'через ' + this.seconds + ' секунд';
      } else if (this.seconds === 1) {
        return 'через ' + this.seconds + ' секунду';
      } else if (this.seconds < 1) {
        return ' прямо сейчас!';
      } else {
        return 'через ' + this.seconds + ' секунды';
      }
    }
  },
}
</script>
<template>
  <div class="step-alcometer__outer">

    <div v-if="!showRetry" class="step-alcometer">
      <h3 class="animate__animated animate__fadeInDown">Проверка на алкоголь</h3>

      <div  class="step-alcometer__items">
        <div class="step-alcometer__item animate__animated animate__fadeInUp d-1">
          <span>1</span>
          <img style="padding-right: 20px" width="55" src="@/assets/images/fast.png">
          <label>Проверьте, что вставлена воронка</label>
        </div>
        <div class="step-alcometer__item animate__animated animate__fadeInUp d-2">
          <span>2</span>
          <img width="100" src="@/assets/images/alco_guide_2.png">
          Держите алкотестер на расстоянии 2-3 см от рта
        </div>
        <div class="step-alcometer__text  animate__animated animate__fadeInUp d-2">
          Дождитесь ГОТОВ на экране алкометра<br><br>
          Начните дуть с умеренной силой до окончания<br>
          звукового сигнала.<br><br>
          <p v-if="!needRetry">Дуйте {{ status }}</p>
          <p v-if="needRetry" style="width: 100%">Результат: Положительный</p>
        </div>
      </div>
      <p v-if="!showRetry" class="alert red">
        <i class="ri-alarm-warning-fill"></i>
        НЕ ПРИКАСАТЬСЯ К АЛКОТЕСТЕРУ ГУБАМИ
      </p>
    </div>
    <div v-if="showRetry" class="step-alcometer">
      <h3  class="animate__animated animate__fadeInDown">Количественное определение алкоголя</h3>
      <div  class="step-alcometer__items">
        <div class="step-alcometer__item animate__animated animate__fadeInUp d-1">
          <span style="min-height: 30px">3</span>
          <img style="padding-right: 20px" width="80" src="@/assets/images/precise.png">
          Снимите воронку , установите мундштук
        </div>
        <div class="step-alcometer__text  animate__animated animate__fadeInUp d-2">
          Снимите мундштук-воронка<br><br>
          Установите индивидуальный мундштук<br><br>
          Дождитесь ГОТОВ на экране алкометра<br><br>
          Начните дуть с умеренной силой до<br>
          окончания звукового сигнала.<br><br>
          Снимите индивидуальный мундштук<br><br>
          Установите мундштук-воронка<br><br>
        </div>
      </div>
    </div>
    <div class="step-buttons">
      <button @click="prevStep()" class="btn opacity blue">Назад</button>
      <button @click="nextStep()" v-if="JSON.parse(system.alcometer_skip)" class="btn">Продолжить</button>
    </div>

    <!-- Модальное окно ошибки измерения -->
    <Modal
        :visible="showErrorModal"
        :show-close-button="false"
        :close-on-overlay-click="false">

      <div class="alcometer-error">
        <!-- Иконка ошибки -->
        <div class="alcometer-error__icon-wrapper">
          <div class="alcometer-error__icon">
            <i class="ri-error-warning-line"></i>
          </div>
        </div>

        <!-- Текст и кнопка -->
        <div class="alcometer-error__content">
          <h2 class="alcometer-error__title">Повторите замер!</h2>
          <p v-if="lastResult !== null" class="alcometer-error__result">
            Выявлено отклонение от нормы: <strong>{{ lastResult.toFixed(2) }}</strong>
          </p>
          <p v-if="JSON.parse(system.alcometer_retry)" class="alcometer-error__info">
            При повторном замере будет использован количественный режим
          </p>
          <p v-else class="alcometer-error__info">
            Повторный замер будет в том же режиме
          </p>
          <button @click="retryMeasurement" class="btn blue">Начать</button>
        </div>
      </div>

    </Modal>
  </div>
</template>

<style lang="scss" scoped>
.alcometer-error {
  display: flex;
  flex-direction: column;
  gap: 30px;
  align-items: center;
  text-align: center;
}

.alcometer-error__icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}

.alcometer-error__icon {
  color: #c53936;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 6em;
  line-height: 1;
}

.alcometer-error__content {
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: center;
}

.alcometer-error__title {
  margin: 0;
  font-size: 2em;
  font-weight: 500;
  color: #3c495c;
}

.alcometer-error__result {
  margin: 0;
  font-size: 1.3em;
  color: #3c495c;
}

.alcometer-error__info {
  margin: 0;
  font-size: 1.1em;
  color: #666;
  line-height: 1.6;
  max-width: 400px;
}
</style>
