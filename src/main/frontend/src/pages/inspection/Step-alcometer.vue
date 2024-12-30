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

export default {
  data() {
    return {
      interval: null,
      seconds: 5,
      needRetry: false,
      showRetry: false,
      statusAlcometer: "",
      statusPrev: "prew",
      statusNow: "now",
      recording: false
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
        const data = await makeMedia(this.$store.state.inspection.driver_id);
        this.inspection.photo = data?.photo;
        this.inspection.video = data?.video;
        console.log("start media")
      }
    },
    async stopWebCam() {
      if (this.recording) {
        this.recording = false;
        await stopMedia(this.$store.state.inspection.driver_id);
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

      if (this.checkRetry(result)) {
        this.inspection.alcometer_result = result;
        await this.retry();
        return;
      }
      this.inspection.alcometer_result = Number(result) || 0;
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
  </div>
</template>
